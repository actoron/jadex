package jadex.micro.examples.messagequeue.replicated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.messagequeue.Event;

/**
 * This agent represents a part of a distributed, replicable message queue. It offers a 
 * message queue service that is used by the local clients. Also it offers and uses a remote 
 * message queue service where other remote services can subscribe to receive and distribute 
 * messages from local clients.
 */
@Agent
@Service
@ProvidedServices({@ProvidedService(type = IMessageQueueReplicableService.class, implementation = @Implementation(expression = "$pojoagent")),
	@ProvidedService(type = IMessageQueueReplicationService.class, implementation = @Implementation(expression = "$pojoagent")) })
@RequiredServices(@RequiredService(type = IMessageQueueReplicationService.class, multiple = true, scope = RequiredService.SCOPE_GLOBAL, name = "replication"))
@Arguments(@Argument(name = "searchinterval", clazz = Integer.class, defaultvalue = "1000"))
public class ReplicatedMessageQueueAgent implements IMessageQueueReplicableService, IMessageQueueReplicationService 
{

	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	/** The map of local subscribers. */
	protected Map<String, List<SubscriptionIntermediateFuture<Event>>> localsubscribers;

	/** The map of the replication subscribers. */
	protected Map<String, List<SubscriptionIntermediateFuture<Event>>> repsubscribers;

	/** The map of {@link ReplicationSubscription}s */
	protected Map<String, List<ReplicationSubscription>> repsubscriptions;

	/** The service id */
	@ServiceIdentifier
	protected IServiceIdentifier id;

	/** The search interval argument. */
	@AgentArgument
	protected Integer searchinterval;

	/**
	 * Called on agent creation.
	 */
	@AgentCreated
	public void agentCreated() 
	{
		this.localsubscribers = new HashMap<String, List<SubscriptionIntermediateFuture<Event>>>();
		this.repsubscribers = new HashMap<String, List<SubscriptionIntermediateFuture<Event>>>();
		this.repsubscriptions = new HashMap<String, List<ReplicationSubscription>>();
	}

	@AgentBody
	public void agentBody() 
	{
		// Constantly searches for new occurring replication services
		IComponentStep<Void> searchServicesStep = new IComponentStep<Void>() 
		{
			public IFuture<Void> execute(IInternalAccess ia) 
			{
				getOtherServices().addResultListener(new IntermediateDefaultResultListener<IMessageQueueReplicationService>() 
				{
					public void intermediateResultAvailable(IMessageQueueReplicationService result) 
					{
						// check topic-wise if the found service is already subscripted
						for(final String topic : repsubscriptions.keySet()) 
						{
							boolean present = false;

							for(ReplicationSubscription repsub: repsubscriptions.get(topic)) 
							{
								if(repsub.getService().equals(result)) 
								{
									present = true;
									break;
								}
							}

							// if it is not and it is not the own service...
							if(!present && !((IService)result).getId().equals(id)) 
							{
								// subscribe...
								ISubscriptionIntermediateFuture<Event> subscription = result.subscribeForReplication(topic);
								ReplicationSubscription replicationSubscription = new ReplicationSubscription(result, subscription);
								// and store the information for later termination if no local subscriber is interested in the topic anymore
								repsubscriptions.get(topic).add(replicationSubscription);
								subscription.addResultListener(new IntermediateDefaultResultListener<Event>() 
								{
									public void intermediateResultAvailable(Event result) 
									{
										// if remote event occur publish them to the local subscribers
										publish(topic, result);
									}
								});
							}
						}
					}
				});

				// repeat
				agent.getFeature(IExecutionFeature.class).waitForDelay(searchinterval, this);
				return IFuture.DONE;
			}
		};

		this.agent.getFeature(IExecutionFeature.class).waitForTick(searchServicesStep);
	}

