package jadex.agentkeeper.ai.oldai.heroes;

import jadex.agentkeeper.ai.oldai.basic.GehHinUndArbeit;
import jadex.agentkeeper.game.state.missions.Gebaudeverwalter;
import jadex.agentkeeper.game.state.missions.IAuftragsverwalter;
import jadex.agentkeeper.init.map.process.InitMapProcess;

@SuppressWarnings("serial")
public class KaputtmachPlan extends GehHinUndArbeit {
	public static int BESETZDAUER = 15;

	private GegnerVerwalter _gegnerverwalter;

	public KaputtmachPlan() {
		_verbrauchsgrad = 0;
		
		_gegnerverwalter = (GegnerVerwalter) _avatar.getProperty("auftragsverwalter");
	}

	@Override
	public void aktion() {
		ladAuftrag();

		// Wirklich etwas zum Kaputtmachen da?
		if (isCorrectField(_zielpos, _auf.gibZieltyp())) {
			erreicheZiel(_zielpos, true);

			bearbeite(_zielpos, BESETZDAUER);

			setze(_zielpos, InitMapProcess.DIRT_PATH, false);

			Gebaudeverwalter g = (Gebaudeverwalter) grid.getProperty(InitMapProcess.GEBAEUDELISTE);
			if (!_auf.gibZieltyp().equals(InitMapProcess.CLAIMED_PATH)) {
				g.loeschen(_zielpos);
			}

		}
		else {
			// System.out.println("Ziel nicht gefunden: "+_auf.gibZieltyp() +
			// ", stattdessen: " + gibFeld( _zielpos ).getProperty("type") );
			// System.out.println("Position: "+_auf.gibZiel() );
		}
		_ausfuehr = false;
	}

	@Override
	protected void gegnerNaehe(long id) {

	}

	@Override
	public void aborted() {
		_gegnerverwalter.istTod((String) _avatar.getId().toString());
	}

}
