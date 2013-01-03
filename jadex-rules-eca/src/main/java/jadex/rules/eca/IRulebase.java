package jadex.rules.eca;

import java.util.List;

/**
 *  Interface for the rulebase that contains all
 *  the rules of the system.
 */
public interface IRulebase
{
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public void addRule(IRule<?> rule);
	
	/**
	 *  Remove a rule.
	 *  @param rulename The rule name.
	 */
	public void removeRule(String rulename);
	
	/**
	 *  Get all rules that are relevant for an event type.
	 *  @param event The event type.
	 *  @return The rules.
	 */
	public List<IRule<?>> getRules(String event);
	
	/**
	 *  Get the rule.
	 *  @param event The rule name.
	 *  @return The rule.
	 */
	public IRule<?> getRule(String name);
}
