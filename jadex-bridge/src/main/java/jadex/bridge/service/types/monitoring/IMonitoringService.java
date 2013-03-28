package jadex.bridge.service.types.monitoring;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The monitoring service allows for:
 *   
 *  sources: publishing new events
 *  consumers: subscribing for event patterns 
 */
public interface IMonitoringService
{
	/**
	 *  Publish a new event.
	 *  @param event The event. 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event);
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter);
}
