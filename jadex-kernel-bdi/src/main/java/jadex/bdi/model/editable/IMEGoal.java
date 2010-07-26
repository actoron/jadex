package jadex.bdi.model.editable;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMGoal;
import jadex.bdi.model.IMTypedElement;

/**
 * 
 */
public interface IMEGoal extends IMGoal
{
	/**
	 *  Create the creation condition.
	 *  @return The creation condition.
	 */
	public IMECondition createCreationCondition();
	
	/**
	 *  Create the context condition.
	 *  @return The context condition.
	 */
	public IMECondition createContextCondition();
	
	/**
	 *  Create the drop condition.
	 *  @return The drop condition.
	 */
	public IMECondition createDropCondition();
	
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
	 *  Get the recur condition.
	 *  @return The recur condition.
	 */
	public IMECondition createRecurCondition();

	/**
	 *  Get the exlcude mode.
	 *  @retur The exclude mode.
	 */
	public void setExcludeMode(String excludemode);
	
	/**
	 *  Test if rebuild APL.
	 *  @retur True, if rebuild.
	 */
	public void setRebuild(boolean rebuild);
	
	/**
	 *  Test if goal should be unique.
	 *  @retur True, if unique.
	 */
	public void setUnique(boolean unique);
	
	/**
	 *  Get excluded parameters.
	 *  @retur The excluded parameters.
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
