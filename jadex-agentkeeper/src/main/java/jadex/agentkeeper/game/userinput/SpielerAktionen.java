package jadex.agentkeeper.game.userinput;

/**
 * Diese Klasse beinhaltet die Spieleraktionen hinsichtlich des Bauens von Geb�uden und Globale Zauber
 * 
 * @author 7willuwe
 * 
 */
import java.util.HashMap;


import jadex.agentkeeper.ai.oldai.basic.MoveAction;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

public class SpielerAktionen {
	private Grid2D _space;

	public SpielerAktionen(Grid2D space) {
		_space = space;

	}

	public void bauGebauede(Vector2Int zielpos, String typ, int kosten) {
		

		for (Object o : _space.getSpaceObjectsByGridPosition(zielpos, "field")) {
			if (o instanceof ISpaceObject) {
				ISpaceObject feld = (ISpaceObject) o;
				String type = (String) feld.getProperty("type");
				if (type.equals(InitMapProcess.CLAIMED_PATH)) {
					InitMapProcess.gebaeuedeverwalter.machGebaeude( (Vector2Int) zielpos, typ );
					if (typ.equals(InitMapProcess.LAIR)) {
						
//						InitMapProcess.lairliste.add((Vector2Double) zielpos);
						feld.setProperty("besetzt", "0");
					}
					else
						if (typ.equals(InitMapProcess.HATCHERY)) {
							feld.setProperty("huehner", 5.0);
							feld.setProperty("besetzt", "0");
						}
						else
							if (typ.equals(InitMapProcess.TRAININGROOM)) {
								feld.setProperty("besetzt", "0");

							}
							else
								if (typ.equals(InitMapProcess.LIBRARY)) {
									feld.setProperty("besetzt", "0");

								}
								else
									if (typ.equals(InitMapProcess.TORTURE)) {
										feld.setProperty("besetzt", "0");
									}

					feld.setProperty("type", typ);
					feld.setProperty("bearbeitung", new Integer(0));

					int altg = (Integer) _space.getProperty("gold");
					int newg = altg - kosten;

					// GesamtGold anpassen
					_space.setProperty("gold", newg);
//					GUIInformierer.aktuallisierung();

				}
			}
		}
	}

	public void zauberImp(IVector2 zielpos, int kosten) {
		if (begehbar((Vector2Double) zielpos)) {

			String type = "imp";
			HashMap<String, Object> props = new HashMap<String, Object>();
			props.put("type", "imp");
			props.put("spieler", new Integer(1) );
			props.put(Space2D.PROPERTY_POSITION, zielpos);
			_space.createSpaceObject(type, props, null);

			int altmana = (Integer) _space.getProperty("mana");
			int neumana = altmana - kosten;

			// GesamtGold anpassen
			_space.setProperty("mana", neumana);
//			GUIInformierer.aktuallisierung();

		}
	}

	private boolean begehbar(Vector2Double punkt) {

		for (Object o : _space.getSpaceObjectsByGridPosition(punkt, "field")) {
			if (o instanceof ISpaceObject) {
				ISpaceObject blub = (ISpaceObject) o;
				if (MoveAction.ALLOWFIELDS.contains(blub.getProperty("type"))) {
					return true;
				}
			}
		}

		return false;
	}

}
