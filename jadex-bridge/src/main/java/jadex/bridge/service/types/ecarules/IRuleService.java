package jadex.bridge.service.types.ecarules;

import jadex.commons.future.IFuture;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;

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
	public IFuture<Void> addEvent(IEvent event);
	
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> addRule(IRule rule);
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> removeRule(IRule rule);
	
}
