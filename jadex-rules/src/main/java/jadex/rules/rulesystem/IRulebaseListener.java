package jadex.rules.rulesystem;


/**
 *  Rulebase listener callback interface.
 */
public interface IRulebaseListener
{
	/**
	 *  Notification when a rule has been added.
	 *  @param rule The added rule.
	 */
	public void ruleAdded(IRule rule);
	
	/**
	 *  Notification when a rule has been removed.
	 *  @param rule The removed rule.
	 */
	public void ruleRemoved(IRule rule);
}
