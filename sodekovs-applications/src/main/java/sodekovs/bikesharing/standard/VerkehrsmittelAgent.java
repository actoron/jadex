package sodekovs.bikesharing.standard;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.FIPAMessageType;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.micro.MicroAgent;

import java.util.HashMap;
import java.util.Map;

import sodekovs.bikesharing.bahnverwaltung.BahnStation;
import sodekovs.bikesharing.bahnverwaltung.LinienInformationen;
import sodekovs.bikesharing.container.Weg;
import sodekovs.bikesharing.simulation.BahnStationen;
import sodekovs.bikesharing.verkehrsteilnehmer.NutzeBahn;


/**
 * Agent, der ein Verkehrsmittel darstellt
 * @author dagere
 *
 */
public class VerkehrsmittelAgent extends MicroAgent
{
	public static String POSITION = "position";
	public static String STATION = "station";
	public static String WEG = "weg";
	public static String OFFEN = "offen";
	public static String LINIE = "linie";
	public static String LINIENNAME = "linienname";
	public static String RICHTUNG = "richtung";
	public static String MITFAHRER = "mitfahrer";

	private ISpaceObject _avatar;
	private Grid2D _space;
	private String _linienName; // Müsste final sein, geht aber wegen JadeX
								// nicht..

	private boolean _richtung;
	private BahnStation _letzteStation;
	private BahnStation _naechsteStation;
//	private Double _geschwindigkeit;
	private IVector2 _geschwindigkeitsvektor;
	private int _schritte;
	//Problem: Geschwindigkeiten mit Double sind nicht genau genug, um im Zweifelsfall Perioden abzufangen (z.B. Geschwindigkeit von 1/3)
	//		   dadurch erreicht dann ein Verkehrsmittel ggf. die Haltestelle nie; um dem Abhilfe zu schaffen, werden die schritte gezählt, und das
	//		   Verkehrsmittel wird nach _schritte schritten einfach "hingebeamt"

	IComponentStep r = null;

	public VerkehrsmittelAgent()
	{
		_schritte = 10000;
	}

