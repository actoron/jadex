package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;

/**
 * Plan, bei dem der Agent auswaehlt, was er als naechstes tun moechte Derzeitig
 * geht er früh vom wohn- zum arbeitsort und abends zurück; eine Erweiterung um
 * mehr Orte ist hier denkbar
 * 
 * @author David Georg Reichelt
 * 
 */
public class ZielWaehlPlan extends Plan
{
	private static final long serialVersionUID = 803152055432793643L;

	public static String WOHNORT = "wohnort";
	public static String ARBEITSORT = "arbeitsort";
	public static String TRINKORT = "trinkort";
	public static String GEARBEITET = "hatGearbeitet";
	public static String NAECHSTEBEWEGUNG = "naechsteBewegung";
	public static String ZIEL = "ziel";
	public static String ERREICHEZIEL = "erreicheziel";
	public static String MORGENSSTART = "morgensStart";
	public static String ABENDSENDE = "abendsEnde";

	public static int morgenStart, abendsEnde;
	ISpaceObject _avatar;


	/**
	 * Konstruktor, blabla -> kommentieren
	 */
	public ZielWaehlPlan()
	{
		morgenStart = 9 * 60;
		abendsEnde = 18 * 60;
//		IApplicationExternalAccess app = (IApplicationExternalAccess) getScope().getParent();
//
//		Grid2D space = (Grid2D) app.getSpace("simulationsspace");
//		_avatar = space.getAvatar(getComponentIdentifier());
//		_avatar = SelbstBewegPlan.gibAvatar(getScope().getParent(), this);
		_avatar = (ISpaceObject) getBeliefbase().getBelief("avatar").getFact();
		
		Vector2Double wohnort = (Vector2Double) _avatar.getProperty(WOHNORT);
		Vector2Double arbeitsort = (Vector2Double) _avatar.getProperty(ARBEITSORT);

//		Vector2Int wohnortInt = new Vector2Int(wohnort.getXAsInteger(), wohnort.getYAsInteger());
//		Vector2Int arbeitsortInt = new Vector2Int(arbeitsort.getXAsInteger(), arbeitsort.getYAsInteger());

		morgenStart = (Integer) _avatar.getProperty(MORGENSSTART);
		abendsEnde = (Integer) _avatar.getProperty(ABENDSENDE);
		Double fahrradpraeferenz = (Double) _avatar.getProperty(SucheNeuenWegPlan.FAHRRADPRAEFERENZ);
		
		getBeliefbase().getBelief(WOHNORT).setFact(wohnort);
		getBeliefbase().getBelief(ARBEITSORT).setFact(arbeitsort);
		
//		System.out.println("Start: " + morgenStart + " " + abendsEnde + " " + fahrradpraeferenz);
		
		getBeliefbase().getBelief(MORGENSSTART).setFact( morgenStart );
		getBeliefbase().getBelief(ABENDSENDE).setFact(abendsEnde);
		getBeliefbase().getBelief(SucheNeuenWegPlan.FAHRRADPRAEFERENZ).setFact(fahrradpraeferenz);
	}

	@Override
	public void body()
	{
//		if ( 1 != 2 )
//		{
//			return;
//		}
		Vector2Double wohnort = (Vector2Double) getBeliefbase().getBelief(WOHNORT).getFact();
		Vector2Double arbeitsort = (Vector2Double) getBeliefbase().getBelief(ARBEITSORT).getFact();
		Vector2Double trinkort = (Vector2Double) _avatar.getProperty(TRINKORT);
//		Vector2Double essort = (Vector2Double) _avatar.getProperty("essort");
//		Vector2Double arbeitsort = new Vector2Double( ( 30 + Math.random()*20 ), ( 40 + Math.random()*10 ) );
//		Vector2Double trinkort = new Vector2Double( ( 30 + Math.random()*20 ), ( 40 + Math.random()*10 ) );
		// long mitternacht = Zeitverwaltung.gibInstanz().gibMitternacht();

		long tag = 0;
		int startzeit = (int) ((7 + (Math.random() * 2 - 1)) * 60);

//		System.out.println("Startzeit: " + startzeit);

		while (true) // TODO: Simulationsende einfügen
		{
			
			if ( Zeitverwaltung.gibInstanz().gibTageszeit() > 18 * 60 )
			{
				waitFor( (24*60 -  Zeitverwaltung.gibInstanz().gibTageszeit())*100 );
			}
			if ( Zeitverwaltung.gibInstanz().gibTageszeit() < startzeit )
			{
				waitFor( (startzeit - Zeitverwaltung.gibInstanz().gibTageszeit()) *100 );
			}
			
//			while (Zeitverwaltung.gibInstanz().gibTageszeit() < startzeit
//					|| Zeitverwaltung.gibInstanz().gibTageszeit() > 18 * 60) 
//			{
//				waitForTick();
//			}

//			System.out.println("  Starte Tag: " + Zeitverwaltung.gibInstanz().gibTageszeit());

//			IGoal subgoal2 = createGoal(ERREICHEZIEL);
//			subgoal2.getParameter(ZIEL).setValue(essort);
//			dispatchSubgoalAndWait(subgoal2);
			
			IGoal subgoal = createGoal(ERREICHEZIEL);
			subgoal.getParameter(ZIEL).setValue(arbeitsort);
			dispatchSubgoalAndWait(subgoal);

			
			
//			System.out.println("  Komme an: " + Zeitverwaltung.gibInstanz().gibTageszeit());

			long wartezeit = (Zeitverwaltung.gibInstanz().gibTageszeit() < 12 * 60) ? (7 * 60)
					: (19 * 60 - Zeitverwaltung.gibInstanz().gibTageszeit());
			if (wartezeit < 0)
			{
				wartezeit = 0; // Wenn es schon nach 19 Uhr ist, gehts gleich
							  // wieder nach Hause..
			}
//			System.out.println("Wartezeit: " + wartezeit);
			waitFor(wartezeit * 100); // Wartet 7 Stunden

//			System.out.println("  Beende Arbeit: " + Zeitverwaltung.gibInstanz().gibTageszeit());
			
			subgoal = createGoal(ERREICHEZIEL);
			subgoal.getParameter(ZIEL).setValue(trinkort);
			dispatchSubgoalAndWait(subgoal);
			
			waitFor(30 * 100);
			
			subgoal = createGoal(ERREICHEZIEL);
			subgoal.getParameter(ZIEL).setValue(wohnort);
			dispatchSubgoalAndWait(subgoal);

//			System.out.println("  Bin zuhause: " + Zeitverwaltung.gibInstanz().gibTageszeit());

		}

	}

}
