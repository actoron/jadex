package sodekovs.old.bikesharing.fahrrad;
//
//import jadex.application.space.envsupport.environment.ISpaceObject;
//import jadex.application.space.envsupport.math.Vector2Double;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector2Double;

/**
 * Eine Klasse, um die Fahrräder der Simulation zu modellieren
 * @author dagere
 *
 */
public class Fahrrad
{
	private Vector2Double _position;
	private ISpaceObject _besitzer;
	private	FahrradVerleihStation _station;
	
	/**
	 * Erstellt das Fahrrad, stehend an der übergeben Station und an der übergebenen Position
	 * @param position	Position, an der sich das Fahrrad befinden soll
	 * @param station	Station, an der sich das Fahrrad befinden soll
	 */
	public Fahrrad( Vector2Double position, FahrradVerleihStation station )
	{
		_position = position;
		_station = station;
		_besitzer = null;
	}
	
	/**
	 * Leiht das Fahrrad für den übergebenen Besitzer aus
	 * @param besitzer
	 */
	public void ausleihen( ISpaceObject besitzer )
	{
		if ( _besitzer == null )
		{
			_besitzer = besitzer;
			_station = null;
		}
		else
		{
			System.out.println("Fahrrad.ausleihen: Fahrrad bereits ausgeliehen");
		}
		
	}
	
	/**
	 * Gibt das Fahrrad an der übergebenen Station zurück
	 * @param station	Station, an die das Fahrrad zurückgegeben werden soll
	 */
	public void zurueckgeben( FahrradVerleihStation station )
	{
		if ( _station == null )
		{
			if ( station.gibPosition().equals( _position ) )
			{
				_station = station;
				_besitzer = null;
			}
		}
		else
		{
			System.out.println("Fahrrad.zurueckgeben: Fahrrad ist bereits an einer Station");
		}
	}
	
	public Vector2Double gibPosition()
	{
		return _position;
	}
	
	public void setzPosition( Vector2Double position )
	{
		_position = position;
	}
}
