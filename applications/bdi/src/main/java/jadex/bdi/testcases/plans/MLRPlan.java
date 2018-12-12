package jadex.bdi.testcases.plans;

import jadex.bdiv3x.runtime.ICandidateInfo;
import jadex.bdiv3x.runtime.Plan;

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
