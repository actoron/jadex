package sodekovs.bikesharing.simulation;

import jadex.base.Starter;
import sodekovs.bikesharing.disposition.DispositionsAgent;
import sodekovs.old.bikesharing.verkehrsteilnehmer.FahreRad;

/**
 * Startet die Simulation
 * @author dagere
 *
 */
public class SimulationStarter
{
	public static boolean simulationsModus = false; //Gegenteil: Visualisierungsmodus -> lieber immer im Visualisierungsmodus
	public static String dispositionsAgentenTyp = DispositionsAgent.ENGPASS;
	
	public static void main( String args[] )
	{
		String datei = "E:/Workspaces/Jadex/Jadex Test Instanz/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/fahrten.csv";
		if ( args.length > 0 )
		{
			datei = args[0];
		}
		System.out.println("Schreibe in: " + datei);
		String datei2 = "E:/Workspaces/Jadex/Jadex Test Instanz/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/FahrgastDaten.csv";
		if ( args.length > 1 )
		{
			datei2 = args[1];
			StartSimulationProzess.setzFahrgastDaten( datei2 );  
		}
		System.out.println("Fahrgastdaten aus: " + datei2);
		if ( args.length > 2 )
		{
			if ( args[2].equals("simulation") )
			{
				simulationsModus = true;
				System.out.println("Simulation: true");
			}
			else
			{
				simulationsModus = false;
				System.out.println("Simulation: false");
			}
		}
		if ( args.length > 3 )
		{
			if ( args[3].equals( DispositionsAgent.ENGPASS) )
			{
				dispositionsAgentenTyp = DispositionsAgent.ENGPASS;
			}
			if ( args[3].equals( DispositionsAgent.SCHWELLWERT) )
			{
				dispositionsAgentenTyp = DispositionsAgent.SCHWELLWERT;
			}
			if ( args[3].equals( DispositionsAgent.VERGANGENHEIT) )
			{
				dispositionsAgentenTyp = DispositionsAgent.VERGANGENHEIT;
			}
//			System.out.println("Übergeben: " + args[3]);
			System.out.println("Dispositionsagent: " + dispositionsAgentenTyp);
		}
		
		if ( args.length > 4 )
		{
			String disponentenAusgabe = args[4];
			DispositionsAgent.ausgabedatei = disponentenAusgabe;
			System.out.println("Dispositionsausgabe in: " + disponentenAusgabe);
		}
		
		StartSimulation("src/de/Platform.component.xml", "meineApplikation", 1000, datei );
		
		
	}
	
	public static void StartSimulation( String platformxml, String conf, int ticks, String ausgabedatei )
	{
		
		String args[] = new String[4];
		args[0] = "-conf";
		args[1] = platformxml;
		args[2] = "-configname";
		args[3] = conf;
//		jadex.base.Starter.createPlatform( args );
		Starter.main(args);
		
		System.out.println("Plattform gestartet!");
		
		FahreRad.setzAusgabe( ausgabedatei );
	}
}
