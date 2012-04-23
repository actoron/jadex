package jadex.micro.examples.messagequeue;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * 
 */
public interface IMessageQueueService
{
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<Event> subscribe(String topic);
	
	/**
	 * 
	 */
	public IFuture<Void> publish(String topic, Event event);

}
