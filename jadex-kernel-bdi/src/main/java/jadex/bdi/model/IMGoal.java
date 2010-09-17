package jadex.bdi.model;

/**
 *  Interface for goal models.
 */
public interface IMGoal extends IMProcessableElement
{
	/**
	 *  Get the creation condition.
	 *  @return The creation condition.
	 */
	public IMCondition getCreationCondition();
	
	/**
	 *  Get the context condition.
	 *  @return The context condition.
	 */
	public IMCondition getContextCondition();
	
	/**
	 *  Get the drop condition.
	 *  @return The drop condition.
	 */
	public IMCondition getDropCondition();
	
	/**
	 *  Test if is retry.
	 *  @return True, if is retry.
	 */
	public boolean isRetry();
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRetryDelay();
	
	/**
	 *  Test if is recur.
	 *  @return True, if is recur.
	 */
	public boolean isRecur();
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRecurDelay();
	
	/**
	 *  Get the recur condition.
	 *  @return The recur condition.
	 */
	public IMCondition getRecurCondition();

	/**
	 *  Get the exlcude mode.
	 *  @return The exclude mode.
	 */
	public String getExcludeMode();
	
	/**
	 *  Test if rebuild APL.
	 *  @return True, if rebuild.
	 */
	public boolean isRebuild();
	
	/**
	 *  Test if goal should be unique.
	 *  @return True, if unique.
	 */
	public boolean isUnique();
	
	/**
	 *  Get excluded parameters.
	 *  @return The excluded parameters.
	 */
	public String[] getExcludedParameters();
	
	/**
	 *  Get inhibited goals.
	 *  @return The inhibited goals.
	 */
	public IMInhibited[] getInhibitedGoals();
	
	/**
	 *  Get the cardinality.
	 *  @return The cardinality.
	 */
	public int getCardinality();
}
