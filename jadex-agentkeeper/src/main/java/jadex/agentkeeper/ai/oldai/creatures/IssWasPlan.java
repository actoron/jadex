package jadex.agentkeeper.ai.oldai.creatures;

import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.agentkeeper.game.state.missions.Gebaeude;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.List;



/**
 * Verwaltet den Plan des Goblins zum Essen
 * 
 * isst 2x an (möglicherweise) verschienden Hatcherys
 * 
 * @author 8reichel
 * @author 7willuwe
 * 
 */
@SuppressWarnings("serial")
public class IssWasPlan extends MonsterPlan {
	public static int ESSZEIT = 45;

	IVector2 _zielpos;
	
	public IssWasPlan()
	{
		ladAuftrag(false);
	}
	
	private void esse() {
		_zielpos = gibNaechsteHatchery();
		if (!(_zielpos == null)) {
			erreicheZiel(_zielpos, true);

			ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(_zielpos, grid);
			if (feld.getProperty("type").equals(InitMapProcess.HATCHERY)) {
				double alt = (Double) feld.getProperty("huehner");
				if (alt >= InitMapProcess.monsteressverbrauch && (feld.getProperty("besetzt").equals("0"))) {
					feld.setProperty("besetzt", "1");
					warte(ESSZEIT);
					feld.setProperty("besetzt", "0");
					feld.setProperty("huehner", alt - InitMapProcess.monsteressverbrauch);
					getBeliefbase().getBelief("hunger").setFact(0.0);
					getBeliefbase().getBelief("zufrieden").setFact(true);
				}
				else {
					esse();
				}
			}
		}
		else {
			getBeliefbase().getBelief("zufrieden").setFact(false);
		}

	}

	/**
	 * Sucht den naechsten Hühnerhof
	 * 
	 * @return
	 */
	private IVector2 gibNaechsteHatchery() {
		List<Gebaeude> hatcherys = gibNaechstesGebaeude( InitMapProcess.HATCHERY);;

		AStarSearch suche;
		int minGKosten = Integer.MAX_VALUE;
		int gKosten;
		Vector2Int tmp, minKostenPunkt = null;
		for (int i = 0; i < hatcherys.size(); i++) {
			ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(hatcherys.get(i).gibPos(), grid);
			double huehner = (Double) feld.getProperty("huehner");
			if ((huehner >= InitMapProcess.monsteressverbrauch) && (feld.getProperty("besetzt").equals("0"))) {
				suche = new AStarSearch((Vector2Double) _mypos, hatcherys.get(i).gibPos(), grid, false);
				gKosten = suche.gibPfadKosten();
				tmp = hatcherys.get(i).gibPos();
				if (gKosten < minGKosten) {
					minGKosten = gKosten;
					minKostenPunkt = tmp;
				}
			}
		}
		return minKostenPunkt;
	}
	@Override
	protected void aktion() {
		esse(); 
		testeEinheiten();
		esse(); // 2 mal? Derbe unsauber
		_ausfuehr = false;
		
	}

}
