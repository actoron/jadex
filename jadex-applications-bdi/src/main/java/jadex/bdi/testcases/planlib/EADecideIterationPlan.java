package jadex.bdi.testcases.planlib;

import jadex.bdi.runtime.Plan;

/**
 *  Decide about the next round and the cfp for it.
 */
public class EADecideIterationPlan extends Plan
{	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Object[] cfps = getParameterSet("history").getValues();
		Double newcfp = new Double(((Double)cfps[cfps.length-1]).doubleValue()+5);
		getParameter("cfp").setValue(newcfp);
	}
	
	public void passed()
	{
		getLogger().info("passed: "+this);
	}

	public void failed()
	{
		getLogger().info("failed: "+this);
	}

	public void aborted()
	{
		getLogger().info("aborted: "+this);
	}
}
