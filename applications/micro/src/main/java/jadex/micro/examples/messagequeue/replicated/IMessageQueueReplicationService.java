package jadex.micro.examples.messagequeue.replicated;

import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.examples.messagequeue.Event;

/**
 * Message queue interface for the replication of distributed message queues.
 */
public interface IMessageQueueReplicationService 
{
	/**
	 * Subscribe to a specific topic. New events that fit to the topic are forwarded to all replication subscribers as intermediate results. A subscribe can unsubscribe by terminating the future.
	 * 
	 * @param topic The topic.
	 * @return The events.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<Event> subscribeForReplication(String topic);

	/**
	 * Returns the services unique Id.
	 * 
	 * @return the service Id.
	 */
	public String getIdString();
}
