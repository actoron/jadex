package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.ILogService;
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.LogEvent;
import jadex.wfms.service.listeners.ProcessEvent;

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
	
	protected List<LogEvent> eventLog;
	
	protected Map<IComponentIdentifier, Set<ILogListener>> listeners;
	
	public LogService(IServiceContainer provider)
	{
		super(provider.getId(), ILogService.class, new HashMap());
		this.eventLog = Collections.synchronizedList(new ArrayList<LogEvent>());
		this.listeners = Collections.synchronizedMap(new HashMap<IComponentIdentifier, Set<ILogListener>>());
		this.provider = provider;
	}
	
	@Override
	public IFuture startService()
	{
		final IFuture sFuture = super.startService();
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
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
		});
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
					for (Iterator<LogEvent> it = eventLog.iterator(); it.hasNext();)
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
			Set lSet = listeners.get(client);
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
	
	protected void logEvent(LogEvent event)
	{
		synchronized (listeners)
		{
			eventLog.add(event);
			for (Iterator<Set<ILogListener>> it = listeners.values().iterator(); it.hasNext(); )
				for (Iterator<ILogListener> it2 = it.next().iterator(); it2.hasNext(); )
					it2.next().logMessage(event);
		}
	}
}
