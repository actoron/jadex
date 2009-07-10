package jadex.bdi.examples.antworld.foraging;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This action is responsible for updating the trace route of the ant.
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
		IAgentIdentifier owner = (IAgentIdentifier)parameters.get(ISpaceAction.ACTOR_ID);
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
			props.put(STRENGTH, new Integer(10));		
			space.createSpaceObject("pheromone", props, null);
		}
		//update old pheromone
		else{
			pheromone.setProperty(ANT_ID, so.getId());
			pheromone.setProperty(STRENGTH, new Integer(10));
		}
		
//		Collection foodSources = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.PROPERTY_POSITION), "foodSource");
//		ISpaceObject foodSource = (ISpaceObject) foodSources.iterator().next();
//		int stock = ((Integer)foodSource.getProperty("stock")).intValue();
//		foodSource.setProperty("stock", new Integer(stock-1));
//
//		// add this position to trace route
//		Map props = new HashMap();
//		props.put(Space2D.PROPERTY_POSITION, position);
//		props.put(ANT_ID, ownerAgentId.toString());
//		props.put(ROUND, new Integer(0));		
////		props.put("creation_age", new Double(clock.getTick()));
////		props.put("clock", clock);
//		space.createSpaceObject("pheromone", props, null);
//
//
//		// check whether there is already a traceRoute on this position
//		// yes: update "round" and "owner" value of old trace route
//		// no: create new trace route object
//		ISpaceObject objects[] = space.getSpaceObjectsByType("pheromone");
//		ISpaceObject oldTraceRoute = null;

//		for (int i = 0; i < objects.length; i++) {
//			IVector2 pos = (IVector2) objects[i].getProperty(POSITION);
//			if (pos.equals(position)) {
//				oldTraceRoute = objects[i];
//				break;
//			}
//		}
//
//		
//		
//		if (oldTraceRoute == null) {
//			// add this position to trace route
//			props = new HashMap();
//			props.put(Space2D.PROPERTY_POSITION, position);
//			props.put(ANT_ID, ownerAgentId.toString());
//			props.put(ROUND, new Integer(0));
//			// props.put("creation_age", new Double(clock.getTick()));
//			// props.put("clock", clock);
//			space.createSpaceObject("pheromone", props, null);
//		} else {
//			oldTraceRoute.setProperty(ANT_ID, ownerAgentId.toString());
//			oldTraceRoute.setProperty(ROUND, new Integer(0));
//		}
		// ((Space2D)space).setPosition(destinationSign, destination);

		// System.out.println("Go action: "+obj.getProperty(IAgentAction.ACTOR_ID)+" "+pos);

		// obj.fireObjectEvent(new ObjectEvent(POSITION_CHANGED));

		return null;
		
		
//		// add this position to trace route
//		Map props = new HashMap();
//		props.put(Space2D.PROPERTY_POSITION, position);
//		props.put(ANT_ID, ownerAgentId.toString());
//		props.put(ROUND, new Integer(0));		
////		props.put("creation_age", new Double(clock.getTick()));
////		props.put("clock", clock);
//		space.createSpaceObject("pheromone", props, null);
//
//
//		// check whether there is already a traceRoute on this position
//		// yes: update "round" and "owner" value of old trace route
//		// no: create new trace route object
//		ISpaceObject objects[] = space.getSpaceObjectsByType("pheromone");
//		ISpaceObject oldTraceRoute = null;
//
//		for (int i = 0; i < objects.length; i++) {
//			IVector2 pos = (IVector2) objects[i].getProperty(POSITION);
//			if (pos.equals(position)) {
//				oldTraceRoute = objects[i];
//				break;
//			}
//		}
//
//		
//		
//		if (oldTraceRoute == null) {
//			// add this position to trace route
//			props = new HashMap();
//			props.put(Space2D.PROPERTY_POSITION, position);
//			props.put(ANT_ID, ownerAgentId.toString());
//			props.put(ROUND, new Integer(0));
//			// props.put("creation_age", new Double(clock.getTick()));
//			// props.put("clock", clock);
//			space.createSpaceObject("pheromone", props, null);
//		} else {
//			oldTraceRoute.setProperty(ANT_ID, ownerAgentId.toString());
//			oldTraceRoute.setProperty(ROUND, new Integer(0));
//		}
//		// ((Space2D)space).setPosition(destinationSign, destination);
//
//		// System.out.println("Go action: "+obj.getProperty(IAgentAction.ACTOR_ID)+" "+pos);
//
//		// obj.fireObjectEvent(new ObjectEvent(POSITION_CHANGED));
//
//		return null;
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
