package sodekovs.old.bikesharing.datenkonvertierung;

import jadex.extension.envsupport.math.IVector2;

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
 * Berechnet die Ähnlichkeit zwischen 2 Fahrtendateien, die im üblichen Format
 * als .csv eingegeben werden, und visualisiert diese
 * 
 * @author 8reichel
 * 
 */
public class AehnlichkeitsAnalysator
{
	public static int maxAbweich;
	public static Map<FahrradVerleihStation, Integer> abweichungen;

	public static void main(String args[]) throws IOException
	{
		String dateiA = "fahrtenAusgabeTest20T.csv";
		String dateiB = "realeFahrten3.csv";
		
		if ( args.length > 0 && args[0] != null )
		{
			System.out.println("DateiA: " + args[0]);
			dateiA = args[0];
		}
		
		if ( args.length > 1 && args[1] != null )
		{
			System.out.println("DateiB: " + args[1]);
			dateiB= args[1];
		}
		
		StadtRadFahrtenKonverter.initialisiereRadStationen();

		File fA = new File( dateiA );
		File fB = new File( dateiB );

		BufferedReader readerA = new BufferedReader(new FileReader(fA));
		BufferedReader readerB = new BufferedReader(new FileReader(fB));

		
		List<Fahrt> fahrtenA = new LinkedList<Fahrt>();
		List<Fahrt> fahrtenB = new LinkedList<Fahrt>();

		
		readerA = new BufferedReader(new FileReader(fA));
		readerB = new BufferedReader(new FileReader(fB));
//		
		machListe(fahrtenA, readerA);
		machListe(fahrtenB, readerB);
		
//		fahrtenA.addAll( fahrtenB );
		
		Map<FahrradVerleihStation, Integer> ankuenfteA = new HashMap<FahrradVerleihStation, Integer>();
		Map<FahrradVerleihStation, Integer> ankuenfteB = new HashMap<FahrradVerleihStation, Integer>();
		
		Map<FahrradVerleihStation, Integer> abfahrtenA = new HashMap<FahrradVerleihStation, Integer>();
		Map<FahrradVerleihStation, Integer> abfahrtenB = new HashMap<FahrradVerleihStation, Integer>();
		
		int summe = fahrtenA.size() * 2 + fahrtenB.size() * 2;
		
		machAbfahrtAnkunftTabelle(fahrtenA, abfahrtenA, ankuenfteA);
		machAbfahrtAnkunftTabelle(fahrtenB, abfahrtenB, ankuenfteB);
		
		int f1 = vergleicheStationsAnzahlMengen(ankuenfteA, abfahrtenA);
		int f2 = vergleicheStationsAnzahlMengen(ankuenfteB, abfahrtenB);
		
		
		int fehler = vergleicheStationsAnzahlMengen(ankuenfteA, ankuenfteB) + vergleicheStationsAnzahlMengen(abfahrtenA, abfahrtenB);
		
		System.out.println("Fehler: " +ankuenfteA.size() + " " + f1 + " /  " + ankuenfteB.size() + " "  + f2);
		System.out.println("Gesamtausleihvorgänge: " + summe + " Fehler: " + fehler + " Anteil: " + (1 - ((double) fehler/summe) ) );
		
		veroeffentliche( ankuenfteA );
		
		StartSimulation("Platform2.application.xml", "meineApplikation2");
		
	}
	
	public static void veroeffentliche ( Map<FahrradVerleihStation, Integer> daten )
	{
		maxAbweich = 0;
		for ( Map.Entry<FahrradVerleihStation, Integer> entry : daten.entrySet() )
		{
			System.out.println("Entry: " + entry.getKey().gibName());
			if ( entry.getValue() > maxAbweich )
			{
				maxAbweich = entry.getValue();
			}
		}
		abweichungen = new HashMap<FahrradVerleihStation, Integer>();
		abweichungen.putAll( daten );
//		abweichungen.addAll( daten );
	}
	
	public static void StartSimulation( String platformxml, String conf )
	{
		
		String args[] = new String[4];
		args[0] = "-conf";
		args[1] = platformxml;
		args[2] = "-configname";
		args[3] = conf;
		jadex.base.Starter.createPlatform( args );
	}
	
