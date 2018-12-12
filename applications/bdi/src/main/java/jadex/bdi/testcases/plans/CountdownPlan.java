package jadex.bdi.testcases.plans;

import jadex.bdiv3x.runtime.Plan;

/**
 *  The countdown plan counts down to zero.
 */
public class CountdownPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		while(true)
		{
			int num = ((Integer)getBeliefbase().getBelief("num").getFact()).intValue();
			getLogger().info(""+num);
			getBeliefbase().getBelief("num").setFact(Integer.valueOf(num-1));
			waitFor(10);
		}
	}
}
