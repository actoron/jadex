package sodekovs.old.bikesharing.bahnverwaltung;

import java.util.Map;

/**
 * Verwaltet alle Linieninformationen; ist nicht Teil von BahnStationen, weil er inhaltlich etwas eigenes verwaltet 
 * (Informationen über Linien, die unabhängig von Stationen sind). Da diese Informationen für jeden zugänglich, allerdings 
 * insgesamt nur einmal vorhanden sind, wird die Klasse als Singleton implementiert
 * @author dagere
 *
 */
public class LinienInformationen
{
	private static LinienInformationen _singleton;
	private Map<String, LinienInformation> _linieninformationen;
	
	private LinienInformationen( Map<String, LinienInformation> linieninformationen )
	{
		_linieninformationen = linieninformationen;
	}
	
	public static void erstellInstanz( Map<String, LinienInformation> linieninformationen )
	{
		if ( _singleton == null )
		{
			_singleton = new LinienInformationen( linieninformationen );
		}
		else
		{
			System.out.println("LinienInformationen.erstellInstanz: Instanz bereits vorhanden");
		}
	}
	
	public static LinienInformationen gibInstanz()
	{
		return _singleton;
	}
	
	/**
	 * Gibt die Informationen zu einer bestimmten Linie aus
	 * @param linie
	 * @return
	 */
	public LinienInformation gibLinienInformation( String linie )
	{
		return _linieninformationen.get( linie );
	}
	
	/**
	 * Gibt die Startzeit der Linie aus; redundant wegen gibLinienInformation, 
	 * aber durchaus nützlich für das Beachten der Regel von Demeter
	 * @param linie
	 * @return
	 */
	public long gibStartzeit( String linie )
	{
		return _linieninformationen.get(linie).gibStart();
	}
	
	/**
	 * Gibt die Endzeit der Linie aus; redundant wegen gibLinienInformation, 
	 * aber durchaus nützlich für das Beachten der Regel von Demeter
	 * @param linie
	 * @return
	 */
	public long gibEndzeit( String linie )
	{
		return _linieninformationen.get(linie).gibEnde();
	}
	
	/**
	 * Gibt den Takt der Linie aus; redundant wegen gibLinienInformation, 
	 * aber durchaus nützlich für das Beachten der Regel von Demeter
	 * @param linie
	 * @return
	 */
	public long gibTakt( String linie )
	{
		return _linieninformationen.get(linie).gibTakt();
	}
	
	public int gibFahrzeuge( String linie )
	{
		return _linieninformationen.get(linie).gibFahrzeuge();
	}
	
	/**
	 * Gibt den Abstand zwischen zwei Stationen für eine bestimmte Linie
	 * in Ticks aus
	 * @param linie		Linie, die untersucht werden soll
	 * @param station	Startstation, von der der Abstand zu nächsten ausgegeben werden soll
	 * @return
	 */
	public Integer gibAbstand( String linie, int station )
	{
		return _linieninformationen.get(linie).gibZeit(station);
	}
}
