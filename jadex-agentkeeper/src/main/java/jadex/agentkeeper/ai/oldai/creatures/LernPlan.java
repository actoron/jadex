package jadex.agentkeeper.ai.oldai.creatures;
//package agentkeeper.monster;
//
///**
// * Jeder Warlock braucht 4 Bibleotheksfelder die dann auch beim Lernen alle besucht werden
// * 
// * @author 7willuwe
// * 
// */
//
//import jadex.extension.envsupport.environment.ISpaceObject;
//import jadex.extension.envsupport.math.IVector2;
//import jadex.extension.envsupport.math.Vector2Double;
//import jadex.extension.envsupport.math.Vector2Int;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import agentkeeper.InitMapProcess;
//import agentkeeper.auftragsverwaltung.Gebaeude;
//import agentkeeper.gui.GUIInformierer;
//import agentkeeper.wegfindung.ASternSuche;
//
//
//@SuppressWarnings("serial")
//public class LernPlan extends MonsterPlan {
//	public static int LERNZEIT = 20;
//
//	public boolean lerne() {
//
//		boolean gelernt = false;
//
//		// Wir quasi 4x ausgeführt und danach die Bib wieder unbesetzt damit es
//		// so aussieht als wenn der Warlock durch die Bib läuft
//
//		LinkedList<ISpaceObject> besetzte = new LinkedList<ISpaceObject>();
//
//		for (int i = 0; i < 4; i++) {
//
//			IVector2 zielpos = gibNaechsteBib();
//			if (!(zielpos == null)) {
//				erreicheZiel(zielpos, true);
//
//				ISpaceObject feld = gibFeld(zielpos);
//				if (feld.getType().equals(InitMapProcess.LIBRARY)) {
//					if (feld.getProperty("besetzt").equals("0")) {
//						feld.setProperty("besetzt", "1");
//						besetzte.add(feld);
//						warte(LERNZEIT);
//						if (i == 3) {
//							gelernt = true;
//						}
//					}
//					else {
//						lerne();
//					}
//
//				}
//				else {
//					fail();
//				}
//			}
//			else {
//				gelernt = false;
//			}
//		}
//		for (ISpaceObject feld : besetzte) {
//			feld.setProperty("besetzt", "0");
//		}
//		return gelernt;
//
//	}
//
//	/**
//	 * Sucht die nächste bib
//	 * 
//	 * @return
//	 */
//	private IVector2 gibNaechsteBib() {
//		List<Gebaeude> bibs= gibNaechstesGebaeude( InitMapProcess.LIBRARY );
//		
//
//		ASternSuche suche;
//		int minGKosten = Integer.MAX_VALUE;
//		int gKosten;
//		Vector2Int tmp, minKostenPunkt = null;
//		for (int i = 0; i < bibs.size(); i++) {
//			ISpaceObject feld = gibFeld(bibs.get(i).gibPos());
//			if (feld.getProperty("besetzt").equals("0")) {
//				suche = new ASternSuche((Vector2Double) _mypos, bibs.get(i).gibPos(), grid, false);
//				gKosten = suche.gibPfadKosten();
//				tmp = bibs.get(i).gibPos();
//				if (gKosten < minGKosten) {
//					minGKosten = gKosten;
//					minKostenPunkt = tmp;
//				}
//			}
//
//		}
//		return minKostenPunkt;
//	}
//
//	@Override
//	protected void aktion() {
//		ladAuftrag(false);
//
//		boolean gelernt = lerne();
//
//		if (gelernt) {
//			int zauberkraftalt = (Integer) getBeliefbase().getBelief("zauberkraft").getFact();
//
//			getBeliefbase().getBelief("zauberkraft").setFact(zauberkraftalt + 1);
//			getBeliefbase().getBelief("stimmung").setFact(1.0);
//
//			int altforschung = (Integer) grid.getProperty("forschung");
//			int neuforschung = altforschung + 1;
//			grid.setProperty("forschung", neuforschung);
//
//			int level = ((Integer) _avatar.getProperty("level"));
//
//			int levelneu = level + 1;
//
//			_avatar.setProperty("level", levelneu);
//
//			GUIInformierer.aktuallisierung();
//		}
//		
//		_ausfuehr = false;
//		
//	}
//
//}
