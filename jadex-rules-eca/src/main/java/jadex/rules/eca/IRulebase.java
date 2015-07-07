package jadex.rules.eca;

import java.util.Collection;
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
	 *  Update a rule.
	 *  @param rule The rule.
	 */
	public void updateRule(IRule<?> rule);
	
	/**
	 *  Get all rules that are relevant for an event type.
	 *  @param event The event type.
	 *  @return The rules.
	 */
	public List<IRule<?>> getRules(EventType event);
	
	/**
	 *  Get all rules.
	 *  @return The rules.
	 */
	public Collection<IRule<?>> getRules();
	
	/**
	 *  Get the rule.
	 *  @param event The rule name.
	 *  @return The rule.
	 */
	public IRule<?> getRule(String name);
	
	/**
	 *  Test if a rule is contained in the rule base.
	 *  @param name The rule name.
	 *  @return True, if contained.
	 */
	public boolean containsRule(String name);
}
