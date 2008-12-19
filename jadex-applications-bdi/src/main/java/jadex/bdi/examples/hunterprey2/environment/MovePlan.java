package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.hunterprey2.RequestMove;
import jadex.bdi.examples.hunterprey2.TaskInfo;
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
//		System.out.println("a) move: "+getName());
		RequestMove rm = (RequestMove)getParameter("action").getValue();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addMoveTask(rm.getCreature(), rm.getDirection());

		// Wait until all tasks are processed by the environment.
		waitForCondition("notasks");

//		System.out.println("b) move: "+getName());

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
