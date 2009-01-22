package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.hunterprey2.RequestMove;
import jadex.bdi.examples.hunterprey2.TaskInfo;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Handle move requests by the environment.
 */
public class  MovePlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public  MovePlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{

		RequestMove rm = (RequestMove)getParameter("action").getValue();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addMoveTask(rm.getCreature(), rm.getDirection());

		System.out.println("waiting for condition notask " + this + "(size="+env.getTaskSize()+")");
		
		// Wait until all tasks are processed by the environment.
		waitForCondition("notasks");
		
		System.out.println("condition notask fired " + this + "(size="+env.getTaskSize()+")");

		// perform move goal in sim engine
		if(ti.getResult()!=null && ti.getResult() instanceof IGoal)
		{
			IGoal moveGoal = (IGoal) ti.getResult();
			dispatchSubgoalAndWait(moveGoal);
			assert moveGoal.isSucceeded() : "Sim-Engine Problem with move goal"+moveGoal; 
			ti.setResult(new Boolean(moveGoal.isSucceeded()));
			// remove task from list
			env.removeGoalTask(ti);
		}
		
		// Wait until all sim goals where processed.
		waitForCondition("nogoaltasks");

		// Result is null, when creature died and action was not executed.
		if(ti.getResult()!=null && ((Boolean)ti.getResult()).booleanValue())
		{
			Done done = new Done();
			done.setAction(rm);
			getParameter("result").setValue(done);
		}
		else
		{
			fail();
		}
	}
}
