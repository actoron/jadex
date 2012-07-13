package sodekovs.old.bikesharing.fahrrad;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.LinkedList;
import java.util.List;


/**
 * Eine Klasse, um eine Fahrradverleihstation mit den an der Station
 * befindlichen Fahrrädern und der Gesamtkapazität der Station zu speichern
 * 
 * @author dagere
 * 
 */
public class FahrradVerleihStation
{
//	public static String ausgabedatei = "VerleihStationsDaten.csv";
	
	public static final String FAHRRADANZAHL = "fahrradanzahl";
	private List<Fahrrad> _fahrraeder;
	private int _kapazitaet;
	private Vector2Double _position;
	private ISpaceObject _iso;
	private String _name;

	public FahrradVerleihStation(Vector2Double position, int kapazitaet, int fahrraeder, String name)
	{
		_fahrraeder = new LinkedList<Fahrrad>();
		for (int i = 0; i < fahrraeder; i++)
		{
			_fahrraeder.add(new Fahrrad(position, this));
		}
		_kapazitaet = kapazitaet;
		_position = position;
		_name = name;
	}
	
	public void setzObjekt( ISpaceObject iso )
	{
		_iso = iso;
	}


	/**
	 * Gibt die Gesamtkapazität der Fahrradverleihstation aus
	 * 
	 * @return Kapazität der Fahrradverleihstation
	 */
	public int gibKapazitaet()
	{
		return _kapazitaet;
	}

	/**
	 * Gibt die Anzahl der freien Stellplätze aus
	 * 
	 * @return Anzahl der freien Stellplätze
	 */
	public int gibFreiePlaetze()
	{
		return _kapazitaet - _fahrraeder.size();
	}
	
	public String gibName()
	{
		return _name;
	}

	/**
	 * Gibt die Anzahl der vorhandenen Fahrräder aus
	 * 
	 * @return Anzahl der Fahrräder
	 */
	public int gibFahrradAnzahl()
	{
		return _fahrraeder.size();
	}

	public Vector2Double gibPosition()
	{
		return _position;
	}

	public String toString()
	{
		String ret = "Position: " + _position + " Name: " + _name + " Stellplätze: " + _kapazitaet + " Fahrräder: " + _fahrraeder.size();
		return ret;
	}

	/**
	 * Wird diese Methode aufgerufen, wird ein Fahrrad entliehen; dabei wird die
	 * Referenz des Fahrrades übergeben, und gleichzeitig aus der
	 * Entliehen-Liste gelöscht
	 * 
	 * @return
	 */
	public synchronized Fahrrad leihFahrrad()
	{
		if ( _fahrraeder.size() > 0 )
		{
			
			speichereDaten();
			Fahrrad ret = _fahrraeder.get(0);
			_fahrraeder.remove(ret);
			speichereDaten();
			
			_iso.setProperty(FAHRRADANZAHL, _fahrraeder.size());
			
			
			return ret;
		}
		else
		{
			return null;
		}
		
	}

	/**
	 * Gibt das übergebene Fahrrad zurück, d.h. speichert es in die Liste
	 * 
	 * @param f
	 *            Fahrrad, das zurückgegeben wird
	 * @return
	 */
	public synchronized boolean gibRadZurueck(Fahrrad f)
	{
//		int anzahl = (Integer) _iso.getProperty(FAHRRADANZAHL);

		if (_fahrraeder.size() < _kapazitaet)
		{
			speichereDaten();
			_fahrraeder.add(f);
			_iso.setProperty(FAHRRADANZAHL, _fahrraeder.size());
			speichereDaten();
			
//			System.out.println("true, size: " + _fahrraeder.size());
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void speichereDaten()
	{
//		Datensammler.gibDatensammler(ausgabedatei).speichereWert( _fahrraeder.size(), _name );
	}
	
	public boolean equals( Object o )
	{
		if ( o instanceof FahrradVerleihStation )
		{
			FahrradVerleihStation f = (FahrradVerleihStation) o;
			String name1 = f.gibName().replace(" ", "");
			name1 = name1.replace("ß", "");
			name1 = name1.replace("ä", "");
			name1 = name1.replace("ü", "");
			name1 = name1.replace("ö", "");
			String name2 = gibName().replace(" ", "");
			name2 = name2.replace("ß", "");
			name2 = name2.replace("ä", "");
			name2 = name2.replace("ü", "");
			name2 = name2.replace("ö", "");
			if ( name1.equals( name2 ) || f.gibPosition().equals( _position ) )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
