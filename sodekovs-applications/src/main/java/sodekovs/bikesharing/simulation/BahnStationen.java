package sodekovs.bikesharing.simulation;

import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sodekovs.old.bikesharing.bahnverwaltung.BahnStation;
import sodekovs.old.bikesharing.bahnverwaltung.WegZustand;
import sodekovs.old.bikesharing.container.Weg;


/**
 * Eine Klasse, die alle Bahnstationen (bzw. allgemein ÖPNV-Stationen)
 * speichert; da diese für jeden zugänglich, allerdings insgesamt nur einmal
 * vorhanden sind, wird die Klasse als Singleton implementiert
 * 
 * @author David Georg Reichelt
 * 
 */
public class BahnStationen
{
	private static BahnStationen _singleton;
	private static double MAXIMALDISTANZ = 0.1;

	// Es wird die Annahme getroffen, dass man alle Stationen mit ganzzahligen
	// Koordinaten erfasst.
	// Ggf. durch Streckung dürfte das auch problemlos möglich sein
	private List<BahnStation> _stationen;
	

	public static BahnStationen gibInstanz()
	{
		return _singleton;
	}

	/**
	 * Erstellt eine Instanz der Singletonklasse mit der übergebenen
	 * Stationsliste
	 * 
	 * @param stationen
	 */
	public static void erstellInstanz(List<BahnStation> stationen)
	{
		if (gibInstanz() == null)
		{
//			System.out.println("Erstelle singleton");
			_singleton = new BahnStationen( stationen );
		}
		else
		{
			System.out.println("BahnStationen.erstellInstanz: Instanz bereits vorhanden");
		}
	}

	private BahnStationen( List<BahnStation> stationen )
	{
		_stationen = stationen;	// TODO: Test, ob Stationsbezeichnungen doppelt
	}

	/**
	 * Gibt die Liste mit allen �PNV-Stationen aus
	 * 
	 * @return Liste mit Stationen
	 */
	public List<BahnStation> gibStationen()
	{
		return _stationen;
	}

	/**
	 * Gibt die nächste ÖPNV-Station an
	 * 
	 * @param position
	 *            Position, in deren Umgebung die nächste Station gesucht wird
	 * @return Position der nächstgelegenen ÖPNV-Station
	 */
	public BahnStation gibNaechsteStation(IVector2 position)
	{
		// List<BahnStation> stationsliste =
		// BahnStationen.getInstance().gibStationen();

		IVector1 abstand = _stationen.get(0).gibPosition().getDistance(position);
		BahnStation naechsteStation = _stationen.get(0);
		for (BahnStation station : _stationen)
		{
			IVector1 abstandNeu = station.gibPosition().getDistance(position);
			if (abstand.greater(abstandNeu))
			{
				abstand = abstandNeu;
				naechsteStation = station;
			}
		}
		return naechsteStation;
	}

	/**
	 * Gibt die nächste ÖPNV-Station auf einer Linie
	 * 
	 * @param position
	 *            Position, in deren Umgebung die nächste Station gesucht wird
	 * @param linie
	 *            Linie, an der Station gesucht werden soll
	 * @return Position der nächstgelegenen ÖPNV-Station
	 */
	public BahnStation gibNaechsteStation(IVector2 position, String linie)
	{
		// List<BahnStation> stationsliste =
		// BahnStationen.getInstance().gibStationen();

		IVector1 abstand = _stationen.get(0).gibPosition().getDistance(position);
		BahnStation naechsteStation = _stationen.get(0);
		for (BahnStation station : _stationen)
		{
			IVector1 abstandNeu = station.gibPosition().getDistance(position);
			if (abstand.greater(abstandNeu) && station.gibLinien().contains(linie))
			{
				abstand = abstandNeu;
				naechsteStation = station;
			}
		}
		return naechsteStation;
	}