	private static int vergleicheStationsAnzahlMengen( Map<FahrradVerleihStation, Integer> ankuenfteA, Map<FahrradVerleihStation, Integer> ankuenfteB)
	{
		int anz = 0;
		for ( Map.Entry<FahrradVerleihStation, Integer> entryA : ankuenfteA.entrySet() )
		{
			boolean gefunden = false;
			for ( Map.Entry<FahrradVerleihStation, Integer> entryB : ankuenfteB.entrySet() )
			{
				if ( entryB.getKey().equals( entryA.getKey() ) ) 
				{
//					System.out.println("Diff " + entryB.getKey() + " " + Math.abs( entryB.getValue() - entryA.getValue() ));
					anz+= Math.abs( entryB.getValue() - entryA.getValue() ); 
					gefunden = true;
					break;
				}
			}
			if ( !gefunden )
			{
				System.out.println("Nicht gefunden: " + entryA.getKey().gibName() + entryA.getValue());
				anz += entryA.getValue();
			}
		}
		return anz;
	}
	
	private static void machAbfahrtAnkunftTabelle( List<Fahrt> fahrtenA, Map<FahrradVerleihStation, Integer> abfahrtenA, Map<FahrradVerleihStation, Integer> ankuenfteA )
	{
		for (Fahrt a : fahrtenA)
		{
			FahrradVerleihStation start = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( a.start );
			FahrradVerleihStation ziel = FahrradVerleihStationen.gibInstanz().gibNaechsteStation( a.ziel );
			registrierPos(start, abfahrtenA);
			registrierPos(ziel, ankuenfteA);
			
		}
	}
	
	private static void registrierPos( FahrradVerleihStation station, Map<FahrradVerleihStation, Integer> karte )
	{
		if ( karte.containsKey( station ) )
		{
			Integer anzahl = karte.get( station );
			karte.put( station, anzahl + 1);
		}
		else
		{
			karte.put(station, new Integer( 1 ) );
		}
	}
	
	private static void auswerten( List<Fahrt> liste )
	{
		Map<FahrradVerleihStation, Integer> anzahl = new HashMap<FahrradVerleihStation, Integer>();
		int anzahlS = 0;
		for ( FahrradVerleihStation station : FahrradVerleihStationen.gibInstanz().gibStationen() )
		{
			anzahlS = 0;
			for ( Fahrt f : liste )
			{
				if ( f.ziel.equals( station.gibPosition() ) )
				{
//					System.out.println("Fahrt: " + f.zeit + " " + FahrradVerleihStationen.gibInstanz().gibNaechsteStation( f.start ).gibName() + " |  "
//							+ FahrradVerleihStationen.gibInstanz().gibNaechsteStation( f.ziel ).gibName());
					anzahlS++;
				}
			}
			anzahl.put( station, new Integer( anzahlS ) );
		}
		for ( Map.Entry<FahrradVerleihStation, Integer> entry : anzahl.entrySet() )
		{
			System.out.println(entry.getKey().gibName()+ ": " + entry.getValue() );
		}
	}

	// Differenz: 26438
	// Anzahl A: 524 Anzahl B: 3979
	// Nicht gefunden A: 466 Nicht gefunden B: 3921
	// Differenz: 5686181
	// Anzahl A: 1328 Anzahl B: 3979
	// Nicht gefunden A: 1254 Nicht gefunden B: 3905
	// Differenz: 5941947

	static void machListe(List<Fahrt> liste, BufferedReader reader) throws NumberFormatException, IOException
	{
		String zeile = "";

		while ((zeile = reader.readLine()) != null)
		{
			// System.out.println("Zeile: " + zeile);
			String teile[] = zeile.split(",");

			String zeitstring = teile[2].replace(" ", "");
			String startString = teile[4];
			String endeString = teile[5];
			// System.out.println("Zeit: " + zeitstring + " Start: " +
			// startString + " Ende: " + endeString );
			int zeit = new Integer(zeitstring.substring(0, zeitstring.indexOf(":"))) * 60
					+ new Integer(zeitstring.substring(zeitstring.indexOf(":") + 1));
			IVector2 start = FahrradVerleihStationen.gibInstanz().gibStation(startString).gibPosition();
			IVector2 ziel = FahrradVerleihStationen.gibInstanz().gibStation(endeString).gibPosition();
			Fahrt f = new Fahrt(zeit, start, ziel);
			liste.add(f);
		}
	}
}
