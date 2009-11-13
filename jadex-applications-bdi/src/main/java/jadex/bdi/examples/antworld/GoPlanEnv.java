package jadex.bdi.examples.antworld;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.garbagecollector.GoAction;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.Plan.SyncResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Go to a specified position.
 */public class GoPlanEnv extends Plan {
	/**
	 * The plan body.
	 */
	public void body() {
		Space2D env = (Space2D) getBeliefbase().getBelief("env").getFact();
//		Boolean hasGravitation = (Boolean) getBeliefbase().getBelief("hasGravitation").getFact();
		IVector2 size = env.getAreaSize();
		IVector2 target = (IVector2) getParameter("pos").getValue();
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
//		myself.setProperty(GravitationListener.FEELS_GRAVITATION, hasGravitation);

		//		System.out.println("#GoPlanEnv# Plan started to walk from " + myself.getProperty(Space2D.PROPERTY_POSITION) + " to " + target + " for ant: " + myself.getId());		
		// Update destination and gravitationSensor of ant on space
		Map params = new HashMap();
		params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getComponentIdentifier()).getId());
		params.put(UpdateDestinationAction.DESTINATION, target);
//		params.put(GravitationListener.FEELS_GRAVITATION, hasGravitation);
		// params.put("owner", myself.getId());
//		SyncResultListener srl = new SyncResultListener();
//		env.performSpaceAction("updateDestination", params, srl);
		env.performSpaceAction("updateDestination", params);
//		srl.waitForResult();

		while (!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION))) {
			IVector2 mypos = (IVector2) myself.getProperty(Space2D.PROPERTY_POSITION);
			String dir = null;
			int mx = mypos.getXAsInteger();
			int tx = target.getXAsInteger();
			int my = mypos.getYAsInteger();
			int ty = target.getYAsInteger();

			assert mx != tx || my != ty;

			if (mx != tx) {
				dir = GoAction.RIGHT;
				int dx = Math.abs(mx - tx);
				if (mx > tx && dx <= size.getXAsInteger() / 2)
					dir = GoAction.LEFT;
			} else {
				dir = GoAction.DOWN;
				int dy = Math.abs(my - ty);
				if (my > ty && dy <= size.getYAsInteger() / 2)
					dir = GoAction.UP;
			}
			// Produce new pheromone, if ant carries food.
			if (getBeliefbase().getBelief("carriedFood").getFact() != null) {
				params = new HashMap();
				params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
				params.put(ProducePheromoneAction.POSITION, mypos);
				params.put(ProducePheromoneAction.STRENGTH, new Integer(10));
//				SyncResultListener	srl = new SyncResultListener();
				env.performSpaceAction("producePheromone", params);
				
//				srl = new SyncResultListener();
//				env.performSpaceAction("producePheromone", params, srl);
//				srl.waitForResult();
			}
			
			// go to next position
			params = new HashMap();
			params.put(GoAction.DIRECTION, dir);
//			params.put(ISpaceAction.OBJECT_ID, env.getAvatars(getAgentIdentifier())[0].getId());
			params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getComponentIdentifier()).getId());
			SyncResultListener srl = new SyncResultListener();
			env.performSpaceAction("go", params, srl);
			srl.waitForResult();

			
															
			
			ISpaceObject[] objects = null;
			objects = (ISpaceObject[]) getBeliefbase().getBeliefSet("pheromones").getFacts();
//			System.out.println("#GoPlanEnv# Number of pheromones." + objects.length);

//			for (int i = 0; i < objects.length; i++) {
//				 System.out.println(objects[i].toString());
//			}

			objects = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
			// System.out.println("#GoPlanEnv# Number of nests."
			// + objects.length);

//			for (int i = 0; i < objects.length; i++) {
//				 System.out.println(objects[i].toString());
//			}
		}
	}

	/**
	 * Checks whether another plan or goal has changed the target value
	 * (=destination) and adopts the new value, if there has been a change.
	 * 
	 * @return
	 */
	private IVector2 checkDestination(IVector2 target, Space2D env) {
		IVector2 newDestination = (IVector2) getBeliefbase().getBelief("destination").getFact();

		if (!newDestination.equals(target)) {
			target = newDestination.copy();
		}
		return target;
	}
}
