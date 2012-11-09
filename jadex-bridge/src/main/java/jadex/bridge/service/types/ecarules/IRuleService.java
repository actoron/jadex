package jadex.bridge.service.types.ecarules;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.RuleEvent;

/**
 * 
 */
public interface IRuleService
{
	/**
	 *  Add an external event to the rule engine.
	 *  It will process the event and fire rules
	 *  accordingly.
	 *  @param event The event.
	 */
	public IIntermediateFuture<RuleEvent> addEvent(IEvent event);
	
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
	 *  Subscribe to rule executions.
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<RuleEvent> subscribe();
}
