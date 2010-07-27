package jadex.bdi.model.editable;

import jadex.bdi.model.IMGoal;

/**
 * 
 */
public interface IMEGoal extends IMGoal, IMEProcessableElement
{
	/**
	 *  Create the creation condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The creation condition.
	 */
	public IMECondition createCreationCondition(String expression, String language);
	
	/**
	 *  Create the context condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The context condition.
	 */
	public IMECondition createContextCondition(String expression, String language);
	
	/**
	 *  Create the drop condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The drop condition.
	 */
	public IMECondition createDropCondition(String expression, String language);
	
	/**
	 *  Set the retry flag.
	 *  @param retry The retry flag.
	 */
	public void setRetry(boolean retry);
	
	/**
	 *  Set the retry delay.
	 *  @param retry The retry delay.
	 */
	public void setRetryDelay(long retrydelay);
	
	/**
	 *  Set the recur flag.
	 *  @param recur The recur flag.
	 */
	public void setRecur(boolean recur);
	
	/**
	 *  Set the recur delay.
	 *  @param recur The retry delay.
	 */
	public void setRecurDelay(long recurdelay);
	
	/**
	 *  Create the recur condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The recur condition.
	 */
	public IMECondition createRecurCondition(String expression, String language);

	/**
	 *  Set the exlcude mode.
	 *  @param excludemode The exclude mode.
	 */
	public void setExcludeMode(String excludemode);
	
	/**
	 *  Set the rebuild APL flag.
	 *  @param rebuild Rebuild flag.
	 */
	public void setRebuild(boolean rebuild);
	
	/**
	 *  Set the unique flag.
	 *  @param unique The unique flag.
	 */
	public void setUnique(boolean unique);
	
	/**
	 *  Add a excluded parameter.
	 *  @param name The name of the excluded parameter.
	 */
	public void addExcludedParameter(String name);
	
	/**
	 *  Get inhibited goals.
	 *  @retur The inhibited goals.
	 */
//	public IMInhibitedElement getInhibitedGoals();
	
	/**
	 *  Get the cardinality.
	 *  @retur The cardinality.
	 */
	public void setCardinality(int card);
}
