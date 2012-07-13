package sodekovs.old.bikesharing.datenkonvertierung;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;


/**
 * Analysiert, welche Stationen in der gegebenen Datei wie oft genutzt wurden
 * @author dagere
 *
 */
public class DatenAnalysator
{
	public static void main(String args[]) throws IOException
	{
		String dateiA = "realeFahrten.csv";

		if (args.length > 0 && args[0] != null)
		{
			System.out.println("DateiA: " + args[0]);
			dateiA = args[0];
		}

		StadtRadFahrtenKonverter.initialisiereRadStationen();

		File fA = new File(dateiA);

		BufferedReader readerA = new BufferedReader(new FileReader(fA));

		List<Fahrt> fahrtenA = new LinkedList<Fahrt>();

		AehnlichkeitsBerechner.machListe(fahrtenA, readerA);

		auswerten(fahrtenA);
	}

	private static void auswerten(List<Fahrt> liste)
	{
		Map<FahrradVerleihStation, Integer> anzahlStart = new HashMap<FahrradVerleihStation, Integer>();
		Map<FahrradVerleihStation, Integer> anzahlZiel = new HashMap<FahrradVerleihStation, Integer>();
		int anzahlS = 0;
		for (FahrradVerleihStation station : FahrradVerleihStationen.gibInstanz().gibStationen())
		{
			anzahlS = 0;
			for (Fahrt f : liste)
			{
				if (f.ziel.equals(station.gibPosition()))
				{
					anzahlS++;
				}
			}
			anzahlZiel.put(station, new Integer(anzahlS));
		}

		for (FahrradVerleihStation station : FahrradVerleihStationen.gibInstanz().gibStationen())
		{
			anzahlS = 0;
			for (Fahrt f : liste)
			{
				if (f.start.equals(station.gibPosition()))
				{
					anzahlS++;
				}
			}
			anzahlStart.put(station, new Integer(anzahlS));
		}

		int zielGleichStart = 0;
		Map<Fahrt, Integer> haeufigsteFahrten = new HashMap<Fahrt, Integer>();
		for (Fahrt f : liste)
		{
			if ( f.start.equals( f.ziel ) )
			{
				zielGleichStart++;
			}
			boolean gefunden = false;
			for (Map.Entry<Fahrt, Integer> entry : haeufigsteFahrten.entrySet())
			{
				if (entry.getKey().equals(f))
				{
					entry.setValue( entry.getValue() + 1 );
					gefunden = true;
				}
			}
			if ( ! gefunden )
			{
				haeufigsteFahrten.put( f, 1 ); 
			}
		}
		
		System.out.println("ZielGleichStart: " + zielGleichStart );

		System.out.println("Häufigste Ziele: ");
		gibGeordneteMapAus(anzahlZiel, 7);

		System.out.println("Häufigste Starte: ");
		gibGeordneteMapAus(anzahlStart, 7);
		
		System.out.println("Häufigste Fahrten: ");
		gibGeordneteMapAusFahrt(haeufigsteFahrten, 50 );

	}

	public static void gibGeordneteMapAus(Map<FahrradVerleihStation, Integer> map, int anzahl)
	{
		Map<FahrradVerleihStation, Integer> map2 = new HashMap<FahrradVerleihStation, Integer>(map);
		for (int i = 0; i < anzahl; i++)
		{
			Map.Entry<FahrradVerleihStation, Integer> max = null;
			for (Map.Entry<FahrradVerleihStation, Integer> fvs : map2.entrySet())
			{
				if (max == null || fvs.getValue() > max.getValue())
				{
					max = fvs;
				}
			}
			if (max != null)
			{
				System.out.println("Anzahl: " + max.getValue() + " " + max.getKey().gibName());
				map2.remove(max.getKey());
			}
			else
			{
				return;
			}
		}
	}
	
	public static void gibGeordneteMapAusFahrt(Map<Fahrt, Integer> map, int anzahl)
	{
		Map<Fahrt, Integer> map2 = new HashMap<Fahrt, Integer>(map);
		for (int i = 0; i < anzahl; i++)
		{
			Map.Entry<Fahrt, Integer> max = null;
			for (Map.Entry<Fahrt, Integer> fvs : map2.entrySet())
			{
				if (max == null || fvs.getValue() > max.getValue())
				{
					max = fvs;
				}
			}
			if (max != null)
			{
				System.out.println("Anzahl: " + max.getValue() + " " + max.getKey() );
				map2.remove(max.getKey());
			}
			else
			{
				return;
			}
		}
		
		System.out.println("Start == Ziel: ");
		
		map2 = new HashMap<Fahrt, Integer>(map);
		for (int i = 0; i < anzahl*2; i++)
		{
			Map.Entry<Fahrt, Integer> max = null;
			for (Map.Entry<Fahrt, Integer> fvs : map2.entrySet())
			{
				if ( (max == null || fvs.getValue() > max.getValue()) && fvs.getKey().start.equals( fvs.getKey().ziel) )
				{
					max = fvs;
				}
			}
			if (max != null)
			{
				System.out.println("Anzahl(I: " + i + "): " + max.getValue() + " " + max.getKey() );
				map2.remove(max.getKey());
			}
			else
			{
				return;
			}
		}
	}
}
