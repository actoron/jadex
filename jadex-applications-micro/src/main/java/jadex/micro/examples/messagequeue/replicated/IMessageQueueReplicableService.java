package jadex.micro.examples.messagequeue.replicated;

import jadex.commons.future.IFuture;
import jadex.micro.examples.messagequeue.Event;
import jadex.micro.examples.messagequeue.IMessageQueueService;

/**
 * A replicable extension of the {@link IMessageQueueService}.
 */
public interface IMessageQueueReplicableService extends IMessageQueueService 
{
	/**
	 * Publish a new event to the queue.
	 * 
	 * @param topic The topic.
	 * @param event The event to publish.
	 * @param replicate Should the event be replicated among all message queues?
	 */
	public IFuture<Void> publish(String topic, Event event, Boolean replicate);
}
