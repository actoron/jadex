package jadex.bdi.examples.moneypainter;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 * 
 */
public class PrintRichPlan extends Plan
{
	/**
	 * 
	 */
	public void body()
	{
		int mon = ((Integer)getBeliefbase().getBelief("money").getFact()).intValue();
		int target = ((Integer)getBeliefbase().getBelief("target").getFact()).intValue();
		
		if(((IGoal)getReason()).isSucceeded())
		{
			System.out.println("Now I am rich as I have made "+mon+" euros.");
		}
		else
		{
			System.out.println("I have made only "+mon+" euros, planned were "+target);
		}
	}
}
