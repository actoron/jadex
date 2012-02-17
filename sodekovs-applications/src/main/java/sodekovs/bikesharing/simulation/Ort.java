package sodekovs.bikesharing.simulation;

import jadex.extension.envsupport.math.Vector2Double;
//import jadex.application.space.envsupport.math.Vector2Double;

/**
 * Klasse, um einen Ort einer Verteilung zu speichern
 * @author dagere
 *
 */
public class Ort
{
	private double _x, _y, _gewichtung;
	
	public Ort( double x, double y, double gewichtung)
	{
		_x = x;
		_y = y;
		_gewichtung = gewichtung;
	}
	
	public double gibX()
	{
		return _x;
	}
	
	public double gibY()
	{
		return _y;
	}
	
	public double gibGewichtung()
	{
		return _gewichtung;
	}
	
	public Vector2Double gibAlsVektor() //TODO: ändern, bissl blöd, dass dann 100 Instanzen von dem Vektor erzeugt werden
	{
		return new Vector2Double( _x, _y);
	}
}
