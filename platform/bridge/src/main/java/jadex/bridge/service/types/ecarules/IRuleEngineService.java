package jadex.bridge.service.types.ecarules;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.RuleEvent;

/**
 * 
 */
@Service
public interface IRuleEngineService
{
	/**
	 *  Add an external event to the rule engine.
	 *  It will process the event and fire rules
	 *  accordingly.
	 *  @param event The event.
	 */
	public IIntermediateFuture<RuleEvent> addEvent(IEvent event);
	
	/**
	 *  Subscribe to rule executions.
	 */
	public ISubscriptionIntermediateFuture<RuleEvent> subscribeToEngine();
}
