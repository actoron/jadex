package jadex.bridge.service.types.ecarules;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.rules.eca.IRule;

/**
 * 
 */
@Service
public interface IRulebaseService
{
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> addRule(IRule<?> rule);
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> removeRule(String rulename);
	
	/**
	 *  Subscribe to rule base changes.
	 */
	public ISubscriptionIntermediateFuture<IRulebaseEvent> subscribeToRulebase();
}