	/**
	 * Gibt eine Map allen Stationen und ihren Entfernungen zu einer Position an
	 * 
	 * @param position
	 * @return
	 */
	public Map<Double, BahnStation> gibNaechsteStationen(IVector2 position)
	{
		Map<Double, BahnStation> distanzliste = new HashMap<Double, BahnStation>();
		for (BahnStation station : _stationen)
		{
			Double abstandNeu = station.gibPosition().getDistance(position).getAsDouble();
			distanzliste.put(abstandNeu, station);
		}
		return distanzliste;
	}

	/**
	 * Gibt die Station aus, die exakt an der übergebenen Position ist
	 * 
	 */
	public BahnStation gibStation(IVector2 position)
	{
		for (BahnStation station : _stationen)
		{
			if (station.gibPosition().equals(position))
			{
				return station;
			}
		}
		return null;
	}

	/**
	 * Gibt die Station aus, die den übergebenen Namen hat
	 * 
	 * @param nodeValue
	 */
	public BahnStation gibStation(String nodeValue)
	{
		for (BahnStation station : _stationen)
		{
			// station2.
			if (station.gibBezeichnung().equals(nodeValue))
			{
				return station;
			}
		}
		return null;
	}
	
	public List<BahnStation> gibStationen(String l)
	{
		List<BahnStation> stationen = new ArrayList<BahnStation>();
		for ( BahnStation station : _stationen )
		{
			if ( station.gibLinien().contains( l ) )
			{
				stationen.add( station );
			}
		}
		return stationen;
	}

	private class Knoten
	{
		double abstand;
		Knoten vorgaenger;
		BahnStation station;
		Map<Knoten, Double> nachbarn;

		Knoten(int abstand, Knoten vorgaenger, BahnStation station, List<Knoten> knotenliste)
		{
			knotenliste.add(this);
			this.abstand = abstand;
			this.vorgaenger = vorgaenger;
			this.station = station;
			nachbarn = new HashMap<Knoten, Double>();
			for (Map.Entry<BahnStation, WegZustand> bs : station.gibNachbarn().entrySet())
			{
				boolean gefunden = false;
				for (Knoten kn : knotenliste)
				{
					if (kn.station == bs.getKey())
					{
						nachbarn.put(kn, bs.getValue().gibEntfernung());
						gefunden = true;
					}
				}
				if (!gefunden)
				{
					Knoten kn = new Knoten(Integer.MAX_VALUE, null, bs.getKey(), knotenliste);
					nachbarn.put(kn, bs.getValue().gibEntfernung());
				}
			}
		}
	}

	private void initialisiere(List<Knoten> knotenliste, BahnStation start)
	{
		Knoten startKnoten = new Knoten(0, null, start, knotenliste);
		// erstellt dann rekursiv alle andern...

	}

	/**
	 * Gibt die Station mit minimalem Abstand vom Startknoten aus der Liste
	 * zurück
	 * 
	 * @param knotenliste
	 *            Liste mit Stationen
	 * @return Station mit minimalem Abstand
	 */
	private Knoten gibStationMitMinimalemAbstand(List<Knoten> knotenliste)
	{
		Knoten minStation = knotenliste.get(0);
		for (Knoten k : knotenliste)
		{
			if (k.abstand < minStation.abstand)
			{
				minStation = k;
			}
		}
		return minStation;
	}

	/**
	 * Gibt den Umsteigeplan als Weg von einer Startstation zu einer Zielstation
	 * aus
	 * 
	 * @param start
	 *            Startstation als BahnStation
	 * @param ziel
	 *            Zielstation als BahnStation
	 * @return Weg vom Start zum Ziel (d.h. Stationspositionen)
	 */
	public Weg gibUmsteigePlan(BahnStation start, BahnStation ziel)
	{
		// System.out.println("Start: " + start + "Ziel: " + ziel);

		List<Knoten> knotenliste = new LinkedList<Knoten>();
		initialisiere(knotenliste, start);

		Knoten zielknoten = null;
		for (Knoten kn : knotenliste)
		{
			if (kn.station == ziel)
			{
				zielknoten = kn;
			}
		}

		// System.out.println("Zielknoten: " + zielknoten);

		while (!knotenliste.isEmpty())
		{
			Knoten aktuelleStation = gibStationMitMinimalemAbstand(knotenliste);
			for (Map.Entry<Knoten, Double> kn : aktuelleStation.nachbarn.entrySet())
			{
				if (kn.getKey().abstand > aktuelleStation.abstand + kn.getValue())
				{
					kn.getKey().vorgaenger = aktuelleStation;
					kn.getKey().abstand = aktuelleStation.abstand + kn.getValue();
					// TODO kantengewichte einfügen
				}
			}
			knotenliste.remove(aktuelleStation);
		}

		Weg w = new Weg();
		if (zielknoten.vorgaenger != null)
		{
			for (Knoten kn = zielknoten; kn != null; kn = kn.vorgaenger)
			{
				w.addStation(kn.station.gibPosition(), 0);
			}
		}
		else
		{
			w = null; // Kein Weg zum Zielknoten gefunden
		}

		return w;
	}

