package sodekovs.old.bikesharing.zeit;

/**
 * Eine Klasse, die allgemeine Informationen über die Zeit geben soll
 * (insbesondere: wann ist morgens, abends, ..)
 * @author dagere
 *
 */
public class Zeitverwaltung
{
	private static Zeitverwaltung _zeitverwaltung;
	
	private long _mitternacht;
	private long _zeit;
	
	/**
	 * Konstruktor des Objektes, benötigt die Zeit
	 * @param startzeit
	 */
	private Zeitverwaltung( long startzeit )
	{
		_zeit = startzeit;
	}
	
	/**
	 * Gibt die Zeit, die seit Mitternacht vergangen ist, zurück
	 * @return
	 */
	public long gibTageszeit( )
	{
		return ( _zeit ) % (24*60);
	}
	
	/**
	 * Gibt die aktuelle Tageszeit als String der Form hh:mm aus
	 * @return Aktuelle Tageszeit als String
	 */
	public String gibZeitString()
	{
		long zeit = gibTageszeit();
		String s = zeit / 60 + ":" + zeit%60;
		return s;
	}
	
	/**
	 * Gibt die Zeit, die seit Simulationsanfang vergangen ist, zurück
	 * @return
	 */
	public long gibZeit()
	{
		return _zeit;
	}
	
	public boolean gibNacht()
	{
		return false;
	}
	
	/**
	 * Erstellt eine Instanz des Singleton, und benötigt dafür die Startzeit des Systems
	 * @param startzeit Startzeit des Systems
	 */
	public static void createInstance( long startzeit )
	{
		_zeitverwaltung = new Zeitverwaltung( startzeit );
	}
	
	/**
	 * Gibt das einzige erstelle Objekt aus
	 * @return
	 */
	public static Zeitverwaltung gibInstanz()
	{
		return _zeitverwaltung;
	}

	public void setzZeit( int zeit )
	{
//		System.out.println("Zeit: " + zeit);
		if ( zeit%(24*60) == 0 )
		{
			System.out.println("Mitternacht, Tag: " + ((int)zeit / (24*60) ) );
		}
		_zeit = zeit;
	}

	
}
