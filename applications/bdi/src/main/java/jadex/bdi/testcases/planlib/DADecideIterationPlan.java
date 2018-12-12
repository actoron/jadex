package jadex.bdi.testcases.planlib;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Decide about the next round and the cfp for it.
 */
public class DADecideIterationPlan extends Plan
{	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Object[] cfps = getParameterSet("history").getValues();
		Double newcfp = Double.valueOf(((Double)cfps[cfps.length-1]).doubleValue()-5);
		if(newcfp.doubleValue()<100)
			fail();
		getParameter("cfp").setValue(newcfp);
	}
}