	/**
	 * Gibt den Umsteigeplan als Weg von einer Startstation zu einer Zielstation
	 * aus Achtung: auch, wenn die nur die Positionen der Stationen übergeben
	 * werden, muss es sich dabei wirklich um Stationen (d.h. exakte Positionen
	 * handeln), ansonsten wird null zurück gegeben
	 * 
	 * @param start
	 *            Startstation als Position, muss eine Station sein
	 * @param ziel
	 *            Zielstation als Position, muss eine Station sein
	 * @return Weg vom Start zum Ziel (d.h. Stationspositionen)
	 */
	public Weg gibUmsteigePlan(IVector2 start, IVector2 ziel)
	{
		BahnStation startStation = gibNaechsteStation(start);

		if ( startStation.gibPosition().getDistance( start ).getAsDouble() > MAXIMALDISTANZ)
		{
			System.out.println("Startstation");
			return null;
		}

		BahnStation zielStation = gibNaechsteStation(ziel);

		if ( zielStation.gibPosition().getDistance( ziel ).getAsDouble() > MAXIMALDISTANZ)
		{
			System.out.println("Zielstation: " + zielStation + " " + ziel);
			return null;
		}

		return gibUmsteigePlan(startStation, zielStation);

	}

	/**
	 * Prüft, ob ein Weg zwischen zwei Stationen zurzeit belegt ist (d.h.
	 * befahren wird)
	 * Muss volatile sein, weil verschiedene Agenten das parallel aufrufen...
	 * 
	 * @param letzte
	 *            Position der Startstation
	 * @param naechste
	 *            Position der Zielstation
	 * @return
	 */
	public synchronized boolean pruefWegBesetzt(IVector2 letzte, IVector2 naechste)
	{
//		System.out.println("Starte Prüfung");
		WegZustand wz = gibWegZustand(letzte, naechste);
//		System.out.println("Beende Prüfung");
		return wz.istBelegt();
	}

	/**
	 * Setzt einen Weg zwischen zwei stationen auf belegt bzw. frei
	 * 
	 * @param letzte
	 *            Position der Startstation
	 * @param naechste
	 *            Position der Zielstation
	 */
	public void setzWegBesetzt(IVector2 letzte, IVector2 naechste, boolean belegt)
	{
		WegZustand wz = gibWegZustand(letzte, naechste);
		wz.setzBelegt(belegt);
	}

	private WegZustand gibWegZustand(IVector2 letzte, IVector2 naechste)
	{
		BahnStation letzteStation = gibNaechsteStation(letzte);
		BahnStation naechsteStation = gibNaechsteStation(naechste);
		if (!letzteStation.gibPosition().equals(letzte) || !naechsteStation.gibPosition().equals(naechste))
		{
			System.out.println("BahnStationen.gibWegZustand: Fehler, Stationen nicht vorhanden " + letzte + " "
					+ naechste + " Stationen: " + letzteStation + " " + naechsteStation);
			return null;
		}
		WegZustand wz = letzteStation.gibNachbarn().get(naechsteStation);
		if (wz == null)
		{
			System.out.println("BahnStationen.gibWegZustand: Fehler, Weg nicht vorhanden");
		}
		return wz;
	}

	

}
