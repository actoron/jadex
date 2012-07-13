package sodekovs.old.bikesharing.container;

import jadex.extension.envsupport.math.IVector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse speichert Daten f�r einen Weg, den Agenten selbstst�ndig
 * (d.h. ohne Nutzung von �ffentlichen Verkehrsmitteln) zur�cklegen
 * Dabei werden die einzelnen Orte (auf der Karte) gespeichert
 * F�r Wege, die Agenten durch Nutzung von Verkehrsmitteln zur�cklegen,
 * siehe Verbindungen
 * @author David Georg Reichelt
 *
 */
public class Weg 
{
	protected List<IVector2> _stationen;
	
	/**
	 * Initialisiert einen Weg mit einer leeren Stationsliste
	 */
	public Weg( )
	{
		_stationen = new ArrayList<IVector2>(); 
		// Arraylist -> Zugriff schnell, Veränderung unwahrscheinlich
	}
	
	/**
	 * Gibt eine Liste aller Stationen aus
	 * @return	Liste aller Stationen
	 */
	public List<IVector2> gibStationen( )
	{
		return _stationen;
	}
	
	/**
	 * Fügt eine Station hinzu
	 * @param station	Station, die hinzugefügt wird
	 */
	public void addStation( IVector2 station )
	{
		_stationen.add( station );
	}
	
	public void addStation( IVector2 station, int position )
	{
		_stationen.add( position, station);
	}
	
	/**
	 * Gibt die Position aus, von der der Weg startet
	 * @return Startposition des Weges
	 */
	public IVector2 gibStart()
	{
		return _stationen.get(0);
	}
	
	public IVector2 gibEnde()
	{
		return _stationen.get( _stationen.size() - 1);
	}
	
	public String toString()
	{
		String ret = "";
		for ( IVector2 station : _stationen )
		{
			ret = ret + station.toString() + " ";
		}
		return ret;
	}
	
	public boolean equals( Object o )
	{
		if ( o instanceof Weg )
		{
			Weg w = (Weg) o;
			for ( int i = 0; i < _stationen.size(); i++ )
			{
				if ( w._stationen.get(i) == null || ! (w._stationen.get(i).equals( _stationen.get(i)) ) )
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}

	protected void loescheLetzteStation() {
		_stationen.remove( _stationen.size() - 1 );
	}
}


