package sodekovs.old.bikesharing.fahrrad;

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

import sodekovs.old.bikesharing.zeit.Zeitverwaltung;

/**
 * Agent für das Hin- und herfahren von Rädern
 * 
 * @author dagere
 * 
 */
public class DispositionsAgent extends MicroAgent
{
	public static final String DISPOSITIONSAGENT = "dispositionsagent";

	private Vector2Double _ziel;
	private Vector2Double _position;
	private ISpaceObject _avatar;
	private FahrradVerleihStation _vonStation;
	private FahrradVerleihStation _zuStation;
	private List<Fahrrad> _raeder;
	IComponentStep r;
	IComponentStep zuZielBewegen;

	public DispositionsAgent()
	{
		_raeder = new LinkedList<Fahrrad>();
	}

	public IFuture<Void> executeBody()
	{
		System.out.println("Starte Dispositionsagent");

		// _avatar = SelbstBewegPlan.gibAvatar(getParent(), null);
		IExternalAccess paexta = (IExternalAccess) getParentAccess();
		paexta.getExtension("simulationsspace").addResultListener(createResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result)
			{
				Grid2D space = (Grid2D) result;
				_avatar = space.getAvatar(getComponentDescription());

				r = new IComponentStep() {

					@Override
					public IFuture<Void>  execute(IInternalAccess arg0)
					{
						verschieben();
						return IFuture.DONE;
					}
				};

				zuZielBewegen = new IComponentStep() {

					@Override
					public IFuture<Void> execute(IInternalAccess arg0)
					{
						zuZielBewegen();
						return IFuture.DONE;
					}
				};

				waitForTick(new IComponentStep() {

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

	private void zuZielBewegen()
	{
		// System.out.println("Bewege zu: " + _ziel);

		_position = (Vector2Double) _avatar.getProperty(Space2D.PROPERTY_POSITION);
		Vector2Double richtung = (Vector2Double) _ziel.copy().subtract(_position);

		IVector2 _geschwindigkeitsvektor = (Vector2Double) new Vector2Double(richtung.getXAsDouble(), richtung
				.getYAsDouble()).normalize();

		_position = (Vector2Double) _position.add(_geschwindigkeitsvektor);

		_avatar.setProperty(Space2D.PROPERTY_POSITION, _position);

		if (_position.getDistance(_ziel).getAsDouble() < 1.0)
		{
			_avatar.setProperty(Space2D.PROPERTY_POSITION, _ziel);
			_position = (Vector2Double) _ziel.copy();
		}

		if (_position.getDistance(_ziel).getAsDouble() < 0.1)
		{
			dispositionsProzess();
		}
		else
		{
			// System.out.println("Warte Tick");
			waitForTick(zuZielBewegen);
		}
	}

	private void dispositionsProzess()
	{
		if (_ziel.equals(_vonStation.gibPosition()))
		{
			int leihAnzahl = _vonStation.gibFahrradAnzahl() / 2;
			for (int i = 0; i < leihAnzahl; i++) // entscheidend: hier wird die
													// Wegnehmmenge geprüft
			{
				_raeder.add(_vonStation.leihFahrrad());
			}
			_avatar.setProperty(FahrradVerleihStation.FAHRRADANZAHL, new Integer(_raeder.size()));

			_ziel = (Vector2Double) _zuStation.gibPosition().copy();

			zuZielBewegen();
		}
		else if (_ziel.equals(_zuStation.gibPosition()))
		{
			while (!_raeder.isEmpty())
			{
				Fahrrad rad = _raeder.get(0);
				_raeder.remove(0);
				_zuStation.gibRadZurueck(rad);
			}
			// System.out.println("Hinpacken(dannach)!" + _zuStation);

			_avatar.setProperty(FahrradVerleihStation.FAHRRADANZAHL, new Integer(_raeder.size()));

			verschieben();
		}
	}

	private void verschieben()
	{
		_vonStation = FahrradVerleihStationen.gibInstanz().gibStationMitMeistenRaedern();
		_zuStation = FahrradVerleihStationen.gibInstanz().gibStationMitWenigstenRaedern();
		// System.out.println("Verschieben!" + _vonStation + " zu " +
		// _zuStation);
		if (_zuStation.gibFahrradAnzahl() * 2.5 > _vonStation.gibFahrradAnzahl())
		{
			// System.out.println("Verschieben verschoben");
			_vonStation = null;
			_zuStation = null;
			warten();
		}
		else
		{
			_ziel = (Vector2Double) _vonStation.gibPosition().copy();
			zuZielBewegen();
		}
	}

	private void warten()
	{
		long wartezeit = 10;
		if (Zeitverwaltung.gibInstanz().gibTageszeit() < 8 * 60)
		{
			wartezeit = 8 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit();
		}
		if (Zeitverwaltung.gibInstanz().gibTageszeit() > 19 * 60)
		{
			wartezeit = 24 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit();
		}
		System.out.println("Wartezeit: " + wartezeit);

		waitFor(wartezeit * 100, r);
	}
}
