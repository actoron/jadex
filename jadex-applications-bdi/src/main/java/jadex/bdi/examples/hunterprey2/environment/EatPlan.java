package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.examples.hunterprey2.RequestEat;
import jadex.bdi.examples.hunterprey2.TaskInfo;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IWaitqueue;
import jadex.bdi.runtime.Plan;

/**
 *  Handle eat requests by the environment.
 */
public class  EatPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public  EatPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		

		RequestEat re = (RequestEat)getParameter("action").getValue();
		
		//System.out.println("a) eat: "+re.getCreature().getName());

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addEatTask(re.getCreature(), re.getObject());

//		// Wait until all tasks are processed by the environment.
//		waitForCondition("notasks");
		
		// Wait for tick from SimTickerPlan
		waitForInternalEvent("tick_event");
		
		//System.out.println("b) eat: "+re.getCreature().getName());

		// Result is null when creature died and action was not executed.
		if(ti.getResult()!=null && ((Boolean)ti.getResult()).booleanValue())
		{
			Done done = new Done();
			done.setAction(re);
			getParameter("result").setValue(done);
		}
		else
		{
			fail();
		}
	}

}
