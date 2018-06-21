package jadex.rules.rulesystem;


import java.util.Collection;

/**
 *  Contains the rules of a rule system.
 */
public interface IRulebase extends Cloneable
{
	/**
	 *  Add a rule.
	 *  @param rule The rule to add.
	 */
	public void addRule(IRule rule);
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule to remove.
	 */
	public void removeRule(IRule rule);
	
	/**
	 *  Get all rules.
	 *  @return All rules.
	 */
	public Collection getRules();
	
	/**
	 *  Get a rule with a given name.
	 *  @param name	The rule name.
	 *  @return The rule.
	 */
	public IRule getRule(String string);
	
	//-------- state observers --------
	
	/**
	 *  Add a new state listener.
	 *  @param listener The state listener.
	 */
	public void addRulebaseListener(IRulebaseListener listener);
	
	/**
	 *  Remove a state listener.
	 *  @param listener The state listener.
	 */
	public void removeRulebaseListener(IRulebaseListener listener);
	
	//-------- cloneable --------
	
	/**
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone();	
}