	/**
	 * Subscribe to a specific topic. New events that fit to the topic are forwarded to all replication subscribers as intermediate results. A subscribe can unsubscribe by terminating the future.
	 * 
	 * @param topic The topic.
	 * @return The events.
	 */
	public ISubscriptionIntermediateFuture<Event> subscribeForReplication(String topic) 
	{
		final SubscriptionIntermediateFuture<Event>	ret	= (SubscriptionIntermediateFuture<Event>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
//		SubscriptionIntermediateFuture<Event> ret = new SubscriptionIntermediateFuture<Event>();

		List<SubscriptionIntermediateFuture<Event>> subs = repsubscribers.get(topic);
		if (subs == null) 
		{
			subs = new ArrayList<SubscriptionIntermediateFuture<Event>>();
			repsubscribers.put(topic, subs);
		}
		subs.add(ret);

		return ret;
	}

	/**
	 * Replicates the given event and topic to all remote replication subscribers where they publish it to their local subscribers.
	 * 
	 * @param topic The given topic
	 * @param event The given event
	 */
	private void replicate(String topic, Event event) 
	{
		List<SubscriptionIntermediateFuture<Event>> subs = repsubscribers.get(topic);
		if (subs != null) 
		{
			for(Iterator<SubscriptionIntermediateFuture<Event>> it = subs.iterator(); it.hasNext();) 
			{
				SubscriptionIntermediateFuture<Event> sub = it.next();
				if (!sub.addIntermediateResultIfUndone(event)) 
				{
					System.out.println("Removed: " + sub);
					it.remove();
				}
			}
			if(subs.isEmpty())
				repsubscribers.remove(topic);
		}
	}

	/**
	 * Subscribe to a specific topic. New events that fit to the topic are forwarded to all subscribers as intermediate results. A subscribe can unsubscribe by terminating the future. Every time when
	 * someone subscribe for local messages the server also subscribe for the given topic by all the other remote services.
	 * 
	 * @param topic The topic.
	 * @return The events.
	 */
	public ISubscriptionIntermediateFuture<Event> subscribe(final String topic) 
	{
//		SubscriptionIntermediateFuture<Event> ret = new SubscriptionIntermediateFuture<Event>();
		final SubscriptionIntermediateFuture<Event>	ret	= (SubscriptionIntermediateFuture<Event>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);

		List<SubscriptionIntermediateFuture<Event>> subs = localsubscribers.get(topic);
		if(subs == null) 
		{
			subs = new ArrayList<SubscriptionIntermediateFuture<Event>>();
			localsubscribers.put(topic, subs);
		}
		subs.add(ret);

		// subscribe at the remote services
		subscribeRemote(topic);

		return ret;
	}

	/**
	 * The service subscribes at all the other remote service to receive messages form their local subscribers.
	 * 
	 * @param topic
	 *            The given topic
	 */
	private void subscribeRemote(final String topic) 
	{
		getOtherServices().addResultListener(new IntermediateDefaultResultListener<IMessageQueueReplicationService>() 
		{
			public void intermediateResultAvailable(IMessageQueueReplicationService result) 
			{
				boolean present = false;

				// check if there are already subscriptions for the given topic
				if (repsubscriptions.containsKey(topic)) {
					List<ReplicationSubscription> subscriptions = repsubscriptions.get(topic);
					// if so is there already a subscription at the found remote service?
					for (ReplicationSubscription subscription : subscriptions) 
					{
						if (subscription.getService().equals(result)) 
						{
							present = true;
							break;
						}
					}
				} 
				else 
				{
					List<ReplicationSubscription> subscriptions = new ArrayList<ReplicationSubscription>();
					repsubscriptions.put(topic, subscriptions);
				}

				// if no subscription was found for the given service...
				if(!present && !((IService)result).getId().equals(id)) 
				{
					// subscribe...
					ISubscriptionIntermediateFuture<Event> subscription = result.subscribeForReplication(topic);
					ReplicationSubscription replicationSubscription = new ReplicationSubscription(result, subscription);
					// and store the information for later termination if no local subscriber is interested in the topic anymore
					repsubscriptions.get(topic).add(replicationSubscription);
					subscription.addResultListener(new IntermediateDefaultResultListener<Event>() 
					{
						public void intermediateResultAvailable(Event result) 
						{
							// if remote event occur publish them to the local subscribers
							publish(topic, result);
						}
					});
				}
			}
		});
	}

	/**
	 * Publish a new event to the queue.
	 * 
	 * @param topic The topic.
	 * @param event The event to publish.
	 * @param replicate Should the event be replicated among all message queues?
	 */
	public IFuture<Void> publish(String topic, Event event, Boolean replicate) 
	{
		List<SubscriptionIntermediateFuture<Event>> subs = localsubscribers.get(topic);
		if(subs != null) 
		{
			for(Iterator<SubscriptionIntermediateFuture<Event>> it = subs.iterator(); it.hasNext();) 
			{
				SubscriptionIntermediateFuture<Event> sub = it.next();
				if(!sub.addIntermediateResultIfUndone(event)) 
				{
					System.out.println("Removed: " + sub);
					it.remove();
				}
			}
			if(subs.isEmpty())
				removeTopic(topic);
		}

		if(replicate) 
			replicate(topic, event);

		return IFuture.DONE;
	}

	/**
	 * Removes a topic from the map of local subscribers and terminates all remote subscriptions for this topic.
	 * 
	 * @param topic The topic to be removed.
	 */
	private void removeTopic(String topic) 
	{
		localsubscribers.remove(topic);
		List<ReplicationSubscription> replicationSubscriptions = this.repsubscriptions.get(topic);
		for(ReplicationSubscription replicationSubscription : replicationSubscriptions) 
		{
			replicationSubscription.getSubscription().terminate();
		}
	}

	/**
	 * Returns all the other remote {@link IMessageQueueReplicationService}s.
	 * 
	 * @return the other remote {@link IMessageQueueReplicationService}s
	 */
	private IIntermediateFuture<IMessageQueueReplicationService> getOtherServices() 
	{
		return agent.getFeature(IRequiredServicesFeature.class).getServices("replication");
	}

	/**
	 * Publish a new event to the queue.
	 * 
	 * @param topic The topic.
	 * @param event The event to publish.
	 */
	public IFuture<Void> publish(String topic, Event event) 
	{
		return publish(topic, event, false);
	}
}