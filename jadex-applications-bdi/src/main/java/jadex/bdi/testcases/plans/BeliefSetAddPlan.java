package jadex.bdi.testcases.plans;

import jadex.bdi.runtime.Plan;

/**
 *  Add a fact to a belief set.
 */
public class BeliefSetAddPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		int i=0;
		while(true)
		{
			waitFor(100);
			getBeliefbase().getBeliefSet("some_numbers").addFact(new Integer(i++));
			//getBeliefbase().getBeliefSet("some_numbers").removeFact(new Integer(i++));
		}
	}
}
