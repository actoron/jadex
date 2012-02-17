package sodekovs.bikesharing.simulation;

//import jadex.application.space.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Double;
/**
 * Speichert einen Bereich, in dem Menschen starten, der Ziel von Menschen ist usw.
 * @author dagere
 *
 */
public class Bereich extends Ort
{
	private double _breite, _hoehe;
	
	public Bereich( double x, double y, double gewichtung, double breite, double hoehe)
	{
		super( x, y, gewichtung );
		
		_breite = breite;
		_hoehe = hoehe;
	}
	
	
	@Override
	public Vector2Double gibAlsVektor() 
	{
		return new Vector2Double( gibX() + Math.random() * _breite, gibY() + Math.random() * _hoehe);
	}
}
