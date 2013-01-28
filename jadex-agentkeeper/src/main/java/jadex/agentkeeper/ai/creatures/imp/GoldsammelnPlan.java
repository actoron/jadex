package jadex.agentkeeper.ai.creatures.imp;

import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.agentkeeper.game.state.missions.Auftrag;
import jadex.agentkeeper.game.state.missions.Auftragsverwalter;
import jadex.agentkeeper.game.state.missions.Gebaeude;
import jadex.agentkeeper.game.state.missions.Gebaudeverwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.bdi.runtime.IGoal;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.List;



/**
 * Plan f�rs Gold Sammeln
 * 
 * @author 8reichel
 * @author 7willuwe
 * 
 */
@SuppressWarnings("serial")
public class GoldsammelnPlan extends ImpPlan {

	public static final int ABBAUZEIT = 15;
	public static final int AUFNEHMZEIT = 2;

	public GoldsammelnPlan()
	{
		_verbrauchsgrad = 0;
	}
	
	
	@Override
	public void aktion() {
		ladAuftrag();

		erreicheZiel(_zielpos, true);

		// Was n das bitte? ^^
		_mypos = new Vector2Double( _zielpos.getXAsDouble(), _zielpos.getYAsDouble() );

		ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(_mypos, grid);
		if (feld.getProperty("type").equals(InitMapProcess.GOLD2)) {
			// System.out.println("Goldsammelnplan: Gold :D");
		}
		else {
			// System.out.println("Goldsammelnplan: Achtung, kein Gold!");
		}

		int gold = Integer.valueOf((feld.getProperty("amount")).toString());

		String kapazitaet = (getBeliefbase().getBelief("kapazitaet").getFact()).toString();
		int kapa = Integer.valueOf(kapazitaet);
		String arbeitS = (getBeliefbase().getBelief("arbeit").getFact()).toString();
		int arbeit = Integer.valueOf(arbeitS);
		int realabbau;
		boolean nochgold = false;
		if (kapa > gold) {
			realabbau = gold;
		}
		else {
			realabbau = kapa;
			nochgold = true;

		}
		bearbeite(_mypos, realabbau * AUFNEHMZEIT * arbeit);
		// System.out.println("gold: " + (gold - realabbau));
		getBeliefbase().getBelief("gold").setFact(new Integer(realabbau));

		feld.setProperty("amount", gold - realabbau);
		if (!nochgold) {
			feld.setProperty("type", InitMapProcess.DIRT_PATH);
		}

		IVector2 treasurypos = findeNaechsteSchatztruhe(_mypos.copy());

		erreicheZiel(treasurypos, false);
		
		

		ISpaceObject feld2 = InitMapProcess.getFieldTypeAtPos(treasurypos, grid);

		int altg = (Integer) grid.getProperty("gold");
		int newg = altg + realabbau;

		feld2.setProperty("amount", newg);

		// GesamtGold anpassen
		grid.setProperty("gold", newg);
//		GUIInformierer.aktuallisierung();

		// IMP Tragekapa anpassen
		getBeliefbase().getBelief("gold").setFact(new Integer(0));

		if (nochgold) {
			IGoal sammele = createGoal(Auftragsverwalter.GOLDSAMMELN);
			Auftrag auf = new Auftrag(Auftragsverwalter.GOLDSAMMELN, _zielpos);
			sammele.getParameter("auftrag").setValue(auf);
			dispatchSubgoalAndWait(sammele);
		}
		
		_ausfuehr = false;
	}

	/**
	 * @param mypos
	 * 
	 * @return
	 */
	
	//TODO: Auslagern?
	private IVector2 findeNaechsteSchatztruhe(IVector2 mypos) {
		Gebaudeverwalter g = (Gebaudeverwalter)grid.getProperty( InitMapProcess.GEBAEUDELISTE );
		List<Gebaeude> truen = g.gibGebaeude( InitMapProcess.TREASURY );

		AStarSearch suche;
		int minGKosten = Integer.MAX_VALUE;
		int gKosten;
		IVector2 tmp, minKostenPunkt = null;
		for (int i = 0; i < truen.size(); i++) {
			suche = new AStarSearch( mypos, truen.get(i).gibPos(), grid, false);
			gKosten = suche.gibPfadKosten();
			tmp = truen.get(i).gibPos();
			if (gKosten < minGKosten) {
				minGKosten = gKosten;
				minKostenPunkt = tmp;
			}
		}
		return minKostenPunkt;
	}
	
	@Override
	public void aborted()
	{
//		_auftragsverwalter.neuerAuftrag( _auf );
	}

}
