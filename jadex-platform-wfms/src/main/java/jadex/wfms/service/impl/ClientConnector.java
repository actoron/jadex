package jadex.wfms.service.impl;

import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.ActivityEvent;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.IWorkitemListener;
import jadex.wfms.listeners.WorkitemEvent;
import jadex.wfms.service.AccessControlCheck;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IClientConnector;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class ClientConnector extends BasicService implements IClientConnector // implements IClientService, IWfmsClientService
{
	
	private IServiceContainer provider;
	
	private Map workitemQueues;
	
	private Map userActivities;
	
	private Map processWorkitemListeners;
	
	private Map workitemQueueListeners;
	
	private Map activityListeners;
	
	private Set wfmsActivityListeners;
	
	public ClientConnector(IServiceContainer provider)
	{
		super(provider.getId(), IClientConnector.class, null);
		//super(BasicService.createServiceIdentifier(provider.getId(), AdministrationService.class));
		System.out.println("Invoked");
		this.provider = provider;
		processWorkitemListeners = new HashMap();
		workitemQueues = new HashMap();
		userActivities = new HashMap();
		workitemQueueListeners = new HashMap();
		activityListeners = new HashMap();
		wfmsActivityListeners = new HashSet();
		System.out.println("Invoked");
	}
	
	//TODO: Hack!
	private boolean active = false;
	
	public IFuture startService()
	{
		if (!active)
		{
			active = true;
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
	 * Authenticated a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture authenticate(final IClient client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				boolean auth = ((IAAAService) result).authenticate(client);
				if (auth)
				{
					synchronized(ClientConnector.this)
					{
						activityListeners.put(client, new HashSet());
						if (!userActivities.containsKey(client.getUserName()))
							userActivities.put(client.getUserName(), new HashSet());
					}
					ret.setResult(Boolean.TRUE);
				}
				else
					ret.setResult(Boolean.FALSE);
			}
		});
		
		return ret;
	}
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public IFuture deauthenticate(final IClient client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				synchronized(ClientConnector.this)
				{
					workitemQueueListeners.remove(client);
					activityListeners.remove(client);
					((IAAAService) result).deauthenticate(client);
				}
			}
		});
		return ret;
	}
	
	/**
	 * Starts a new BPMN-process
	 * 
	 * @param client the client
	 * @param name name of the process
	 */
	/*public synchronized void startBpmnProcess(IClient client, String name)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.START_BPMN_PROCESS))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
		String filename = rs.getProcessModel(name).getFilename();
		
		IExecutionService bps = (IExecutionService)wfms.getService(IExecutionService.class);
		Object id  = bps.startProcess(filename, null, null, false);
		System.out.println("Started process instance " + id);
	}*/
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	/*public synchronized Set getBpmnModelNames(IClient client)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}*/
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param workitem the activity being finished
	 */
	public synchronized void finishActivity(IClient client, IClientActivity activity)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.COMMIT_WORKITEM))
			throw new AccessControlException("Not allowed: "+client);
		IResultListener listener = (IResultListener) processWorkitemListeners.remove(activity);
		((HashSet) userActivities.get(client.getUserName())).remove(activity);
		fireActivityRemovedEvent(client.getUserName(), activity);
		listener.resultAvailable(this, activity);
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public synchronized void beginActivity(IClient client, IWorkitem workitem)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.ACQUIRE_WORKITEM))
			throw new AccessControlException("Not allowed: "+client);
		Set workitems = (Set) workitemQueues.get(workitem.getRole());
		if (workitems.remove(workitem))
		{
			fireWorkitemRemovedEvent(workitem);
			((HashSet) userActivities.get(client.getUserName())).add(workitem);
			fireActivityAddedEvent(client.getUserName(), (IClientActivity) workitem);
		}
	}
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public void cancelActivity(IClient client, IClientActivity activity)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.RELEASE_WORKITEM))
			throw new AccessControlException("Not allowed: "+client);
		((HashSet) userActivities.get(client.getUserName())).remove(activity);
		fireActivityRemovedEvent(client.getUserName(), activity);
		queueWorkitem((IWorkitem) activity, null);
	}
	
	public IFuture getAvailableWorkitems(final IClient client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, new Integer[] { IAAAService.REQUEST_AVAILABLE_WORKITEMS, IAAAService.ADD_WORKITEM_LISTENER }))
			.checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAAAService.class)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						Set roles = ((IAAAService) result).getRoles(client.getUserName());
						Set workitems = new HashSet();
						synchronized(ClientConnector.this)
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
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(final IClient client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REQUEST_AVAILABLE_ACTIVITIES)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				synchronized(ClientConnector.this)
				{
					ret.setResult(new HashSet((Set) userActivities.get(client.getUserName())));
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture addWorkitemListener(final IClient client, final IWorkitemListener listener)
	{
		final Future ret = new Future();
		
		(new AccessControlCheck(client, IAAAService.ADD_WORKITEM_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				getAvailableWorkitems(client).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						synchronized(ClientConnector.this)
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
	public IFuture removeWorkitemListener(final IClient client, final IWorkitemListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REMOVE_WORKITEM_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				synchronized(ClientConnector.this)
				{
					Set listeners = (Set) workitemQueueListeners.get(client);
					if (listeners != null)
						listeners.remove(listener);
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 */
	public synchronized void addActivityListener(IActivityListener listener)
	{
		wfmsActivityListeners.add(listener);
		
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
	public synchronized void removeActivityListener(IActivityListener listener)
	{
		wfmsActivityListeners.remove(listener);
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(final IClient client, final IActivityListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADD_ACTIVITY_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				synchronized(ClientConnector.this)
				{
					Set listeners = (Set) activityListeners.get(client);
					if (listeners == null)
					{
						listeners = new HashSet();
						activityListeners.put(client, listeners);
					}
					listeners.add(listener);
					Set activities = (Set) userActivities.get(client.getUserName());
					if (activities != null)
					{
						for (Iterator it = activities.iterator(); it.hasNext(); )
						{
							listener.activityAdded(new ActivityEvent(client.getUserName(), (IClientActivity) it.next()));
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
	public IFuture removeActivityListener(final IClient client, final IActivityListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REMOVE_ACTIVITY_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				synchronized (ClientConnector.this)
				{
					Set listeners = (Set) activityListeners.get(client);
					if (listeners != null)
						listeners.remove(listener);
				}
			}
		});
		
		return ret;
	}
	
	private synchronized void fireWorkitemAddedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable());
		for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			IClient client = (IClient) entry.getKey();
			Set listeners = (Set) entry.getValue();
			
			if (as.getRoles(client.getUserName()).contains(workitem.getRole()) ||
				workitem.getRole().equals(IAAAService.ANY_ROLE) ||
				as.getRoles(client.getUserName()).contains(IAAAService.ALL_ROLES))
			{
				WorkitemEvent evt = new WorkitemEvent(workitem);
				for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
					((IWorkitemListener) it2.next()).workitemAdded(evt);
			}
		}
	}
	
	private synchronized void fireWorkitemRemovedEvent(IWorkitem workitem)
	{
		IAAAService as = (IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable());
		for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			IClient client = (IClient) entry.getKey();
			Set listeners = (Set) entry.getValue();
			
			if (as.getRoles(client.getUserName()).contains(workitem.getRole()) ||
				workitem.getRole().equals(IAAAService.ANY_ROLE) ||
				as.getRoles(client.getUserName()).contains(IAAAService.ALL_ROLES))
			{
				WorkitemEvent evt = new WorkitemEvent(workitem);
				for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
					((IWorkitemListener) it2.next()).workitemRemoved(evt);
			}
		}
	}
	
	private synchronized void fireActivityAddedEvent(String userName, IClientActivity activity)
	{
		IAAAService as = (IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable());
		Set clients = as.getAuthenticatedClients(userName);
		
		for (Iterator it = clients.iterator(); it.hasNext(); )
		{
			IClient currentClient = (IClient) it.next();
			
			Set listeners = (Set) activityListeners.get(currentClient);
			
			for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
			{
				IActivityListener listener = (IActivityListener) it2.next();
				ActivityEvent evt = new ActivityEvent(userName, activity);
				listener.activityAdded(evt);
			}
		}
		
		for (Iterator it = wfmsActivityListeners.iterator(); it.hasNext(); )
			((IActivityListener) it.next()).activityAdded(new ActivityEvent(userName, activity));
	}
	
	private synchronized void fireActivityRemovedEvent(String userName, IClientActivity activity)
	{
		IAAAService as = (IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable());
		Set clients = as.getAuthenticatedClients(userName);
		
		for (Iterator it = clients.iterator(); it.hasNext(); )
		{
			IClient currentClient = (IClient) it.next();
			
			Set listeners = (Set) activityListeners.get(currentClient);
			
			for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
			{
				IActivityListener listener = (IActivityListener) it2.next();
				ActivityEvent evt = new ActivityEvent(userName, activity);
				listener.activityRemoved(evt);
			}
		}
		
		for (Iterator it = wfmsActivityListeners.iterator(); it.hasNext(); )
			((IActivityListener) it.next()).activityRemoved(new ActivityEvent(userName, activity));
	}
}
