package sodekovs.old.bikesharing.bahnverwaltung;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse, um eine Bahnstation mit ihren Daten zu speichern
 * TODO: sicherstellen, dass von jeder BahnStation nur eine Instanz existiert (Fabrikmethode?)
 * @author dagere
 *
 */
public class BahnStation
{
	private final Vector2Double _position;
	private final String _bezeichnung;
	private List<String> _linienliste;
	
	private Map<BahnStation, WegZustand> _nachbarn; //Adjazenzliste der Bahnstation im Graphen mit Kantengewichten
	
	public BahnStation( Vector2Double position, String bezeichnung )
	{
		_position = position;
		_bezeichnung = bezeichnung;
		_nachbarn = new HashMap<BahnStation, WegZustand>(); 
		_linienliste = new ArrayList<String>();
	}
	
	public IVector2 gibPosition()
	{
		return _position;
	}
	
	public String gibBezeichnung()
	{
		return _bezeichnung;
	}
	
	/**
	 * Fügt eine Verbindung von einer Station zu einer anderen hinzu, und speichert die Entfernung
	 * @param nachbar
	 */
	public void fuegNachbarHinzu( BahnStation nachbar, double entfernung )
	{
		if ( !_nachbarn.containsKey( nachbar ) )
		{
			WegZustand w = new WegZustand( this, nachbar, entfernung);
			_nachbarn.put( nachbar, w );
		}
		else
		{
//			System.out.println("Bahnstation.fuegNachbarHinzu: Nachbar bereits vorhanden");
//TODO: möglich wäre es, die Struktur hier so zu ändern, dass U und S-Bahnen für gleiche Stationen unterschiedlich lang brauchen... 
//		aber so richtig nötig ist es nicht, zurzeit wird dann eben immer die zuerst hinzugefügte Distanz gespeichert
		}
		
	}
	
	public void fuegLinieHinzu( String linie )
	{
		if ( linie == null || linie.equals("") )
		{
			System.out.println("BahnStation.fuegLinieHinzu: Linienname ungültig");
		}
		else
		{
			_linienliste.add( linie );
		}
	}
	
	/**
	 * Gibt alle Nachbarknoten mit deren Entfernung aus
	 * @return	Nachbarknoten (als Map)
	 */
	public Map<BahnStation, WegZustand> gibNachbarn()
	{
		return _nachbarn;
	}
	
	
	public String toString()
	{
		return _bezeichnung + ": " + _position.toString();
	}

	/**
	 * Gibt eine Liste mit allen haltenden Linien aus
	 * @return
	 */
	public List<String> gibLinien()
	{
		return _linienliste;
	}
}
