package jadex.bdi.examples.cleanerworld_env.cleaner;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.Plan;

/**
 *  The move to a location plan.
 */
public class MoveToLocationPlan extends Plan
{
	//-------- attributes --------
	
	/** The move task. */
	protected MoveTask	move;
	
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
		ISpaceObject myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2 dest = (IVector2)getParameter("location").getValue();
		
		SyncResultListener	res	= new SyncResultListener();
		move = new MoveTask(dest, res, getExternalAccess());
		myself.addTask(move);
		res.waitForResult();
	}

	/**
	 *  Remove the task, when the plan is aborted. 
	 */
	public void aborted()
	{
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		myself.removeTask(move);
	}
}
	
