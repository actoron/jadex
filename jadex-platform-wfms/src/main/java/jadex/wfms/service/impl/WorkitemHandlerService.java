package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IWorkitemHandlerService;
import jadex.wfms.service.listeners.ActivityEvent;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.IAuthenticationListener;
import jadex.wfms.service.listeners.IWorkitemListener;
import jadex.wfms.service.listeners.WorkitemEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class WorkitemHandlerService extends BasicService implements IWorkitemHandlerService
{
	
	private IServiceContainer provider;
	
	private Map workitemQueues;
	
	private Map userActivities;
	
	private Map processWorkitemListeners;
	
	private Map workitemQueueListeners;
	
	private Map activityListeners;
	
	private Map globalActivityListeners;
	
	public WorkitemHandlerService(IServiceContainer provider)
	{
		super(provider.getId(), IWorkitemHandlerService.class, null);
		this.provider = provider;
		processWorkitemListeners = new HashMap();
		workitemQueues = new HashMap();
		userActivities = new HashMap();
		workitemQueueListeners = new HashMap();
		activityListeners = new HashMap();
		globalActivityListeners = new HashMap();
	}
	
	//TODO: Hack!
	private boolean active = false;
	
	public IFuture startService()
	{
		if (!active)
		{
			active = true;
			SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IAAAService as = (IAAAService) result;
					as.addAuthenticationListener(new IAuthenticationListener()
					{
						public void deauthenticated(IComponentIdentifier client, ClientInfo info)
						{
							processWorkitemListeners.remove(client);
							workitemQueues.remove(client);
							userActivities.remove(client);
							workitemQueueListeners.remove(client);
							activityListeners.remove(client);
							globalActivityListeners.remove(client);
						}
						
						public void authenticated(IComponentIdentifier client, ClientInfo info)
						{
						}
					});
				}
			});
			return super.startService();
		}
		return new Future(null);
	}
	
	public IFuture shutdownService()
	{
		if (active)
		{
			active = false;
			return super.shutdownService();
		}
		return new Future(null);
	}
	
	public synchronized void queueWorkitem(IWorkitem workitem, IResultListener listener)
	{
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems == null)
		{
			workitems = new HashSet();
			workitemQueues.put(workitem.getRole(), workitems);
		}
		workitems.add(workitem);
		if (listener != null)
			processWorkitemListeners.put(workitem, listener);
		fireWorkitemAddedEvent(workitem);
	}
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @return current activities for all users
	 */
	public synchronized Map getUserActivities()
	{
		Map ret = new HashMap();
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry userEntry = (Map.Entry) it.next();
			Set activities = (Set) userEntry.getValue();
			ret.put(userEntry.getKey(), new HashSet(activities));
		}
		
		return ret;
	}
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param activity the activity
	 */
	public synchronized void terminateActivity(IClientActivity activity)
	{
		String userName = null;
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			Set activities = (Set) entry.getValue();
			if (activities.contains(activity))
			{
				userName = (String) entry.getKey();
				break;
			}
		}
		if (userName != null)
		{
			((Set) userActivities.get(userName)).remove(activity);
			fireActivityRemovedEvent(userName, activity);
			queueWorkitem((IWorkitem) activity, null);
		}
	}
	
	/**
	 *  Finishes an Activity.
	 *  @param userName the user name
	 *  @param workitem the activity being finished
	 */
	public synchronized void finishActivity(String userName, IClientActivity activity)
	{
		IResultListener listener = (IResultListener) processWorkitemListeners.remove(activity);
		((HashSet) userActivities.get(userName)).remove(activity);
		fireActivityRemovedEvent(userName, activity);
		listener.resultAvailable(activity);
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param userName the user name
	 *  @param workitem the workitem being requested for the activity
	 */
	public synchronized void beginActivity(String userName, IWorkitem workitem)
	{
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems.remove(workitem))
		{
			fireWorkitemRemovedEvent(workitem);
			Set activities = (Set) userActivities.get(userName);
			if (activities == null)
			{
				activities = new HashSet();
				userActivities.put(userName, activities);
			}
			activities.add(workitem);
			fireActivityAddedEvent(userName, (IClientActivity) workitem);
		}
	}
	
	/**
	 *  Cancel an activity.
	 *  @param userName the user name
	 *  @param activity the activity being canceled
	 */
	public void cancelActivity(String userName, IClientActivity activity)
	{
		((HashSet) userActivities.get(userName)).remove(activity);
		fireActivityRemovedEvent(userName, activity);
		queueWorkitem((IWorkitem) activity, null);
	}
	
	public IFuture getAvailableWorkitems(final String userName)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Set roles = ((IAAAService) result).getRoles(userName);
				Set workitems = new HashSet();
				synchronized(WorkitemHandlerService.this)
				{
					if (roles.contains(IAAAService.ALL_ROLES))
					{
						for (Iterator it = workitemQueues.values().iterator(); it.hasNext(); )
						{
							Set roleItems = (Set) it.next();
							workitems.addAll(roleItems);
						}
					}
					else
					{
						for (Iterator it = roles.iterator(); it.hasNext(); )
						{
							Set roleItems = (Set) workitemQueues.get(it.next());
							if (roleItems != null)
								workitems.addAll(roleItems);
						}
					}
				}
				ret.setResult(workitems);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns all activities available to a client.
	 *  @param userName the user name
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(final String userName)
	{
		final Future ret = new Future();
		synchronized(WorkitemHandlerService.this)
		{
			ret.setResult(new HashSet((Set) userActivities.get(userName)));
		}
		return ret;
	}
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture addWorkitemListener(final IComponentIdentifier client, final IWorkitemListener listener)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				String userName = as.getUserName(client);
				getAvailableWorkitems(userName).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						synchronized(WorkitemHandlerService.this)
						{
							Set workitems = (Set) result;
							Set listeners = (Set) workitemQueueListeners.get(client);
							if (listeners == null)
							{
								listeners = new HashSet();
								workitemQueueListeners.put(client, listeners);
							}
							listeners.add(listener);
						
							if (workitems != null)
							{
								for (Iterator it = workitems.iterator(); it.hasNext(); )
								{
									listener.workitemAdded(new WorkitemEvent((IWorkitem) it.next()));
								}
							}
						}
						ret.setResult(null);
					}
				});
			}
		});
		
		
		return ret;
	}
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture removeWorkitemListener(final IComponentIdentifier client, final IWorkitemListener listener)
	{
		final Future ret = new Future();
		synchronized(WorkitemHandlerService.this)
		{
			Set listeners = (Set) workitemQueueListeners.get(client);
			if (listeners != null)
				listeners.remove(listener);
		}
		return ret;
	}
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 */
	public synchronized void addGlobalActivityListener(IComponentIdentifier client, IActivityListener listener)
	{
		Set listeners = (Set) globalActivityListeners.get(client);
		if (listeners == null)
		{
			listeners = new HashSet();
			globalActivityListeners.put(client, listeners);
		}
		listeners.add(listener);
		
		for (Iterator it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			String userName = (String) entry.getKey();
			Set activities = (Set) entry.getValue();
			for (Iterator it2 = activities.iterator(); it2.hasNext(); )
				listener.activityAdded(new ActivityEvent(userName, (IClientActivity) it2.next()));
		}
	}
	
	/**
	 *  Removes a listener for activity changes.
	 *  @param listener activity listener
	 */
	public synchronized void removeGlobalActivityListener(IComponentIdentifier client, IActivityListener listener)
	{
		Set listeners = (Set) globalActivityListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				String userName = as.getUserName(client);
				synchronized(WorkitemHandlerService.this)
				{
					Set listeners = (Set) activityListeners.get(client);
					if (listeners == null)
					{
						listeners = new HashSet();
						activityListeners.put(client, listeners);
					}
					listeners.add(listener);
					Set activities = (Set) userActivities.get(userName);
					if (activities != null)
					{
						for (Iterator it = activities.iterator(); it.hasNext(); )
						{
							listener.activityAdded(new ActivityEvent(userName, (IClientActivity) it.next()));
						}
					}
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture removeActivityListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		final Future ret = new Future();
		synchronized (WorkitemHandlerService.this)
		{
			Set listeners = (Set) activityListeners.get(client);
			if (listeners != null)
				listeners.remove(listener);
		}
		
		return ret;
	}
	
	private synchronized void fireWorkitemAddedEvent(final IWorkitem workitem)
	{
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) it.next();
					IComponentIdentifier client = (IComponentIdentifier) entry.getKey();
					Set listeners = (Set) entry.getValue();
					
					if (as.getRoles(as.getUserName(client)).contains(workitem.getRole()) ||
						workitem.getRole().equals(IAAAService.ANY_ROLE) ||
						as.getRoles(as.getUserName(client)).contains(IAAAService.ALL_ROLES))
					{
						WorkitemEvent evt = new WorkitemEvent(workitem);
						for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
							((IWorkitemListener) it2.next()).workitemAdded(evt);
					}
				}
			}
		});
	}
	
	private synchronized void fireWorkitemRemovedEvent(final IWorkitem workitem)
	{
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) it.next();
					IComponentIdentifier client = (IComponentIdentifier) entry.getKey();
					Set listeners = (Set) entry.getValue();
					
					if (as.getRoles(as.getUserName(client)).contains(workitem.getRole()) ||
						workitem.getRole().equals(IAAAService.ANY_ROLE) ||
						as.getRoles(as.getUserName(client)).contains(IAAAService.ALL_ROLES))
					{
						WorkitemEvent evt = new WorkitemEvent(workitem);
						for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
							((IWorkitemListener) it2.next()).workitemRemoved(evt);
					}
				}
			}
		});
	}
	
	private synchronized void fireActivityAddedEvent(final String userName, final IClientActivity activity)
	{
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				Set clients = as.getAuthenticatedClients(userName);
				
				for (Iterator it = clients.iterator(); it.hasNext(); )
				{
					IComponentIdentifier currentClient = (IComponentIdentifier) it.next();
					
					Set listeners = (Set) activityListeners.get(currentClient);
					
					for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
					{
						IActivityListener listener = (IActivityListener) it2.next();
						ActivityEvent evt = new ActivityEvent(userName, activity);
						listener.activityAdded(evt);
					}
				}
				
				for (Iterator it = globalActivityListeners.values().iterator(); it.hasNext(); )
					for (Iterator it2 = ((Set) it.next()).iterator(); it2.hasNext(); )
						((IActivityListener) it2.next()).activityAdded(new ActivityEvent(userName, activity));
			}
		});
	}
	
	private synchronized void fireActivityRemovedEvent(final String userName, final IClientActivity activity)
	{
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				Set clients = as.getAuthenticatedClients(userName);
				
				for (Iterator it = clients.iterator(); it.hasNext(); )
				{
					IComponentIdentifier currentClient = (IComponentIdentifier) it.next();
					
					Set listeners = (Set) activityListeners.get(currentClient);
					
					for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
					{
						IActivityListener listener = (IActivityListener) it2.next();
						ActivityEvent evt = new ActivityEvent(userName, activity);
						listener.activityRemoved(evt);
					}
				}
				
				for (Iterator it = globalActivityListeners.values().iterator(); it.hasNext(); )
					for (Iterator it2 = ((Set) it.next()).iterator(); it2.hasNext(); )
						((IActivityListener) it2.next()).activityRemoved(new ActivityEvent(userName, activity));
			}
		});
	}
}
