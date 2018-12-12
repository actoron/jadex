/**
 * 
 */
package jadex.micro.examples.messagequeue.replicated;

import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.examples.messagequeue.Event;


/**
 * @author thomas
 */
public class ReplicationSubscription
{
	private IMessageQueueReplicationService			service			= null;
	private ISubscriptionIntermediateFuture<Event>	subscription	= null;

	/**
	 * @param service
	 * @param subscription
	 */
	public ReplicationSubscription(IMessageQueueReplicationService service,
		ISubscriptionIntermediateFuture<Event> subscription)
	{
		this.service = service;
		this.subscription = subscription;
	}

	/**
	 * @return the service
	 */
	public IMessageQueueReplicationService getService()
	{
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(IMessageQueueReplicationService service)
	{
		this.service = service;
	}

	/**
	 * @return the subscription
	 */
	public ISubscriptionIntermediateFuture<Event> getSubscription()
	{
		return subscription;
	}

	/**
	 * @param subscription the subscription to set
	 */
	public void setSubscription(ISubscriptionIntermediateFuture<Event> subscription)
	{
		this.subscription = subscription;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ReplicationSubscription ["
			+ (service != null ? "service=" + service + ", " : "")
			+ (subscription != null ? "subscription=" + subscription : "")
			+ "]";
	}
}