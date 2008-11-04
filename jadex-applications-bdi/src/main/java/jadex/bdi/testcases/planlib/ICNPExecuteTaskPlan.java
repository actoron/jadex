package jadex.bdi.testcases.planlib;

import jadex.bdi.runtime.Plan;

/**
 *  Execute a task of a icnp.
 */
public class ICNPExecuteTaskPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("execute task called");
		if(((Boolean)getBeliefbase().getBelief("execute").getFact()).booleanValue())
		{
			getParameter("result").setValue("success");
		}
		else
		{
			fail();
		}
	}
}
