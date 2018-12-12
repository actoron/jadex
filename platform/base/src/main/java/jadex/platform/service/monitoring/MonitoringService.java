package jadex.platform.service.monitoring;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Basic monitoring service implementation that stores the
 *  events in memory. Allows for defining a maximum number of
 *  events kept to avoid memory leaks (default is 10000).
 */
@Service
public class MonitoringService implements IMonitoringService
{
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The list of events. */
	protected List<IMonitoringEvent> events;
	
	/** The maximum number of events to keep, -1 for unrestricted. */
	protected long max;
	
	/** The subscriptions (subscription future -> subscription info). */
	protected Map<SubscriptionIntermediateFuture<IMonitoringEvent>, IFilter<IMonitoringEvent>> subscriptions;
	
	/** The event filter to filter out some events. */
	protected IFilter<IMonitoringEvent> filter;
	
	/**
	 *  Create a new MonitoringService. 
	 */
	public MonitoringService()
	{
		this(10000, null); // default is 10000 entries
	}
	
	/**
	 *  Create a new MonitoringService. 
	 */
	public MonitoringService(long max, IFilter<IMonitoringEvent> filter)
	{
		this.max = max;
		this.filter = filter;
		
		// Must pass filter to be added
		this.filter = new IFilter<IMonitoringEvent>()
		{
			public boolean filter(IMonitoringEvent ev)
			{
//				String sn = ev.getCause().getSourceName();
				String tn = ev.getSourceIdentifier().toString();//ev.getCause().getTargetName();
//				boolean srcjadex = sn!=null && sn.indexOf("jadex.bridge")!=-1;
//				boolean trgjadex = tn.indexOf("jadex.bridge")!=-1;
				
//				boolean ret =  sn==null || tn.indexOf("jadex.bridge")!=-1;
//					(tn.indexOf("ComponentManagementService")==-1
//					&& tn.indexOf("LibraryService")==-1
//					&& tn.indexOf("MonitoringService")==-1);
				return true;
			}
		};
	}
	
	/**
	 *  Notify subscribers that monitoring service is terminated.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		Future<Void> ret = new Future<Void>();
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<IMonitoringEvent> sub: subscriptions.keySet())
			{
				sub.setFinishedIfUndone();
			}
			ret.setResult(null);
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}

	/**
	 *  Publish a new event.
	 *  @param event The event. 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event)
	{
//		if(event.getSource().indexOf("method")!=-1)
//			System.out.println("mon service received: "+event);
		
		if(filter==null || filter.filter(event))
		{
			addEvent(event);
			forwardEvent(event);
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter)
	{
//		final SubscriptionIntermediateFuture<IMonitoringEvent> ret = new SubscriptionIntermediateFuture<IMonitoringEvent>();
		final SubscriptionIntermediateFuture<IMonitoringEvent> ret = (SubscriptionIntermediateFuture<IMonitoringEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class,  agent);
		
		ITerminationCommand tcom = new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeSubscription(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		};
		ret.setTerminationCommand(tcom);
		
		addSubscription(ret, filter);
		
		// Forward all stored events initially
		if(events!=null)
		{
			for(IMonitoringEvent event: events)
			{
				forwardEvent(event, ret);
			}
		}
		
		return ret;
	}
	
	
	/**
	 *  Add a new event to the list.
	 *  Removes an old entry if size=max.
	 */
	protected void addEvent(IMonitoringEvent event)
	{
		if(events==null)
			events = new LinkedList<IMonitoringEvent>();
		events.add(event);
			
//		System.out.println("Added event: "+event);
		
		if(max>0)
		{
			while(events.size()>=max)
			{
				events.remove(0);
			}
		}
	}
	
	/**
	 *  Add a new subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> future, IFilter<IMonitoringEvent> filter)
	{
		if(subscriptions==null)
			subscriptions = new LinkedHashMap<SubscriptionIntermediateFuture<IMonitoringEvent>, IFilter<IMonitoringEvent>>();
		subscriptions.put(future, filter);
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param fut The subscription future to remove.
	 */
	protected void removeSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> fut)
	{
		if(subscriptions==null || !subscriptions.containsKey(fut))
			throw new RuntimeException("Subscriber not known: "+fut);
		subscriptions.remove(fut);
	}
	
	/**
	 *  Forward event to all currently registered subscribers.
	 */
	protected void forwardEvent(IMonitoringEvent event)
	{
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<IMonitoringEvent> sub: subscriptions.keySet().toArray(new SubscriptionIntermediateFuture[0]))
			{
				forwardEvent(event, sub);
			}
		}
	}
	
	/**
	 *  Forward event to one subscribers.
	 */
	protected void forwardEvent(IMonitoringEvent event, SubscriptionIntermediateFuture<IMonitoringEvent> sub)
	{
		IFilter<IMonitoringEvent> fil = subscriptions.get(sub);
		try
		{
			if(fil==null || fil.filter(event))
			{
//				System.out.println("forward to: "+sub);
				if(!sub.addIntermediateResultIfUndone(event))
				{
					subscriptions.remove(sub);
				}
			}
		}
		catch(Exception e)
		{
			// catch filter exceptions
			e.printStackTrace();
		}
	}
}
