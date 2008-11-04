package jadex.bdi.testcases.plans;

import jadex.bdi.runtime.ICandidateInfo;
import jadex.bdi.runtime.Plan;

/**
 *  Simple meta-level reasoning plan to select among candidates.
 */
public class MLRPlan extends Plan
{
	//-------- methods --------

	/**
	 * The plan body.
	 */
	public void body()
	{
		ICandidateInfo[] apps = (ICandidateInfo[])getParameterSet("applicables").getValues();
		getLogger().info("Meta-level reasoning selects: " + apps[0]);
		getParameterSet("result").addValue(apps[0]);
	}
}
