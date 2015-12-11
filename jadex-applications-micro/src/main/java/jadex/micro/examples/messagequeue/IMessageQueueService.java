package jadex.micro.examples.messagequeue;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Message queue interface. Allows for subscribing to topics
 *  and publishing events to the queue.
 */
public interface IMessageQueueService
{
	/**
	 *  Subscribe to a specific topic. New events that fit to the topic
	 *  are forwarded to all subscribers as intermediate results.
	 *  A subscribe can unsubscribe by terminating the future.
	 *  @param topic The topic.
	 *  @return The events.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<Event> subscribe(String topic);
	
	/**
	 *  Publish a new event to the queue.
	 *  @param topic The topic.
	 *  @param event The event to publish.
	 */
	public IFuture<Void> publish(String topic, Event event);

}
