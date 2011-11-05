package jadex.rules.eca;

import java.util.List;

/**
 * 
 */
public interface IRule
{
	/**
	 *  Get the rule name.
	 *  @return The rule name.
	 */
	public String getName();

	/**
	 * 
	 */
	public List<String> getEvents();
	
	/**
	 * 
	 */
	public ICondition getCondition();
	
	/**
	 * 
	 */
	public IAction getAction();

}
