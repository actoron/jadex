package jadex.bridge.component.impl;

import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.ServiceGetter;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class MonitoringComponentFeature extends AbstractComponentFeature implements IMonitoringComponentFeature
{
	/** The subscriptions (subscription future -> subscription info). */
	protected Map<SubscriptionIntermediateFuture<IMonitoringEvent>, Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel>>	subscriptions;
	
	/** The monitoring service getter. */
	protected ServiceGetter<IMonitoringService>	getter;

	/** The event emit level for subscriptions. */
	protected PublishEventLevel	emitlevelsub;
	
	/**
	 *  Create the feature.
	 */
	public MonitoringComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		this.emitlevelsub = cinfo.getComponentDescription().getMonitoring();
		if(emitlevelsub==null)
			emitlevelsub = PublishEventLevel.OFF;
//		System.out.println("mon is: "+cinfo.getComponentDescription().getName()+" "+emitlevelsub);
	}
	
	/**
	 *  Publish a monitoring event. This event is automatically send
	 *  to the monitoring service of the platform (if any). 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event, PublishTarget pt)
	{
		if(event.getCause()==null)
		{
			ServiceCall call = CallAccess.getCurrentInvocation();
			if(call!=null)
			{
//				System.out.println("injecting call cause: "+call.getCause());
				event.setCause(call.getCause());
			}
			else if(getComponent().getComponentDescription().getCause()!=null)
			{
//				System.out.println("injecting root cause: "+call.getCause());
				event.setCause(getComponent().getComponentDescription().getCause().createNext());//event.getSourceIdentifier().toString()));
			}
		}
		
		// Publish to local subscribers
		publishLocalEvent(event);
		
//		// Publish to monitoring service if monitoring is turned on
//		if((PublishTarget.TOALL.equals(pt) || PublishTarget.TOMONITORING.equals(pt) 
//			&& event.getLevel().getLevel()<=getPublishEmitLevelMonitoring().getLevel()))
//		{
			return publishEvent(event, getMonitoringServiceGetter());
//		}
//		else
//		{
//			return IFuture.DONE;
//		}
	}
	
	/**
	 *  Publish a monitoring event to the monitoring service.
	 */
	public static IFuture<Void> publishEvent(final IMonitoringEvent event, final ServiceGetter<IMonitoringService> getter)
	{
//		return IFuture.DONE;
		
		final Future<Void> ret = new Future<Void>();
		
		if(getter!=null)
		{
			getter.getService().addResultListener(new ExceptionDelegationResultListener<IMonitoringService, Void>(ret)
			{
				public void customResultAvailable(IMonitoringService monser)
				{
					if(monser!=null)
					{
//						System.out.println("Published: "+event);
						monser.publishEvent(event).addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void exceptionOccurred(Exception exception)
							{
								getter.resetService();
								ret.setException(exception);
							}
						});
					}
					else
					{
//						System.out.println("Could not publish: "+event);
						ret.setResult(null);
					}
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 * Get the monitoring event emit level for subscriptions. Is the maximum
	 * level of all subscriptions (cached for speed).
	 */
	public PublishEventLevel getPublishEmitLevelSubscriptions()
	{
		return emitlevelsub;
	}

	/**
	 * Get the monitoring service getter.
	 * 
	 * @return The monitoring service getter.
	 */
	public ServiceGetter<IMonitoringService> getMonitoringServiceGetter()
	{
		if(getter == null)
			getter = new ServiceGetter<IMonitoringService>(getComponent(), IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		return getter;
	}

	/**
	 * Forward event to all currently registered subscribers.
	 */
	public void publishLocalEvent(IMonitoringEvent event)
	{
		if(subscriptions != null)
		{
			for(SubscriptionIntermediateFuture<IMonitoringEvent> sub : subscriptions.keySet().toArray(new SubscriptionIntermediateFuture[0]))
			{
				publishLocalEvent(event, sub);
			}
		}
	}

	/**
	 * Forward event to one subscribers.
	 */
	protected void publishLocalEvent(IMonitoringEvent event, SubscriptionIntermediateFuture<IMonitoringEvent> sub)
	{
		Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel> tup = subscriptions.get(sub);
		try
		{
			PublishEventLevel el = tup.getSecondEntity();
			// System.out.println("rec ev: "+event);
			if(event.getLevel().getLevel() <= el.getLevel())
			{
				IFilter<IMonitoringEvent> fil = tup.getFirstEntity();
				if(fil == null || fil.filter(event))
				{
					// System.out.println("forward to: "+event+" "+sub);
					if(!sub.addIntermediateResultIfUndone(event))
					{
						subscriptions.remove(sub);
					}
				}
			}
		}
		catch(Exception e)
		{
			// catch filter exceptions
			e.printStackTrace();
		}
	}

	
	/**
	 *  Check if event targets exist.
	 */
	public boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi)
	{
		boolean ret = false;

		if(pi.getLevel() <= getPublishEmitLevelSubscriptions().getLevel() && (PublishTarget.TOALL.equals(pt) || PublishTarget.TOSUBSCRIBERS.equals(pt)))
		{
			ret = subscriptions != null && !subscriptions.isEmpty();
		}
		if(!ret && pi.getLevel() <= getPublishEmitLevelMonitoring().getLevel() && (PublishTarget.TOALL.equals(pt) || PublishTarget.TOMONITORING.equals(pt)))
		{
			ret = true;
		}

		return ret;
	}
	
	/**
	 * Get the monitoring event emit level.
	 */
	public PublishEventLevel getPublishEmitLevelMonitoring()
	{
		return getComponent().getComponentDescription().getMonitoring() != null ? getComponent().getComponentDescription().getMonitoring() : PublishEventLevel.OFF;
		// return emitlevelmon;
	}
	
	/**
	 * Subscribe to monitoring events.
	 * 
	 * @param filter An optional filter.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel emitlevel)
	{
		final SubscriptionIntermediateFuture<IMonitoringEvent> ret = (SubscriptionIntermediateFuture<IMonitoringEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class,
			getComponent());

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

		// Signal that subscription has been done
		MonitoringEvent subscribed = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.TYPE_SUBSCRIPTION_START, System.currentTimeMillis(),
			PublishEventLevel.COARSE);
		boolean post = false;
		try
		{
			post = filter == null || filter.filter(subscribed);
		}
		catch(Exception e)
		{
		}
		if(post)
		{
			ret.addIntermediateResult(subscribed);
		}

		addSubscription(ret, filter, emitlevel);

		if(initial)
		{
			List<IMonitoringEvent> evs = getCurrentStateEvents();
			if(evs != null && evs.size() > 0)
			{
				BulkMonitoringEvent bme = new BulkMonitoringEvent(evs.toArray(new IMonitoringEvent[evs.size()]));
				ret.addIntermediateResult(bme);
			}
		}

		return ret;
	}

	/**
	 * Add a new subscription.
	 * 
	 * @param future The subscription future.
	 * @param si The subscription info.
	 */
	protected void addSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> future, IFilter<IMonitoringEvent> filter, PublishEventLevel emitlevel)
	{
		if(subscriptions == null)
			subscriptions = new LinkedHashMap<SubscriptionIntermediateFuture<IMonitoringEvent>, Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel>>();
		if(emitlevel.getLevel() > emitlevelsub.getLevel())
			emitlevelsub = emitlevel;
		subscriptions.put(future, new Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel>(filter, emitlevel));
	}

	/**
	 * Remove an existing subscription.
	 * 
	 * @param fut The subscription future to remove.
	 */
	protected void removeSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> fut)
	{
		if(subscriptions == null || !subscriptions.containsKey(fut))
			throw new RuntimeException("Subscriber not known: " + fut);
		subscriptions.remove(fut);
		emitlevelsub = PublishEventLevel.OFF;
		for(Tuple2<IFilter<IMonitoringEvent>, PublishEventLevel> tup : subscriptions.values())
		{
			if(tup.getSecondEntity().getLevel() > emitlevelsub.getLevel())
				emitlevelsub = tup.getSecondEntity();
			if(PublishEventLevel.COARSE.equals(emitlevelsub))
				break;
		}
	}
	
	/**
	 *  Get the current state as events.
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		List<IMonitoringEvent> ret = null;
		IExecutionFeature exef = getComponent().getComponentFeature0(IExecutionFeature.class);
		if(exef instanceof ExecutionComponentFeature)
			ret = ((ExecutionComponentFeature)exef).getCurrentStateEvents();
		return ret;
	}
}
