package jadex.bdi.examples.disastermanagement.movement;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.disastermanagement.MoveTask;
import jadex.bdiv3.runtime.PlanFinishedTaskCondition;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;

/**
 *  The move to a location plan.
 */
public class MoveToLocationPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2	dest = (IVector2)getParameter("destination").getValue();
		IVector2	home	= (IVector2)getBeliefbase().getBelief("home").getFact();
		
		if(!((String)myself.getProperty("state")).equals("moving_to_hospital") && dest.equals(home))
			myself.setProperty("state", "moving_home");
		
		// Create a move task
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		Object taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		
		// Wait for the task to finish.
		Future<Void> fut = new Future<Void>();
		space.addTaskListener(taskid, myself.getId(), new DelegationResultListener<Void>(fut));
		fut.get();
	}

	
	/**
	 *  Called when a plan fails.
	 */
	public void failed()
	{
		System.err.println("Plan failed: "+this);
		getException().printStackTrace();
	}
}