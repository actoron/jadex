package sodekovs.old.bikesharing.datenkonvertierung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sodekovs.old.bikesharing.zeit.Zeitverwaltung;


/**
 * Ein Datensammler, dessen Aufgabe es ist, Zahlenwerte und ihre Zeitstempel zu speichern;
 * man hätte das auch mit dataprovidern im Jadex-Space umsetzen können, deren Include-Bedingung
 * dann ein Änderungsflag abgefragt hätte; das hätte aber zur Datenspeicherung, genau wie diese
 * Variante, eine Änderung des Simulationscodes benötigt, so dass es keinen nennenswerten Vorteil
 * gegenüber der Lösung eigener Datensammler gegeben hätte
 * @author dagere
 *
 */
public class Datensammler
{
	private BufferedWriter _ausgabe;
	private static Map<String, Datensammler> _sammler;
	
	static
	{
		_sammler = new HashMap<String, Datensammler>();
	}
	
	public static synchronized Datensammler gibDatensammler( String sammler )
	{
		if ( _sammler.containsKey( sammler ) )
		{
			return _sammler.get( sammler );
		}
		else
		{
			Datensammler s = new Datensammler( sammler );
			_sammler.put( sammler, s);
			return s;
		}
	}
	
	/**
	 * Initialisiert den Datensammler mit dem Namen der gewünschten Ausgabedatei
	 * @param name
	 */
	private Datensammler( String name )
	{
		FileWriter ausgabeStrom;
		try
		{
			ausgabeStrom = new FileWriter( name );
			_ausgabe = new BufferedWriter(ausgabeStrom);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Speichert den gewünschten Wert zur gewünschten Zeit
	 * @param zeit
	 * @param wert
	 */
	public void speichereWert( long zeit, int wert )
	{
		try
		{
			_ausgabe.write(zeit + ", " + wert + "\n");
			_ausgabe.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Speichert den gewünschten Wert zur gewünschten Zeit
	 * @param zeit
	 * @param wert
	 */
	public void speichereWert( long zeit, double wert )
	{
		try
		{
			_ausgabe.write(zeit + ", " + wert + "\n");
			_ausgabe.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Speichert den gewünschten Wert zur gewünschten Zeit
	 * @param zeit
	 * @param wert
	 */
	public synchronized void speichereWert( double wert )
	{
		try
		{
			_ausgabe.write(Zeitverwaltung.gibInstanz().gibZeit() + ", " + wert + "\n");
			_ausgabe.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Speichert den gewünschten Wert zur gewünschten Zeit mit dem angegebenen Kommentar
	 * @param zeit
	 * @param wert
	 */
	public synchronized void speichereWert( long zeit, int wert, String kommentar )
	{
		try
		{
			_ausgabe.write(zeit + ", " + wert + ", " + kommentar + "\n");
			_ausgabe.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Speichert den gewünschten Wert zur gewünschten Zeit mit dem angegebenen Kommentar
	 * @param zeit
	 * @param wert
	 */
	public synchronized void speichereWert( long zeit, double wert, String kommentar )
	{
		try
		{
			_ausgabe.write(zeit + ", " + wert + ", " + kommentar + "\n");
			_ausgabe.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Speichert den gewünschten Wert zur gewünschten Zeit mit dem angegebenen Kommentar
	 * @param zeit
	 * @param wert
	 */
	public synchronized void speichereWert( double wert, String kommentar )
	{
		try
		{
			_ausgabe.write(Zeitverwaltung.gibInstanz().gibZeit() + ", " + wert + ", " + kommentar + "\n");
			_ausgabe.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
