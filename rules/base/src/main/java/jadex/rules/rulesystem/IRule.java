package jadex.rules.rulesystem;

import jadex.rules.rulesystem.rules.IPriorityEvaluator;

/**
 *  Interface for rules.
 */
public interface IRule
{
	/**
	 *  Get the rule name.
	 *  @return The rule name.
	 */
	public String getName();
	
	/**
	 *  Get the condition.
	 *  @return The condition.
	 */
	public ICondition getCondition();

	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public IAction getAction();
	
	/**
	 *  Get the priority. 
	 *  @return The priority.
	 */
	public IPriorityEvaluator getPriorityEvaluator();
	
}