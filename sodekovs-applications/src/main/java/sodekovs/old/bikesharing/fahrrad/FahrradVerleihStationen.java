package sodekovs.old.bikesharing.fahrrad;

import jadex.extension.envsupport.math.IVector2;

import java.util.LinkedList;
import java.util.List;

/**
 * Verwaltet alle Fahrradverleihstationen, und bietet den Verkehrsteilnehmern Möglichkeiten wie
 * das Suchen der nächsten Fahrradverleihstation im Umkreis; ist als Singleton implementiert, da
 * diese Funktionalität einmal für das komplette Programm vorhanden sein muss, und da sie global
 * zugreifbar sein sollte
 * @author dagere
 *
 */
public class FahrradVerleihStationen
{
	private List<FahrradVerleihStation> _verleihStationen;
	private static FahrradVerleihStationen _singleton;
	
	private FahrradVerleihStationen( List<FahrradVerleihStation> verleihStationen )
	{
		_verleihStationen = verleihStationen;
	}
	
	public static void erstellInstanz( List<FahrradVerleihStation> verleihStationen )
	{
		System.out.println("Instanz von FahrradVerleihStationen erstellt");
		_singleton = new FahrradVerleihStationen( verleihStationen );
	}
	
	/**
	 * Gibt die Instanz des Singletons aus
	 * @return
	 */
	public static FahrradVerleihStationen gibInstanz( )
	{
		return _singleton;
	}
	
	/**
	 * Sucht auf der übergebenen List mit Fahrradverleihstationen die Station mit der kleinsten Distanz
	 * zur gegebenen Position; so können Stationen mit bestimmten Eigenschaften, zum Beispiel leer, voll,
	 * ... gesucht werden
	 * @param position		Position, in deren Nähe eine Fahrradverleihstation gesucht werden soll
	 * @param auswahlListe	Liste, auf der die Station gesucht werden soll
	 * @return				Nächste Fahrradverleihstation
	 */
	private FahrradVerleihStation gibNaechsteStation( final IVector2 position, final List<FahrradVerleihStation> auswahlListe )
	{
		FahrradVerleihStation naechsteStation = auswahlListe.get(0);
		double naechsteDistanz = naechsteStation.gibPosition().getDistance( position ).getAsDouble();
		
		for ( FahrradVerleihStation f : auswahlListe )
		{
			double distanz = f.gibPosition().getDistance( position ).getAsDouble();
			if ( distanz < naechsteDistanz )
			{
				naechsteDistanz = distanz;
				naechsteStation = f;
			}
		}
		return naechsteStation;
	}
	
	/**
	 * Gibt die Fahrradverleihstation aus, die zur angegebenen Position die geringste Distanz hat
	 * @param position	Position, in deren Nähe die Verleihstation gesucht werden soll
	 * @return			Fahrradverleihstation, deren Distanz am geringsten ist
	 */
	public FahrradVerleihStation gibNaechsteStation( IVector2 position )
	{
		return gibNaechsteStation( position, _verleihStationen );
	}
	
	/**
	 * Gibt die nächste freie Station aus; ist allerdings für Verkehrsteilnehmer
	 * nicht mehr relevant, weil man ja neuerdings Fahrräder einfach so abstellen kann..
	 * Allerdings ist das noch relevant für die Fahrzeuge, die die Räder umparken
	 * @param position	Position, in deren Nähe die Verleihstation gesucht werden soll
	 * @return			Fahrradverleihstation, deren Distanz am geringsten ist und die frei ist
	 */
	public FahrradVerleihStation gibNaechsteFreieStation( IVector2 position )
	{
		List<FahrradVerleihStation> freie = new LinkedList<FahrradVerleihStation>();
		for ( FahrradVerleihStation f : _verleihStationen )
		{
			if ( f.gibFreiePlaetze() > 0 )
			{
				freie.add( f );
			}
		}
		return gibNaechsteStation(position, freie);
	}
	
	/**
	 * Gibt die Station aus, die zurzeit am wenigsten Fahrräder hat
	 * @return Station mit den wenigsten Rädern
	 */
	public FahrradVerleihStation gibStationMitWenigstenRaedern()
	{
		if ( _verleihStationen.size() == 0 )
		{
			return null;
		}
		
		FahrradVerleihStation wenigsteRaeder = _verleihStationen.get(0);
		for ( FahrradVerleihStation f : _verleihStationen )
		{
			if ( f.gibFahrradAnzahl() < wenigsteRaeder.gibFahrradAnzahl() )
			{
				wenigsteRaeder = f;
			}
		}
		return wenigsteRaeder;
	}
	
	/**
	 * Gibt die Station aus, die zurzeit am wenigsten Fahrräder hat
	 * @return Station mit den wenigsten Rädern
	 */
	public FahrradVerleihStation gibStationMitMeistenRaedern()
	{
		if ( _verleihStationen.size() == 0 )
		{
			return null;
		}
		
		FahrradVerleihStation meisteRaeder = _verleihStationen.get(0);
		for ( FahrradVerleihStation f : _verleihStationen )
		{
			if ( f.gibFahrradAnzahl() > meisteRaeder.gibFahrradAnzahl() )
			{
				meisteRaeder = f;
			}
		}
		return meisteRaeder;
	}

	/**
	 * Gibt eine Liste mit allen FahrradVerleihStationen aus
	 * @return	Liste aus FahrradVerleihStation-Elementen
	 */
	public List<FahrradVerleihStation> gibStationen() {
		return _verleihStationen;
	}

	/**
	 * Gibt die Station mit dem übergebenen Namen zurück
	 * @param stationsName	Name der Station
	 * @return				Gewünschte Station
	 */
	public FahrradVerleihStation gibStation(String stationsName) {
		stationsName = stationsName.replace(" ", "");
		for ( FahrradVerleihStation f : _verleihStationen )
		{
//			System.out.println("Station: " + stationsName + " " + f.gibName());
			if (f.gibName().replace(" ", "").equals( stationsName ) )
			{
				return f;
			}
			else
			{
//				System.out.println("Nicht gleich");
			}
		}
		
		//Notfallsuche, für Datensätze, bei denen Umlaute verschwunden sind
		stationsName = stationsName.replace("ä", "");
		stationsName = stationsName.replace("ö", "");
		stationsName = stationsName.replace("ü", "");
		stationsName = stationsName.replace("ß", "");
		stationsName = stationsName.replace("?", "");
//		System.out.println("Stationsname: " + stationsName);
		for ( FahrradVerleihStation f : _verleihStationen )
		{
			String temp = f.gibName().replace("ä", "ae");
			temp = temp.replace("ö", "oe");
			temp = temp.replace("ü", "ue");
			temp = temp.replace("ß", "");
			temp = temp.replace(" ", "");
//			System.out.println("Vergleich mit: " + temp);
			if ( temp.equals( stationsName ) )
			{
				return f;
			}
			else
			{
//				System.out.println("Nicht gleich");
			}
		}
		
		
		
		return null;
	}
}
