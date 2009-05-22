package jadex.bdi.examples.coordination.ant;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This action is responsible for updating the trace route of the ant.
 */
public class TraceRouteAction extends SimplePropertyObject implements ISpaceAction {
	// -------- constants --------

	/** The destination and id of the ant. */
	public static final String POSITION = "position";
	public static final String ANT_ID = "antID";
	public static final String ROUND = "round";

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
//		System.out.println("performing update destinationSign for: " + (Object) parameters.get(IAgentAction.OBJECT_ID).toString());

		IVector2 position = (IVector2) parameters.get(POSITION);
		Object ownerAgentId = (Object) parameters.get(ISpaceAction.OBJECT_ID);
		Integer round = (Integer) parameters.get(ROUND);
		
		// add this position to trace route
		Map props = new HashMap();
		props.put(Space2D.POSITION, position);
		props.put(ANT_ID, ownerAgentId.toString());
		props.put(ROUND, new Integer(0));		
//		props.put("creation_age", new Double(clock.getTick()));
//		props.put("clock", clock);
		space.createSpaceObject("traceRoute", props, null, null);

		// ((Space2D)space).setPosition(destinationSign, destination);

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
