package sodekovs.bikesharing.truck.movement;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 * The move to a location plan.
 */
public class MoveToLocationPlan extends Plan {
	/**
	 * The plan body.
	 */
	public void body() {
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		IVector2 dest = (IVector2) getParameter("destination").getValue();


		Map<String,Object> props = new HashMap<String,Object>();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(MoveTask.ACTOR_ID, myself.getId());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		IEnvironmentSpace space = (IEnvironmentSpace) getBeliefbase().getBelief("environment").getFact();
		Object taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}
}