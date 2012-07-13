package sodekovs.old.bikesharing.datenkonvertierung;

import jadex.extension.envsupport.math.Vector2Double;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DispositionsAnalysator
{
	public static void main( String args[] )
	{
		if ( args.length < 1 )
		{
			System.out.println("Benutzung: java -jar DispositionsAnalysator.jar dispoAnalyse.csv");
			return;
		}
		String fahrtenDatei = "";
		if ( args.length > 1 && !args[1].equals("NICHTS"))
		{
			fahrtenDatei = args[1];
		}
		String ausgabeDatei = "";
		if ( args.length > 2 )
		{
			ausgabeDatei = args[2];
		}
		String disposAnalyse = args[0];
		
		File datei = new File(disposAnalyse);
		File fahrten = (fahrtenDatei.equals(""))? null : new File( fahrtenDatei );
		File ausgabe = (ausgabeDatei.equals(""))? null : new File( ausgabeDatei );
		File ausgabe_f = (ausgabeDatei.equals(""))? null : new File( "F_" + ausgabeDatei );
		
		try
		{
			BufferedReader br = new BufferedReader( new FileReader( datei ) );
			BufferedWriter writer = (ausgabeDatei.equals("")) ? null : new BufferedWriter( new FileWriter( ausgabe ) );
			
			String zeile;
			
			double weg = 0.0, wegGesamt = 0.0, wegQuadradsumme = 0.0;
			int anzahl = 0;
			int tage = 1;
			int zeit = 0;
			long transportAnzahl = 0, transportAnzahlGesamt = 0, transportQuadradsumme = 0;
			
			Vector2Double altePos = new Vector2Double(0,0);
			System.out.println("Starte Analyse von " + datei);
			while ( (zeile = br.readLine()) != null )
			{
				String teile[] = zeile.split(",");
				
				
				Vector2Double posNeu = gibPos( teile[5], teile[6] );
				double abstand = posNeu.getDistance( altePos ).getAsDouble();
				weg += abstand;
				
				teile[7] = teile[7].replace(" ", "");
				if ( teile[7].matches("[0-9]*") )
				{
					transportAnzahl += new Integer( teile[7] );
				}
				
				int zeitNeu = gibZeit( teile[2].replace(" ", ""));
				if ( zeitNeu < zeit )
				{
					System.out.println("Gesamtweg Tag " + tage + ": " + weg + " Transportmenge: " + transportAnzahl);
					if ( !(ausgabeDatei.equals("")) )
					{
						writer.write(tage + " " + weg + " " + transportAnzahl + "\n");
					}
					
					wegQuadradsumme += weg * weg;
					transportQuadradsumme += transportAnzahl * transportAnzahl;
					wegGesamt += weg;
					weg = 0;
					transportAnzahlGesamt += transportAnzahl;
					transportAnzahl = 0;
					tage++;
				}
				zeit = zeitNeu;
				
			}
			if ( !(ausgabeDatei.equals("")) )
			{
				writer.flush();
			}
			
			tage -= 1;
			System.out.println("Analyse beendet, Tage: " + tage);
			System.out.println("Gesamt: Weg: " + wegGesamt + " Menge: " + transportAnzahlGesamt);
			System.out.println("Durchschnitt: Weg: " + wegGesamt / tage + " Menge: " + transportAnzahlGesamt / tage);
			System.out.println("Standardabweichung: " + Math.sqrt( (wegQuadradsumme / tage ) - Math.pow(wegGesamt / tage, 2) ) + 
					" Menge: " + Math.sqrt( (transportQuadradsumme / tage ) - Math.pow(transportAnzahlGesamt / tage, 2) ) );
//			System.out.println( (transportQuadradsumme / tage ) + " " + Math.pow(transportAnzahlGesamt / tage, 2) );
			if ( ! fahrten.equals("" ) )
			{
				BufferedReader br_fahrten = new BufferedReader( new FileReader( fahrten ) );
				BufferedWriter writer_fahrten = (ausgabe.equals("")) ? null : new BufferedWriter( new FileWriter( ausgabe_f ) );
				
				System.out.println("Starte Analyse von " + datei);
				
				int anz = 0, gesamtAnz = 0;
				tage = 1; zeit = 0; 
				while ( (zeile = br_fahrten.readLine()) != null )
				{
					String teile[] = zeile.split(",");
					anz++;
					int zeitNeu = gibZeit( teile[2].replace(" ", ""));
					if ( zeitNeu < zeit )
					{
						System.out.println("Gesamtweg Tag " + tage + ": " + anz);
						if ( !(ausgabeDatei.equals("") ) )
						{
							writer_fahrten.write(tage + " " + anz + "\n");
						}
						
						gesamtAnz+=anz;
						anz= 0;
						tage++;
					}
					zeit = zeitNeu;
				}
				
				if ( writer_fahrten != null )
				{
					writer_fahrten.flush();
				}
				tage--;
				System.out.println("Analyse beendet, Tage: " + tage);
				System.out.println("Gesamt: Anzahl: " + gesamtAnz);
				System.out.println("Durchschnitt: " + gesamtAnz / tage);
			}
			
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Nimmt Zeit als String der Form XX:XX entgegen und gibt sie als Wert zurück
	 * @param zeit
	 * @return
	 */
	public static int gibZeit( String zeit)
	{
//		System.out.println(zeit);
		if ( !zeit.matches("[0-9]*:[0-9]*") )
		{
			return 0;
		}
		else
		{
			String teile[] = zeit.split(":");
//			System.out.println(teile[0] + " " + teile[1]);
			int ret = new Integer( teile[0] ) * 60 + new Integer( teile[1] );
			return ret;
		}
	}
	
	/**
	 * Gibt die Position, die aus den übergebenen Strings konstruiert werden kann, zurück
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	public static Vector2Double gibPos( String pos1, String pos2 )
	{
		Vector2Double vec1 = new Vector2Double( new Double( pos1 ), new Double( pos2 ) );
		return vec1;
	}
}