	public IFuture<Void> executeBody()
	{
		IExternalAccess	paexta = (IExternalAccess)getParentAccess();
		paexta.getExtension("simulationsspace")
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				_space = (Grid2D) result;
				_avatar = _space.getAvatar( getComponentDescription() );
				
				vorBerechnungen();
				
				r = new IComponentStep()
				{

					@Override
					public IFuture<Void>  execute(IInternalAccess ia)
					{
						bewegen();
						return IFuture.DONE;
					}
				};
				waitForTick(r);
			}
		}));
		return IFuture.DONE;
	}
	
	private synchronized void vorBerechnungen()
	{
		_avatar.setProperty(MITFAHRER, new Integer(0));

		// Berechnet die nächste Station in Abhängigkeit der aktuellen Position
		Weg weg = (Weg) _avatar.getProperty(LINIE);
		String linienname = (String) _avatar.getProperty(LINIENNAME);
		_linienName = linienname;
		IVector2 position = (IVector2) _avatar.getProperty(Space2D.PROPERTY_POSITION);
		BahnStation naechsteBS = BahnStationen.gibInstanz().gibNaechsteStation(position, linienname);
		// Geht einfach davon aus, dass die Startposition eine Station ist
		Integer station = weg.gibStationen().indexOf(naechsteBS.gibPosition());
//		System.out.println("Station: " + station + " " + naechsteBS + " Linie: " + linienname);
		_avatar.setProperty(STATION, station);
		_avatar.setProperty(OFFEN, new Boolean( true ) );

		_letzteStation = BahnStationen.gibInstanz().gibNaechsteStation(position);
		// Geht einfach davon aus, dass die Zielposition eine Station ist

		_naechsteStation = BahnStationen.gibInstanz().gibStation(weg.gibStationen().get(gibNaechste(station)));

//		System.out.println("Derzeitig: " + _letzteStation + " Nächste: " + _naechsteStation);
	}

	private synchronized boolean pruefeAufFreigabe()
	{

		if (BahnStationen.gibInstanz().pruefWegBesetzt(_letzteStation.gibPosition(), _naechsteStation.gibPosition()))
		// Wenn die Bahn gerade hält, aber
		// der Weg zur nächsten Station blockiert ist, muss gewartet werden..
		{
			return false;
		}
		else
		{
			BahnStationen.gibInstanz().setzWegBesetzt(_letzteStation.gibPosition(), _naechsteStation.gibPosition(), true);
			return true;
		}
	}

	/**
	 * Sorgt für die Standardbewegung des Verkehrsmittels in einem Tick
	 */
	private void bewegen()
	{

		Integer station = (Integer) _avatar.getProperty(STATION);
		Weg weg = (Weg) _avatar.getProperty(LINIE);
		Vector2Double position = (Vector2Double) _avatar.getProperty(POSITION);
		
		if (_avatar.getProperty(OFFEN).equals(new Boolean(true)))
		{
			if (pruefeAufFreigabe())
			{
				tuerSchliessen();
				
				int jetzt = LinienInformationen.gibInstanz().gibAbstand(_linienName, station);
				int dannach = LinienInformationen.gibInstanz().gibAbstand(_linienName, gibNaechste(station));
				_schritte = Math.abs( jetzt - dannach );
				
				Vector2Double naechste = new Vector2Double(weg.gibStationen().get(station).getX().getAsDouble(), weg
						.gibStationen().get(station).getY().getAsDouble());

				Vector2Double richtung = (Vector2Double) naechste.copy().subtract(position);
				
				_geschwindigkeitsvektor = (Vector2Double) new Vector2Double( richtung.getXAsDouble() / _schritte, richtung.getYAsDouble() / _schritte );
//				System.out.println("Geschwindigkeit: " + _geschwindigkeitsvektor);
			}
			waitForTick(r);
			return;
		}
		else
		{
			_richtung = (Boolean) _avatar.getProperty(RICHTUNG);

			Vector2Double naechste = new Vector2Double(weg.gibStationen().get(station).getX().getAsDouble(), weg
					.gibStationen().get(station).getY().getAsDouble());

			position = (Vector2Double) position.add( _geschwindigkeitsvektor );

			_avatar.setProperty(POSITION, position);
			_schritte--; //Schritte, da Rundungsungenauigkeit sonst zu Fehlern führen
			
			if ( (position.getXAsDouble() == naechste.getXAsDouble() && 
					position.getYAsDouble() == naechste.getYAsDouble()) ||
				 _schritte <= 0)
			{
				_avatar.setProperty(POSITION, naechste ); // wenn man die Position nicht um die 0.0000001 zurechtrückt, dann kriegen die Verkehrsteilnehmer ein Problem..
				station = behandleStationsankunft(weg, station);
			}

			if ( station != -1 )
			{
				waitForTick(r);
			}
		}
	}

	/**
	 * Behandelt die Ankunft an einer Station, d.h. setzt die nächste
	 * Zielstation auf die nächste, schaut, ob man ggf. Umdrehen muss und lässt
	 * Leute aus- und einsteigen
	 * 
	 * @param weg
	 *            Weg des Verkehrsmittels
	 * @param station
	 *            Index der Station, die gerade angesteuert wurde
	 * @return Index der nächsten anzusteuernden Station
	 */
	private int behandleStationsankunft(Weg weg, int station)
	{
		BahnStationen.gibInstanz().setzWegBesetzt(_letzteStation.gibPosition(), _naechsteStation.gibPosition(), false);

		int naechsteStation = berechneNaechste(station);
		if ( naechsteStation != -1 )
		{
			_avatar.setProperty(STATION, naechsteStation);

			BahnStation bs = BahnStationen.gibInstanz().gibStation(weg.gibStationen().get(station));

			tuerOeffnen();

			_letzteStation = _naechsteStation;
			_naechsteStation = bs;

			return naechsteStation;
		}
		else
		{
//			System.out.println("Eigentlich nacht...");
			return -1;
		}
		
	}
	
