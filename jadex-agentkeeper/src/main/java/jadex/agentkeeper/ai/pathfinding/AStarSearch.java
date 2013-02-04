package jadex.agentkeeper.ai.pathfinding;

import java.util.*;


import jadex.agentkeeper.ai.oldai.basic.MoveAction;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 * Wegfindung nach dem A-Stern algorithmus
 * 
 * Dieser ist vollstaendig (wenn es einen Weg gibt wird dieser gefunden),
 * optimal(der beste weg) und optimal effizient(kein anderer Algorithmus findet
 * den Weg schneller)
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 * 
 * TODO: Refractor in English
 * 
 */
public class AStarSearch {

	private boolean _erreichbar;
	private int _geswegkosten;
	private final Waypoint _start;
	private ArrayList<Waypoint> _offeneliste;
	private ArrayList<Waypoint> _geschlosseneliste;
	private final Grid2D _space;
	private Vector2Int _zielvector;
	private Waypoint _niedrigster;
	private boolean _direkt;
	Stack<Vector2Int> _pfad;

	public AStarSearch(IVector2 start, IVector2 ziel, Grid2D space, boolean direkt) {
		
		_space = space;
		_direkt = direkt;
		_niedrigster = null;


		// Erst einmal checken ob das Feld direkt besucht werden muss (Claim Path, essen, Gold etc..) 
		// oder indirekt (Wand einreissen) wenn indirekt muss ein Nachbarfeld bestimmt werden welches
		// begehbar ist
		if (_direkt) {
			_zielvector = new Vector2Int(ziel.copy().getXAsInteger(), ziel.copy().getYAsInteger());
		}
		else {
			int vect_x = ziel.getXAsInteger();
			int vect_y = ziel.getYAsInteger();

			Vector2Int richtungen[] = best4Richtungen(vect_x, vect_y);

			boolean zielerreichbar = false;
			for (Vector2Int vector : richtungen) {
				if (begehbar(vector)) {
					_zielvector = vector;
					zielerreichbar = true;
				}

			}
			if (!zielerreichbar) {
				// Eigentlich gibt es dann keinen Zielvector! // Debugging Technisch aber notwendig
				_erreichbar = false;
				_zielvector = new Vector2Int(ziel.copy().getXAsInteger(), ziel.copy().getYAsInteger());
			}
		}

		Vector2Int startvector = new Vector2Int(start.getXAsInteger(), start.getYAsInteger());

		//Liste der Wegpunkte die noch geprueft werden muessen
		_offeneliste = new ArrayList<Waypoint>();
		
		//Liste der Wegpunkte die schon geprueft wurden
		_geschlosseneliste = new ArrayList<Waypoint>();

		//Der Startwegpunkt
		_start = new Waypoint(startvector, startvector, 0, 1000);

		//Wenn es einen Pfad gibt wird dieser hier spaeter gespeichert
		_pfad = new Stack<Vector2Int>();
		
		//1 Schitt den Startpunkt in die Offene liste packen
		_offeneliste.add(_start);

		// 2. Schritt: Anfangen zu suchen!
		//Hier versteckt sich der eigentliche Alogrithmus
		sucheoptimalenweg();

		//3. Schritt den Pfad speichern (sofern es einen gibt)
		if (_erreichbar) {
			// Wie teuer ist der gefundene Pfad?
			_geswegkosten = _geschlosseneliste.get(gibIndexOfWegpunkt(_geschlosseneliste, _zielvector))._fwert;

			// Zu dem gefunden Optimalen Weg jetzt nur noch die einzelnen
			// Wegpunkte speichern
			_pfad = berechnePfad();
		}
		else {
			_pfad = null;
		}

	}

