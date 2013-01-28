package jadex.agentkeeper.ai.oldai.basic;

import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.commons.SimplePropertyObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * The move action.
 */
public class MoveAction extends SimplePropertyObject implements ISpaceAction {
	public static Set<String> ALLOWFIELDS;

	static {
		ALLOWFIELDS = new HashSet<String>();
		ALLOWFIELDS.add(InitMapProcess.DIRT_PATH);
		ALLOWFIELDS.add(InitMapProcess.CLAIMED_PATH);
		ALLOWFIELDS.add(InitMapProcess.WATER);
		ALLOWFIELDS.add(InitMapProcess.LAVA);
		ALLOWFIELDS.add(InitMapProcess.LAIR);
		ALLOWFIELDS.add(InitMapProcess.HATCHERY);
		ALLOWFIELDS.add(InitMapProcess.GOLD2);
		ALLOWFIELDS.add(InitMapProcess.PORTAL);
		ALLOWFIELDS.add(InitMapProcess.TRAININGROOM);
		ALLOWFIELDS.add(InitMapProcess.LIBRARY);
		ALLOWFIELDS.add(InitMapProcess.HERO);
	}

	/**
	 * Perform an action.
	 */
	public Object perform(Map parameters, IEnvironmentSpace space) {
		
		Grid2D grid = (Grid2D) space;

		Object id = parameters.get(ISpaceAction.OBJECT_ID);
		IVector2 pos = (IVector2) parameters.get(GetPosition.PARAMETER_POSITION);
		
		grid.setPosition(id, pos);

		return null;
	}

}
