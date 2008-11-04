package jadex.bdi.examples;

import jadex.bdi.runtime.Plan;
import jadex.commons.SUtil;

/**
 * Print the current belief value. 
 */
public class PrintPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		while(true)
		{
			System.out.println("Current time is: "+getBeliefbase().getBelief("time").getFact());
			System.out.println("Current times are: "+SUtil.arrayToString(getBeliefbase().getBeliefSet("times").getFacts()));
			waitFor(1000);
		}
	}
}