	// Der eigentliche A-Stern-Algorithmus
	private void sucheoptimalenweg() {
		while (true) {

			//Den Punkt mit den niedrigsten Kosten bestimmen
			_niedrigster = gibMinFkostenPunkt();

			// In die geschlossene Liste verschieben
			_offeneliste.remove(_niedrigster);
			_geschlosseneliste.add(_niedrigster);

			// Abbruchbedingung -> Wenn Ziel in geschlossener Liste ist ende
			// gelaende
			if (_niedrigster.gibpunkt().equals(_zielvector)) {
				_erreichbar = true;
				break;
			}

			Vector2Int punkt = (Vector2Int) _niedrigster.gibpunkt();

			int vect_x = punkt.getXAsInteger();
			int vect_y = punkt.getYAsInteger();

			//Zu dem aktuell untersuchtem Punkt alle 8 richtungen bestimmen
			Vector2Int richtungen[] = best8Richtungen(vect_x, vect_y);

			//Alle "Nachbarn" pruefen
			for (Vector2Int vector : richtungen) {
				// Wenn es begehbar ist und kein Punkt in der geschlossenen Liste
				// ist...
				if (begehbar(vector) && !(istEnthalten(_geschlosseneliste, vector))) {

					// ....Fuege es der Offenen Liste hinzu, sofern nicht
					// vorhanden
					// sofern verhande pruefe ob der aktuelle G-Wert besser ist
					// oder nicht
					int gwert = berechneGwert(vector, _niedrigster);
					int hwert = berechneHwert(vector);

					Waypoint neueroffener = new Waypoint(vector, _niedrigster.gibpunkt(), gwert, hwert);

					//Wenn nicht Enthalten pack ihn rein, ansonsten pruefe
					//ob es ihn schon gibt
					//Sollte es ihn schon geben pruefte ob der neue
					//Punkt besser ist oder nicht
					//(Diagonal-optimierungs-problem)
					if (!istEnthalten(_offeneliste, vector)) {
						_offeneliste.add(neueroffener);
					}
					else {
						int index = gibIndexOfWegpunkt(_offeneliste, vector);
						if (_offeneliste.get(index)._gwert > gwert) {
							_offeneliste.remove(index);
							_offeneliste.add(neueroffener);
						}

					}

				}

			}

			// Abbruchbedingung 2 -> Wenn offene Liste leer ist gibt es keinen
			// Pfad
			if (_offeneliste.isEmpty()) {
				_erreichbar = false;
				// System.out.println("stop - kein WEG moeglich!!!");
				break;
			}

		}

	}

	//Diese Methode macht aus den Wegpunkten aus dem Algorithmus
	//einen Stack aus lauter Vectoren der von jedem Agenten der
	//einen Weg angefragt hat weiter verwendet werden kann
	private Stack<Vector2Int> berechnePfad() {
		int index = gibIndexOfWegpunkt(_geschlosseneliste, _zielvector);
		Stack<Vector2Int> pfad = new Stack<Vector2Int>();

		/**
		 * RAUSGENOMMEN
		 */
		// +0,5 damit er in der Mitte der Koordinaten ankommt
		pfad.push((Vector2Int) _zielvector.add(0.0));
		boolean erreicht = false;
		while (!erreicht) {
			Vector2Int punkt = (Vector2Int) _geschlosseneliste.get(index).gibvorgangerpunkt();
			if (punkt.equals(_start.gibpunkt())) {
				erreicht = true;
			}

			// +0,5 damit er in der Mitte der Koordinaten laeuft
			//punkt = (Vector2Int) punkt.add(0.5);
			pfad.push(punkt);
			index = gibIndexOfWegpunkt(_geschlosseneliste, punkt);

		}
		return pfad;
	}

	private Vector2Int[] best8Richtungen(int vect_x, int vect_y) {
		Vector2Int n = new Vector2Int(vect_x, vect_y - 1);
		Vector2Int no = new Vector2Int(vect_x + 1, vect_y - 1);
		Vector2Int o = new Vector2Int(vect_x + 1, vect_y);
		Vector2Int so = new Vector2Int(vect_x + 1, vect_y + 1);
		Vector2Int s = new Vector2Int(vect_x, vect_y + 1);
		Vector2Int sw = new Vector2Int(vect_x - 1, vect_y + 1);
		Vector2Int w = new Vector2Int(vect_x - 1, vect_y);
		Vector2Int nw = new Vector2Int(vect_x - 1, vect_y - 1);

		Vector2Int richtungen[] = { n, o, s, w, nw, so, sw, no };

		return richtungen;
	}

