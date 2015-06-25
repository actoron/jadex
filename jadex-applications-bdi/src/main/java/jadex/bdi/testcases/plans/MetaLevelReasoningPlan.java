package jadex.bdi.testcases.plans;

import jadex.bdiv3x.runtime.ICandidateInfo;
import jadex.bdiv3x.runtime.Plan;

/**
 *  The meta-level reasoning plan for deciding between
 */
public class MetaLevelReasoningPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		ICandidateInfo[] apps = (ICandidateInfo[])getParameterSet("applicables").getValues();
		getLogger().info("Meta-level reasoning chooses between: ");
		for(int i=0; i<apps.length; i++)
			getLogger().info("    "+apps[i]);

		if(apps.length==0)
			throw new RuntimeException("No applicable candidates. "+this);

		ICandidateInfo sel = null;
		double selval = -1;
		for(int i=0; i<apps.length; i++)
		{
			double tmpval = ((Double)apps[i].getPlan().getParameter("importance").getValue()).doubleValue();
			if(tmpval>selval)
			{
				sel = apps[i];
				selval = tmpval;
			}
		}

		getParameterSet("result").addValue(sel);
	}
}