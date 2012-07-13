package sodekovs.bikesharing.disposition;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.micro.MicroAgent;

import java.util.LinkedList;
import java.util.List;

import sodekovs.old.bikesharing.datenkonvertierung.Datensammler;
import sodekovs.old.bikesharing.fahrrad.Fahrrad;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;

/**
 * Agent für das Hin- und herfahren von Rädern
 * 
 * @author dagere
 * 
 */
public abstract class DispositionsAgent extends MicroAgent
{
	public static final String DISPOSITIONSAGENT = "dispositionsagent";
	public static final String ENGPASS = "dispositionEngpassagent";
	public static final String SCHWELLWERT = "dispositionSchwellwertagent";
	public static final String VERGANGENHEIT = "dispositionVergangenheitsagent";
	
	public static String ausgabedatei = "E:/Workspaces/Jadex/Jadex Test Instanz/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/DispositionsFahrten.csv";

	protected Vector2Double _ziel;
	private Vector2Double _position;
	protected ISpaceObject _avatar;
	protected FahrradVerleihStation _vonStation;
	protected FahrradVerleihStation _zuStation;
	protected List<Fahrrad> _raeder;
	protected IComponentStep verschiebSchritt;
	IComponentStep zuZielBewegen;

	public DispositionsAgent()
	{
		_raeder = new LinkedList<Fahrrad>();
	}

	public IFuture<Void> executeBody()
	{
		System.out.println("Starte Dispositionsagent " + this.getClass());

		// _avatar = SelbstBewegPlan.gibAvatar(getParent(), null);
		IExternalAccess paexta = (IExternalAccess) getParentAccess();
		paexta.getExtension("simulationsspace").addResultListener(
				createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						Grid2D space = (Grid2D) result;
						_avatar = space.getAvatar(getComponentDescription());

						verschiebSchritt = new IComponentStep()
						{

							@Override
							public IFuture<Void>  execute(IInternalAccess arg0)
							{
								verschieben();
								return IFuture.DONE;
							}
						};

						zuZielBewegen = new IComponentStep()
						{

							@Override
							public IFuture<Void> execute(IInternalAccess arg0)
							{
								zuZielBewegen(_ziel != null ? _ziel
										: new Vector2Double(1.0, 1.0));
								return IFuture.DONE;
							}
						};

						waitForTick(new IComponentStep()
						{

							@Override
							public IFuture<Void>  execute(IInternalAccess arg0)
							{
								warten();
								return IFuture.DONE;
							}
						});
					}
				}));
		return IFuture.DONE;

	}

	protected void zuZielBewegen(Vector2Double ziel)
	{
		_ziel = ziel;
		// System.out.println("Bewege zu: " + _ziel);

		_position = (Vector2Double) _avatar
				.getProperty(Space2D.PROPERTY_POSITION);
		Vector2Double richtung = (Vector2Double) _ziel.copy().subtract(
				_position);

		IVector2 _geschwindigkeitsvektor = (Vector2Double) new Vector2Double(
				richtung.getXAsDouble(), richtung.getYAsDouble()).normalize();

		_position = (Vector2Double) _position.add(_geschwindigkeitsvektor);

		_avatar.setProperty(Space2D.PROPERTY_POSITION, _position);

		if (_position.getDistance(_ziel).getAsDouble() < 1.0)
		{
			_avatar.setProperty(Space2D.PROPERTY_POSITION, _ziel.copy());
			_position = (Vector2Double) _ziel.copy();
		}

		if (_position.getDistance(_ziel).getAsDouble() < 0.1)
		{
			// System.out.println("An Ziel angekommen, disponiere");
			dispositionsProzess();
		}
		else
		{
			// System.out.println("Warte Tick für ZIelbewegung");
			waitForTick(zuZielBewegen);
		}
	}

	/**
	 * Soll die Disposition von Fahrrädern beginnen
	 */
	protected abstract void dispositionsProzess();

	/**
	 * Soll die Verteilung der Fahrräder regeln, wenn der Agent schon am Ziel
	 * ist
	 */
	public abstract void verschieben();

	protected void wegNehmen(FahrradVerleihStation station, int leihAnzahl)
	{
		for (int i = 0; i < leihAnzahl; i++) // entscheidend: hier wird die
		// Wegnehmmenge geprüft
		{
			_raeder.add(station.leihFahrrad());
		}
		_avatar.setProperty(FahrradVerleihStation.FAHRRADANZAHL, new Integer(
				_raeder.size()));
		
		speichereDisposition( _avatar.getId(), Zeitverwaltung.gibInstanz().gibZeitString(), - _raeder.size(), station);
	}
	
	
	protected synchronized void speichereDisposition(Object id,
			String zeitstring, int anz, FahrradVerleihStation station)
	{
		Datensammler.gibDatensammler( ausgabedatei ).speichereWert(1, zeitstring + ", " +
				id + ", " + station.gibName() + "," + station.gibPosition() + ", " + anz);
		
	}

	protected void zurueckGeben( FahrradVerleihStation station )
	{
		
		speichereDisposition( _avatar.getId(), Zeitverwaltung.gibInstanz().gibZeitString(), _raeder.size(), station);
		while (!_raeder.isEmpty())
		{
			Fahrrad rad = _raeder.get(0);
			_raeder.remove(0);
			station.gibRadZurueck(rad);
		}
//		 System.out.println("Hinpacken(dannach)!" + station);

		_avatar.setProperty(FahrradVerleihStation.FAHRRADANZAHL, new Integer(_raeder.size()));
	}

	/**
	 * Wartet eine bestimmte Zeit (wenn es vor 8 Uhr ist bis 8 Uhr, wenn es nach
	 * 19 Uhr ist bis 24 Uhr, ansonsten 10 Minuten); anschließend wird wieder
	 * eine Handlung ausgeführt
	 */
	protected void warten()
	{
		warten(10);
	}
	
	/**
	 * Wartet eine bestimmte Zeit (wenn es vor 8 Uhr ist bis 8 Uhr, wenn es nach
	 * 19 Uhr ist bis 24 Uhr, ansonsten die angegebene Zeit Minuten); anschließend 
	 * wird wieder eine Handlung ausgeführt
	 */
	protected void warten( int zeit)
	{
		long wartezeit = zeit;
		if (Zeitverwaltung.gibInstanz().gibTageszeit() < 8 * 60)
		{
			wartezeit = 8 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit();
		}
		if (Zeitverwaltung.gibInstanz().gibTageszeit() > 19 * 60)
		{
			wartezeit = 24 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit();
		}
		System.out.println("Wartezeit Dispo: " + wartezeit);

		waitFor(wartezeit * 100, verschiebSchritt);
	}
}
