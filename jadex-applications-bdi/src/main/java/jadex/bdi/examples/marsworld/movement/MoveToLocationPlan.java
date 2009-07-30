package jadex.bdi.examples.marsworld.movement;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  The move to a location plan.
 */
public class MoveToLocationPlan extends Plan
{
	//-------- attributes --------
	
	/** The task id. */
	protected Object taskid;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public MoveToLocationPlan()
	{
		//getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2	dest	= (IVector2)getParameter("destination").getValue();
		
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
//		move	= new MoveTask(dest, res, getExternalAccess());
//		myself.addTask(move);
		SyncResultListener	res	= new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

	/**
	 *  Remove the task, when the plan is aborted. 
	 */
	public void aborted()
	{
		if(taskid!=null)
		{
			ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
			IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
			space.removeObjectTask(taskid, myself.getId());
		}
	}
}