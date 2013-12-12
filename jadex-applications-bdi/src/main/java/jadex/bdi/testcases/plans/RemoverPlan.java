package jadex.bdi.testcases.plans;

import jadex.bdi.runtime.Plan;

/**
 *  The remover plan waits for a short time
 *  and then modifies the beliefbase..
 */
public class RemoverPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public RemoverPlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		waitFor(3000);
		getBeliefbase().getBelief("mycontext").setFact(Boolean.FALSE);

		if(getPlanbase().getPlans().length==1)
		{
			getLogger().info("Success. Plan has been removed.");
		}
		else
		{
			getLogger().info("Failure! Plan with invalid context still alive.");
		}

		killAgent();
	}
}
