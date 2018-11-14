package jadex.wfms.service.impl;

import jadex.bridge.ComponentAdapter;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.service.ILogService;
import jadex.wfms.service.listeners.ILogListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class LogService implements ILogService
{
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	/** The event log */
	protected List<IComponentChangeEvent> eventLog;
	
	/** Log listeners */
	protected Map<IComponentIdentifier, Set<ILogListener>> listeners;
	
	/** CMS listener */
	protected ICMSComponentListener cmslistener;
	
	public LogService()
	{
		this.eventLog = Collections.synchronizedList(new ArrayList<IComponentChangeEvent>());
		this.listeners = Collections.synchronizedMap(new HashMap<IComponentIdentifier, Set<ILogListener>>());
	}
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture startService()
	{
		final Future ret = new Future();
		ia.getServiceContainer().getService("clock_service").addResultListener(new DefaultResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				final IClockService clockservice = (IClockService) result;
				ia.getServiceContainer().getService("cms").addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService) result;
						cmslistener = new ICMSComponentListener()
						{
							public IFuture componentRemoved(IComponentDescription desc, Map results)
							{
								return IFuture.DONE;
							}
							
							public IFuture componentChanged(IComponentDescription desc)
							{
								return IFuture.DONE;
							}
							
							public IFuture componentAdded(final IComponentDescription desc)
							{
								final Future cret = new Future();
								cms.getExternalAccess(desc.getName()).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										//TODO: Hack!
										if (!desc.getName().getParent().getName().toLowerCase().contains("execution"))
										{
											cret.setResult(null);
											return;
										}
										
										final IExternalAccess ncea = (IExternalAccess) result;
										IComponentChangeEvent ce = new ComponentChangeEvent()
										{
											{
												setTime(clockservice.getTime());
												setComponentCreationTime(desc.getCreationTime());
												setEventType(IComponentChangeEvent.EVENT_TYPE_CREATION);
												setSourceCategory(IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT);
												setSourceType(desc.getModelName());
												setSourceName(desc.getName().getName());
												setComponent(desc.getName());
											}
										};
										logEvent(ce);
										
										ncea.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												ia.addComponentListener(new ComponentAdapter()
												{
													public IFuture eventOccured(
															IComponentChangeEvent cce)
													{
														logEvent(cce);
														return IFuture.DONE;
													}
												});
												return IFuture.DONE;
											}
										}).addResultListener(ia.createResultListener(new DelegationResultListener(cret)));
									}
								}));
								return cret;
							}
						};
						cms.addComponentListener(null, cmslistener);
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Stops the service.
	 */
	@ServiceShutdown
	public IFuture shutdownService()
	{
		final Future ret = new Future();
		ia.getServiceContainer().getService("cms").addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService) result;
				cms.removeComponentListener(null, cmslistener);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Adds a log listener.
	 *  @param client The client adding the listener or null for none.
	 *  @param listener The listener.
	 *  @param pastEvents True, if past events should be passed to the listener.
	 *  @return Indication of success.
	 */
	public IFuture<Void> addLogListener(IComponentIdentifier client, ILogListener listener, boolean pastEvents)
	{
		synchronized (listeners)
		{
			Set<ILogListener> lSet = listeners.get(client);
			if (lSet == null)
			{
				lSet = new HashSet<ILogListener>();
				listeners.put(client, lSet);
			}
			lSet.add(listener);
			
			if (pastEvents)
			{
				synchronized (eventLog)
				{
					for (Iterator<IComponentChangeEvent> it = eventLog.iterator(); it.hasNext();)
						listener.logMessage(it.next());
				}
			}
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Removes a log listener.
	 *  
	 *  @param client The client adding the listener or null for none.
	 *  @param listener The listener.
	 *  @return Indication of success.
	 */
	public IFuture<Void> removeLogListener(IComponentIdentifier client, ILogListener listener)
	{
		Future<Void> ret = new Future<Void>();
		synchronized (listeners)
		{
			Set<ILogListener> lSet = listeners.get(client);
			if (lSet != null)
			{
				if (lSet.remove(listener))
					ret.setResult(null);
			}
		}
		if (!ret.isDone())
			ret.setException(new NoSuchElementException(listener.toString()));
		return ret;
	}
	
	/**
	 *  Writes an event into the WfMS log.
	 *  @param event The event.
	 *  @return Null, when done.
	 */
	public IFuture<Void> logEvent(IComponentChangeEvent event)
	{
		synchronized (listeners)
		{
			eventLog.add(event);
			for (Iterator<Set<ILogListener>> it = listeners.values().iterator(); it.hasNext(); )
				for (Iterator<ILogListener> it2 = it.next().iterator(); it2.hasNext(); )
					it2.next().logMessage(event);
		}
		return Future.DONE;
	}
	
	protected static final void dispatchLogServiceEvent(IServiceProvider provider, final IComponentChangeEvent cce)
	{
		provider.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ILogService.class, RequiredServiceInfo.SCOPE_GLOBAL)).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				((ILogService)result).logEvent(cce);
			}
		});
	}
}
