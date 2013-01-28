package jadex.agentkeeper.ai.oldai.creatures;

import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.agentkeeper.game.state.missions.Gebaeude;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.List;



@SuppressWarnings("serial")
public class SchlafenPlan extends MonsterPlan {
	public static int SCHLAFZEIT = 120;

	@Override
	public void aktion() {
		ladAuftrag(false);

		boolean geschlafen = schlafe();

		if (geschlafen) {
			getBeliefbase().getBelief("schlaf").setFact(10.0);
		}
		
		_ausfuehr = false;
	}

	public boolean schlafe() {
		IVector2 zielpos = gibNaechsteLair();
		if (!(zielpos == null)) {
			erreicheZiel(zielpos, true);

			ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(zielpos, grid);
			if (feld.getType().equals(InitMapProcess.LAIR)) {
				if (feld.getProperty("besetzt").equals("0")) {
					feld.setProperty("besetzt", "1");
					warte(SCHLAFZEIT);
					feld.setProperty("besetzt", "0");
				}
				else {
					schlafe();
				}

			}
			else {
				fail();
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Sucht die naechste Lair
	 * 
	 * @return
	 */
	private IVector2 gibNaechsteLair() {
		List<Gebaeude> lairs = gibNaechstesGebaeude( InitMapProcess.LAIR );

		AStarSearch suche;
		int minGKosten = Integer.MAX_VALUE;
		int gKosten;
		Vector2Int tmp, minKostenPunkt = null;
		for (int i = 0; i < lairs.size(); i++) {
			ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(lairs.get(i).gibPos(), grid);
			if (feld.getProperty("besetzt").equals("0")) {
				suche = new AStarSearch( _mypos, lairs.get(i).gibPos(), grid, false);
				gKosten = suche.gibPfadKosten();
				tmp = lairs.get(i).gibPos();
				if (gKosten < minGKosten) {
					minGKosten = gKosten;
					minKostenPunkt = tmp;
				}
			}

		}
		return minKostenPunkt;
	}

}
