package jadex.bdi.examples.antworld;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The update action for showing the destination field of the ant.
 */
public class UpdateDestinationAction extends SimplePropertyObject implements ISpaceAction {
	// -------- constants --------

	/** The destination and id of the ant. */
	public static final String DESTINATION = "destination";
	public static final String ANT_ID = "antID";

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

		IVector2 destination = (IVector2) parameters.get(DESTINATION);
		Object ownerAgentId = (Object) parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject[] obj = space.getSpaceObjectsByType("destinationSign");
		// Boolean feelsGravitation = (Boolean)
		// parameters.get(GravitationListener.FEELS_GRAVITATION);

		// Update destination of object.
		for (int i = 0; i < obj.length; i++) {
			ISpaceObject destinationSign = obj[i];
			if (destinationSign.getProperty(ANT_ID).equals(ownerAgentId.toString())) {
				// if (feelsGravitation.booleanValue()) {
				// ((Space2D)
				// space).destroySpaceObject(destinationSign.getId());
				// } else {
				((Space2D) space).setPosition(destinationSign.getId(), destination);
				return null;
			}

		}
		// }

		// create Object for the first time
		Map props = new HashMap();
		props.put(Space2D.PROPERTY_POSITION, destination);
		props.put(ANT_ID, ownerAgentId.toString());
		space.createSpaceObject("destinationSign", props, null);
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * 
	 * @return ID of the action
	 */
	public Object getId() {
		// todo: remove here or from application xml?
		return "updateDestination";
	}
}
