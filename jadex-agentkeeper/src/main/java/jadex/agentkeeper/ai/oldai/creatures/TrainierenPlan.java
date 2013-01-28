package jadex.agentkeeper.ai.oldai.creatures;

/**
 * Trainieren geht auf die Stimmung der Monster
 * 
 * @author 7willuwe
 * 
 */
import jadex.agentkeeper.ai.pathfinding.AStarSearch;
import jadex.agentkeeper.game.state.missions.Gebaeude;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.List;



@SuppressWarnings("serial")
public class TrainierenPlan extends MonsterPlan {
	public static int TRAINIERZEIT = 50;

	public void aktion() {
		ladAuftrag(false);

		boolean trainiert = trainiere();

		if (trainiert) {
			int staerkealt = (Integer) getBeliefbase().getBelief("staerke").getFact();

			getBeliefbase().getBelief("staerke").setFact(staerkealt + 1);
			getBeliefbase().getBelief("stimmung").setFact(1.0);

			int level = ((Integer) _avatar.getProperty("level"));

			int levelneu = level + 1;

			_avatar.setProperty("level", levelneu);
		}
		
		_ausfuehr = false;
	}

	public boolean trainiere() {
		IVector2 zielpos = gibNaechsteTrainingroom();
		if ( zielpos == null )
		{
			return false;
		}
		if (!(zielpos == null)) {
			erreicheZiel(zielpos, true);

			ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(zielpos, grid);
			if (feld.getProperty("type").equals(InitMapProcess.TRAININGROOM)) {
				if (feld.getProperty("besetzt").equals("0")) {
					feld.setProperty("besetzt", "1");
					warte(TRAINIERZEIT);
					feld.setProperty("besetzt", "0");
					return true;
				}
				else {
					trainiere();
				}

			}
			else {
				fail();
			}

		}

		return false;
	}

	/**
	 * Sucht den naechsten Lair
	 * 
	 * @return
	 */
	private IVector2 gibNaechsteTrainingroom() {
		List<Gebaeude> trainingrooms= gibNaechstesGebaeude( InitMapProcess.TRAININGROOM );

		AStarSearch suche;
		int minGKosten = Integer.MAX_VALUE;
		int gKosten;
		Vector2Int tmp, minKostenPunkt = null;
		for (int i = 0; i < trainingrooms.size(); i++) {
			ISpaceObject feld = InitMapProcess.getFieldTypeAtPos(trainingrooms.get(i).gibPos(), grid);
			if (feld.getProperty("besetzt").equals("0")) {
				suche = new AStarSearch(_mypos, trainingrooms.get(i).gibPos(), grid, false);
				gKosten = suche.gibPfadKosten();
				tmp = trainingrooms.get(i).gibPos();
				if (gKosten < minGKosten) {
					minGKosten = gKosten;
					minKostenPunkt = tmp;
				}
			}

		}
		return minKostenPunkt;
	}

}
