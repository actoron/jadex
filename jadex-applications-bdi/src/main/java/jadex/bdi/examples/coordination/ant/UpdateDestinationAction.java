package jadex.bdi.examples.coordination.ant;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
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
		 
		// (Object) parameters.get(IAgentAction.OBJECT_ID).toString());

		IVector2 destination = (IVector2) parameters.get(DESTINATION);
		Object ownerAgentId = (Object) parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject[] obj = space.getSpaceObjectsByType("destinationSign");
		Boolean feelsGravitation = (Boolean) parameters.get(GravitationListener.FEELS_GRAVITATION);

		// Update destination of object. Destroy destination sign, if the agent
		// feels gravitation
		for (int i = 0; i < obj.length; i++) {
			ISpaceObject destinationSign = obj[i];
//			System.out.println("Checking performing update destinationSign for id: " + destinationSign.getId());
//			System.out.println("Pos of destination sign: " + destinationSign.getProperty(Space2D.POSITION) + "vs graviCenterPos: " + destination.toString());
			if (destinationSign.getProperty(ANT_ID).equals(ownerAgentId.toString())) {
//				System.out.println("Destroy Destination Sign? " + feelsGravitation);
				if (feelsGravitation.booleanValue()) {				
					((Space2D) space).destroySpaceObject(destinationSign.getId());					
				} else {
					destinationSign.setProperty(Space2D.POSITION, destination);
//					((Space2D)space).setPosition(oid, pos);
					((Space2D)space).setPosition(destinationSign.getId(), destination);
					
				}
				return null;
			}
		}

		// create Object for the first time
		Map props = new HashMap();
		props.put(Space2D.POSITION, destination);
		props.put(ANT_ID, ownerAgentId.toString());
		space.createSpaceObject("destinationSign", props, null, null);

//		 ((Space2D)space).setPosition(destinationSign, destination);

		// System.out.println("Go action: "+obj.getProperty(IAgentAction.ACTOR_ID)+" "+pos);

		// obj.fireObjectEvent(new ObjectEvent(POSITION_CHANGED));

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