	private Vector2Int[] best4Richtungen(int vect_x, int vect_y) {
		Vector2Int n = new Vector2Int(vect_x, vect_y - 1);
		Vector2Int o = new Vector2Int(vect_x + 1, vect_y);
		Vector2Int s = new Vector2Int(vect_x, vect_y + 1);
		Vector2Int w = new Vector2Int(vect_x - 1, vect_y);

		Vector2Int richtungen[] = { n, o, s, w };

		return richtungen;
	}

	//Methode zur bestimmung des Wegpunktes mit den niedrigesten F-Kosten aus der offenen liste
	private Waypoint gibMinFkostenPunkt() {
		int minFKosten = Integer.MAX_VALUE;
		int fKosten;
		Waypoint tmp, minFkostenPunkt = null;
		for (int i = 0; i < _offeneliste.size(); i++) {
			tmp = _offeneliste.get(i);
			fKosten = tmp._fwert;
			if (fKosten < minFKosten) {
				minFKosten = fKosten;
				minFkostenPunkt = tmp;
			}
		}
		return minFkostenPunkt;
	}

	//Prueft ob ein Wegpunkt enthalten ist
	private boolean istEnthalten(ArrayList<Waypoint> wegpunkte, Vector2Int vector) {
		for (Waypoint weg : wegpunkte) {
			if (weg.gibpunkt().equals(vector)) {
				return true;
			}
		}
		return false;
	}

	//Gibt den index eines Wegpunktes zurueck
	private int gibIndexOfWegpunkt(ArrayList<Waypoint> wegpunkte, Vector2Int vector) {
		int i = 0;
		for (Waypoint weg : wegpunkte) {
			if (weg.gibpunkt().equals(vector)) {
				return i;
			}
			i++;
		}
		// Default, nicht enthalten
		return -1;

	}

	//Methode zur berechnung des H-Wertes (Schaetzwert bis zum Ziel)
	private int berechneHwert(Vector2Int punkt) {
		double vect_x = punkt.getXAsDouble();
		double vect_y = punkt.getYAsDouble();
		double zielvect_x = _zielvector.getXAsDouble();
		double zielvect_y = _zielvector.getYAsDouble();

		double dist = Math.abs(vect_x - zielvect_x) + Math.abs(vect_y - zielvect_y);

		return (int) ((10 * dist) + 10);
	}

	//Methode zur berechnung des G-Wertes (tatsaechlich schon "gelaufene"
	//Wegkosten)
	private int berechneGwert(Vector2Int punkt, Waypoint vorpunkt) {
		// Diagonal oder nicht (Diagonaler Weg ist teuer(Phytagoras)
		double vect_x = punkt.getXAsDouble();
		double vect_y = punkt.getYAsDouble();
		double zielvect_x = vorpunkt.gibpunkt().getXAsDouble();
		double zielvect_y = vorpunkt.gibpunkt().getYAsDouble();

		int dist = (int) (Math.abs(vect_x - zielvect_x) + Math.abs(vect_y - zielvect_y));

		//Wenn dist 1 ein gerader Weg
		if (dist == 1) {
			return (vorpunkt._gwert + 10);
		}
		//Ansonsten muss es sich um einen diagonalen uebergang handeln
		else {
			// Diagonaler Wert kostet 14 (Phytagoras)
			return (vorpunkt._gwert + 14);
		}
	}

	//Prueft ob ein Punkt begehbar ist
	private boolean begehbar(Vector2Int punkt) {
		if(InitMapProcess.isMoveable(punkt, _space))
		{
			return true;
		}
		return false;


	}

	public Stack<Vector2Int> gibPfad() {
		return _pfad;
	}
	
	public ArrayList<Vector2Int> gibPfadInverted() {
		ArrayList<Vector2Int> ret;
		
		ret = new ArrayList<Vector2Int>(_pfad);
		
		Collections.reverse(ret);
		
		ret.remove(0);
		
		return ret;
	}

	public int gibPfadKosten() {
		return _geswegkosten;
	}

	public boolean istErreichbar() {
		return _erreichbar;
	}

}