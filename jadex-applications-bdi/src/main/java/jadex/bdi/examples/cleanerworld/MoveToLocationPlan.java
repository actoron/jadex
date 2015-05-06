package jadex.bdi.examples.cleanerworld;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;

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
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2 dest = (IVector2)getParameter("location").getValue();
		
		SyncResultListener	res	= new SyncResultListener();
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object	taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		space.addTaskListener(taskid, myself.getId(), res);
		
		try
		{
			res.waitForResult();
		}
		catch(Exception e)
		{
			fail(e);
		}
	}
}
	
