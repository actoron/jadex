package agentkeeper.map;

import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;
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

//		if(begehbar(pos, space))
//		{
//			
//			grid.setPosition(id, pos);
//		}
//		else
//		{
//			
//			System.out.println("Not allowed to go to: " + pos);
//			throw new RuntimeException();
//		}

		return null;
	}
	
	private boolean begehbar(IVector2 punkt, IEnvironmentSpace space) {

		//TODO:: umschreiben
		for (Object o : ((Grid2D) space).getSpaceObjectsByGridPosition(punkt, null)) {
			if (o instanceof ISpaceObject) {
				ISpaceObject blub = (ISpaceObject) o;
				if (MoveAction.ALLOWFIELDS.contains(blub.getType())) {
					return true;
				}
				else
				{
					System.out.println("Position von nicht begehbar: " + blub.getProperty(Space2D.PROPERTY_POSITION));
					System.out.println("Type von nicht begehbar: " + blub.getType());
					
				}
			}
		}
		return false;
	}
}
