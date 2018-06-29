package jadex.bdi.testcases.goals;

import jadex.bdiv3x.runtime.Plan;

/**
 *
 */
public class ResultSetterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		String[] outs = (String[])getParameterSet("outs").getValues();
//		System.out.println("Found: "+SUtil.arrayToString(outs));
		for(int i=0; i<outs.length; i++)
		{
			getParameter(outs[i]).setValue("set");
//			System.out.println("Setted: "+outs[i]);
			waitFor(100);
		}
//		System.out.println("Plan end.");
	}
}
