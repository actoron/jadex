package jadex.bdi.examples.disastermanagement.movement;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.math.IVector2;
import jadex.bdi.examples.disastermanagement.MoveTask;
import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

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
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		Object taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		
		// Wait for the task to finish.
		SyncResultListener	res	= new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
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