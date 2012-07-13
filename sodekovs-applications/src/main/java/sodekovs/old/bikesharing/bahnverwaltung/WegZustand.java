package sodekovs.old.bikesharing.bahnverwaltung;


/**
 * Speichert Informationen über den Zustand eines Weges zwischen 2 BahnStationen,
 * insbesondere die Entfernung und ob der Weg zurzeit belegt ist
 * @author dagere
 *
 */
public class WegZustand
{
	private BahnStation _start;
	private BahnStation _ziel;
	private double _entfernung;
	private boolean _belegt;
	
	public WegZustand( BahnStation start, BahnStation ziel, double entfernung )
	{
		_start = start;
		_ziel = ziel;
		_entfernung = entfernung;
		_belegt = false;
	}
	
	public boolean istBelegt( )
	{
		return _belegt;
	}
	
	public void setzBelegt( boolean belegt )
	{
//		System.out.println("Setze Wegbesetzt auf " + belegt + " : " + _start + " " + _ziel);
		_belegt = belegt;
	}
	
	public double gibEntfernung()
	{
		return _entfernung;
	}
}
