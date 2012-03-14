package jadex.bdi.examples.hunterprey_classic.environment;

import jadex.bdi.examples.hunterprey_classic.RequestMove;
import jadex.bdi.examples.hunterprey_classic.TaskInfo;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.Done;

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
//		System.out.println("a) move: "+getAgentName());
		RequestMove rm = (RequestMove)getParameter("action").getValue();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addMoveTask(rm.getCreature(), rm.getDirection());

		// Wait until all tasks are processed by the environment.
		waitForCondition("notasks");

//		System.out.println("b) move: "+getAgentName());

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