//	private IVector2 oeffnepos = null;
	/**
	 * Setzt das Verkehrsmittel auf zu (d.h. offen auf false)
	 */
	private void tuerOeffnen()
	{
		_avatar.setProperty(OFFEN, new Boolean(true));
	}
	
	/**
	 * Setzt das Verkehrsmittel auf auf (d.h. offen auf trie)
	 */
	private void tuerSchliessen()
	{
		_avatar.setProperty(OFFEN, new Boolean(false));
	}
	

	/**
	 * Berechnet die nächste Station und setzt die Richtung des Agenten ggf. neu
	 * 
	 * @param station
	 * @return
	 */
	private int berechneNaechste(int station)
	{
		Weg weg = (Weg) _avatar.getProperty(LINIE);
		int naechste;
		if (_richtung)
		{
			naechste = station + 1;
		}
		else
		{
			naechste = station - 1;
		}

		if (!(weg.gibStationen().size() > naechste) || naechste < 0)
		{
			_richtung = !_richtung;
			_avatar.setProperty(RICHTUNG, _richtung);
			naechste = gibNaechste(station);

			IComponentIdentifier[] empfaenger = new IComponentIdentifier[1];
			Map<String, Object> sendMap = new HashMap<String, Object>();
			sendMap.put(SFipa.SENDER, getComponentIdentifier() );
			empfaenger[0] = gibEndstationsAgent( (IVector2) _avatar.getProperty(Space2D.PROPERTY_POSITION ) ).getName();
			sendMap.put(SFipa.RECEIVERS, empfaenger);
			sendMap.put(SFipa.CONTENT, EndstationsAgent.REGISTRIEREN);
			sendMap.put(SFipa.PERFORMATIVE, SFipa.INFORM);
//			System.out.println("Sende an: " + empfaenger[0] + ": " + EndstationsAgent.REGISTRIEREN);
			sendMessage(sendMap, new FIPAMessageType());
			
			return -1;
		}

		return naechste;
	}
	
	/**
	 * Sucht den Endstationsagent an der übergebenen Position
	 * @param position	Position, an der der Endstationsagent gesucht werden soll
	 * @return			Den Endstationsagent
	 */
	private IComponentDescription gibEndstationsAgent( IVector2 position)
	{
//		System.out.println("Suche ESA");
		ISpaceObject[] endstationsagenten = _space.getSpaceObjectsByType("endstationsagent");
		ISpaceObject esa = null; //EndStationsAgent
		for ( ISpaceObject iso : endstationsagenten )
		{
			if ( iso.getProperty( Space2D.PROPERTY_POSITION).equals( position ) && iso.getProperty(LINIE).equals(_linienName ) )
			{
				esa = iso;
			}
		}

		return _space.getOwner( esa.getId() );
	}

	/**
	 * Berechnet lediglich die nächste Station, ohne die Richtung neu zu setzen
	 * 
	 * @param station
	 *            Station, von der aus die nächste berechnet werden soll
	 */
	private int gibNaechste(int station)
	{
		Weg weg = (Weg) _avatar.getProperty(LINIE);
		int naechste;
		if ( _richtung )
		{
			naechste = station + 1;
		}
		else
		{
			naechste = station - 1;
		}

		if ( !(weg.gibStationen().size() > naechste) || naechste < 0)
		{
			if ( !_richtung )
			{
				naechste = station + 1;
			}
			else
			{
				naechste = station - 1;
			}
		}
		return naechste;
	}
	
	
	/**
	 * Verwaltet Nachrichtenankunft; beginnt, den Agenten wieder zu bewegen,
	 * wenn die entsprechende Nachricht angekommen ist
	 */
	@Override
	public synchronized void messageArrived( Map msg, MessageType mt )
	{
		Object sender = msg.get("sender");
		Object content = msg.get("content");
//		System.out.println("Nachricht, inhalt: " + content);
		
		if ( sender instanceof IComponentIdentifier && EndstationsAgent.FAHRLOS.equals(content) )
		{
			waitForTick( r );
		}
		
		if ( sender instanceof IComponentIdentifier && NutzeBahn.EINSTEIGEN.equals( content  ) )
		{
			Integer mitfahrerzahl = (Integer) _avatar.getProperty(VerkehrsmittelAgent.MITFAHRER);
			_avatar.setProperty("mitfahrer", mitfahrerzahl + 1);
		}
		
		if ( sender instanceof IComponentIdentifier && NutzeBahn.AUSSTEIGEN.equals( content  ) )
		{
			Integer mitfahrerzahl = (Integer) _avatar.getProperty(VerkehrsmittelAgent.MITFAHRER);
			_avatar.setProperty("mitfahrer", mitfahrerzahl - 1);
		}
		
	}
}
