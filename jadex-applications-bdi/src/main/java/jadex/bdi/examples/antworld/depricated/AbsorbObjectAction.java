package jadex.bdi.examples.antworld.depricated;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.commons.SimplePropertyObject;

import java.util.Map;

/**
 * Absorbs an object.
 */
public class AbsorbObjectAction extends SimplePropertyObject implements ISpaceAction {
	// -------- constants --------

	// -------- methods --------

	/**
	 * Performs the action.
	 * 
	 * @param parameters
	 *            parameters for the action
	 * @param space
	 *            the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space) {
		 System.out.println("Absorb objects action: "+parameters);

		ISpaceObject[] objects = space.getSpaceObjectsByType(ManageGravitationProcess.GRAVITATION_CENTER);
		Vector2Int target = (Vector2Int) parameters.get(Space2D.PROPERTY_POSITION);

		//increase count of absorbed objects
		for (int i = 0; i < objects.length; i++) {
			if (target.equals(objects[i].getProperty(Space2D.PROPERTY_POSITION))) {
				int objectCount = ((Integer) objects[i].getProperty(ManageGravitationProcess.ABSORBED_OBJECTS)).intValue();
				objectCount++;
				objects[i].setProperty(ManageGravitationProcess.ABSORBED_OBJECTS, Integer.valueOf(objectCount));
				break;
			}
		}

		return null;
	}

	/**
	 * Returns the ID of the action.
	 * 
	 * @return ID of the action
	 */
	public Object getId() {
		// todo: remove here or from application xml?
		return "go";
	}
}
