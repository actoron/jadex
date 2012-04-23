package jadex.micro.examples.messagequeue;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IMessageQueueService.class, implementation=@Implementation(expression="$pojoagent")))
public class MessageQueueAgent implements IMessageQueueService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The map of subscribers. */
	protected Map<String, List<SubscriptionIntermediateFuture<Event>>> subscribers;
	
	/**
	 * 
	 */
	@AgentCreated
	public void agentCreated()
	{
		this.subscribers = new HashMap<String, List<SubscriptionIntermediateFuture<Event>>>();
	}
	
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<Event> subscribe(String topic)
	{
		SubscriptionIntermediateFuture<Event> ret = new SubscriptionIntermediateFuture<Event>();
		
		List<SubscriptionIntermediateFuture<Event>> subs = subscribers.get(topic);
		if(subs==null)
		{
			subs = new ArrayList<SubscriptionIntermediateFuture<Event>>();
			subscribers.put(topic, subs);
		}
		subs.add(ret);
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> publish(String topic, Event event)
	{
		List<SubscriptionIntermediateFuture<Event>> subs = subscribers.get(topic);
		if(subs!=null)
		{
			for(Iterator<SubscriptionIntermediateFuture<Event>> it = subs.iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Event> sub = it.next();
				if(sub.addIntermediateResultIfUndone(event))
					it.remove();
			}
			if(subs.isEmpty())
				subscribers.remove(topic);
		}
		
		return IFuture.DONE;
	}
}
