package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Execute a task of a cnp.
 */
public class CNPExecuteTaskPlan extends Plan
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
