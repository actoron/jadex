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
	protected IPlan	plan;
	
	//-------- constructors --------
	
	/**
	 *  Create a plan finished task condition.
	 *  @param plan	The plan.
	 */
	public PlanFinishedTaskCondition(final IPlan plan)
	{
		this.plan = plan;
	}
	
	//-------- IBooleanCondition interface --------
	
	/**
	 *  Get the current state of the condition.
	 *  @return	True, if the condition is valid.
	 */
	public boolean isValid()
	{
		return !plan.isFinished();
	}
}
