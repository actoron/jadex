package jadex.wfms.service.impl;

import jadex.bridge.ComponentAdapter;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
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

public class LogService extends BasicService implements ILogService
{
	protected IServiceContainer provider;
	
	protected List<IComponentChangeEvent> eventLog;
	
	protected Map<IComponentIdentifier, Set<ILogListener>> listeners;
	
	public LogService(IServiceContainer provider)
	{
		super(provider.getId(), ILogService.class, new HashMap());
		this.eventLog = Collections.synchronizedList(new ArrayList<IComponentChangeEvent>());
		this.listeners = Collections.synchronizedMap(new HashMap<IComponentIdentifier, Set<ILogListener>>());
		this.provider = provider;
	}
	
	public IFuture startService()
	{
		super.startService();
		final Future ret = new Future();
		SServiceProvider.getService(provider, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				final IClockService clockservice = (IClockService) result;
				SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService) result;
						cms.addComponentListener(null, new ICMSComponentListener()
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
								if(!sid.getProviderId().equals(desc.getParent()))
									return IFuture.DONE;
								IComponentChangeEvent ce = new ComponentChangeEvent()
								{
									{
										setTime(clockservice.getTime());
										setEventType(IComponentChangeEvent.EVENT_TYPE_CREATION);
										setSourceCategory(desc.getType());
										setSourceType(desc.getModelName());
										setSourceName(desc.getName().getName());
										setComponent(desc.getName());
									}
								};
								logEvent(ce);
								
								cms.getExternalAccess(desc.getName()).addResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										((IExternalAccess) result).scheduleStep(new IComponentStep()
										{
											public Object execute(IInternalAccess ia)
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
												return null;
											}
										});
									}
								});
								
								return IFuture.DONE;
							}
						});
						
						ret.setResult(null);
					}
				});
			}
		});
		
		/*SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				// TODO Auto-generated method stub
				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						((IExecutionService) result).addProcessListener(null, new IProcessListener()
						{
							public IFuture processFinished(ProcessEvent event)
							{
								Future ret = new Future();
								logEvent(new LogEvent(LogEvent.EVENT_TYPE_DISPOSAL,
													  LogEvent.EVENT_SOURCE_PROCESS_INSTANCE,
													  event.getInstanceId()));
								return ret;
							}
						});
					}
				});
				
				sFuture.addResultListener(new DelegationResultListener(ret));
			}
		});*/
		return ret;
	}
	
	@Override
	public IFuture shutdownService()
	{
		// TODO Auto-generated method stub
		return super.shutdownService();
	}
	
	/**
	 *  Adds a log listener.
	 *  @param client The client adding the listener or null for none.
	 *  @param listener The listener.
	 *  @param pastEvents True, if past events should be passed to the listener.
	 *  @return Indication of success.
	 */
	public IFuture addLogListener(IComponentIdentifier client, ILogListener listener, boolean pastEvents)
	{
		Future ret = new Future();
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
		ret.setResult(null);
		return ret;
	}
	
	/**
	 *  Removes a log listener.
	 *  
	 *  @param client The client adding the listener or null for none.
	 *  @param listener The listener.
	 *  @return Indication of success.
	 */
	public IFuture removeLogListener(IComponentIdentifier client, ILogListener listener)
	{
		Future ret = new Future();
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
	public IFuture logEvent(IComponentChangeEvent event)
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
		SServiceProvider.getService(provider, ILogService.class, RequiredServiceInfo.SCOPE_APPLICATION).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				((ILogService)result).logEvent(cce);
			}
		});
	}
}
