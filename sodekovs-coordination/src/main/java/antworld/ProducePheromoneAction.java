package antworld;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This action is responsible for updating the trace route of the ant, i.e. put a pheromone on the place that an ant has visited.
 */
public class ProducePheromoneAction extends SimplePropertyObject implements ISpaceAction {
	// -------- constants --------

	/** The destination and id of the ant. */
	public static final String POSITION = "position";
	public static final String ANT_ID = "antID";
//	public static final String ROUND = "round";
	public static final String STRENGTH = "strength";

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
		// System.out.println("performing update destinationSign for: " +
		// (Object) parameters.get(IAgentAction.OBJECT_ID).toString());
		Grid2D grid = (Grid2D)space;
		
		IVector2 position = (IVector2) parameters.get(POSITION);
//		Object ownerAgentId = (Object) parameters.get(ISpaceAction.OBJECT_ID);
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getAvatar(owner);
		Integer strength = (Integer) parameters.get(STRENGTH);
	
		Collection pheromones = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.PROPERTY_POSITION), "pheromone");
//		ISpaceObject pickedFood = (ISpaceObject)(pheromones!=null? pheromones.iterator().next(): null);
		ISpaceObject pheromone = (ISpaceObject)(pheromones==null? null : pheromones.iterator().next());
		
		//create new pheromone
		if(pheromone == null){
			Map props = new HashMap();
			props.put(Space2D.PROPERTY_POSITION, position);
//			props.put(ANT_ID, so.getProperty(ACTOR_ID));
			props.put(ANT_ID, so.getId());
			props.put(STRENGTH, new Integer(20));		
			space.createSpaceObject("pheromone", props, null);
		}
		//update old pheromone
		else{
			int oldStrength = ((Integer)pheromone.getProperty(STRENGTH)).intValue();
			pheromone.setProperty(STRENGTH, new Integer(20 + oldStrength));
			pheromone.setProperty(ANT_ID, so.getId());
//			pheromone.setProperty(STRENGTH, new Integer(10));
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
		return "updateDestination";
	}
}
