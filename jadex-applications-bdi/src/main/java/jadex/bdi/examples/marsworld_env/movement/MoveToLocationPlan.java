package jadex.bdi.examples.marsworld_env.movement;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.marsworld_env.carry.LoadOreTask;
import jadex.bdi.runtime.Plan;

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
		
		SyncResultListener	res	= new SyncResultListener();
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(LoadOreTask.PROPERTY_LISTENER, res);
		IEnvironmentSpace space = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
//		move	= new MoveTask(dest, res, getExternalAccess());
//		myself.addTask(move);
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