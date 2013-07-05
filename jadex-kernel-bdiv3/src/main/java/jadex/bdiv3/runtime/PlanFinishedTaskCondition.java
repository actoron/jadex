package jadex.bdiv3.runtime;

import jadex.commons.IBooleanCondition;

/**
 *  Finished condition for a space object task to be removed
 *  when a plan is finished.
 */
public class PlanFinishedTaskCondition implements IBooleanCondition
{
	//-------- attributes --------
	
	/** The finished flag. */
	protected boolean	finished;
	
	//-------- constructors --------
	
	/**
	 *  Create a plan finished task condition.
	 *  @param plan	The plan.
	 */
	public PlanFinishedTaskCondition(IPlan plan)
	{
		plan.addPlanListener(new IPlanListener()
		{	
			public void planFinished()
			{
				finished	= true;
			}
		});
	}
	
	//-------- IBooleanCondition interface --------
	
	/**
	 *  Get the current state of the condition.
	 *  @return	True, if the condition is valid.
	 */
	public boolean isValid()
	{
		return !finished;
	}
}
