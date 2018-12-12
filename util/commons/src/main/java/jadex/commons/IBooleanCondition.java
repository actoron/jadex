package jadex.commons;

/**
 *  A dynamic boolean condition that can be evaluated on demand
 *  (polling). 
 */
public interface IBooleanCondition
{
	/**
	 *  Get the current state of the condition.
	 *  @return	True, if the condition is valid.
	 */
	public boolean isValid();
}
