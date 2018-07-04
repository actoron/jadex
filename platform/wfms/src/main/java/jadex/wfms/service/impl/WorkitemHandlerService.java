package jadex.wfms.service.impl;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.SUtil;
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
@Service
public class WorkitemHandlerService implements IWorkitemHandlerService
{
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	/** The workitem queues */
	protected Map<String, Set<IWorkitem>> workitemQueues;
	
	/** Current user activities */
	protected Map<String, Set<IClientActivity>> userActivities;
	
	
	protected Map processWorkitemListeners;
	
	/** Listeners for workitems */
	protected Map workitemQueueListeners;
	
	/** User-specific activity listeners */
	protected Map activityListeners;
	
	/** Global (not user-specific) activity listeners */
	protected Map globalActivityListeners;
	
	/** Authentication listener */
	protected IAuthenticationListener authlistener;
	
	public WorkitemHandlerService()
	{
		processWorkitemListeners = new HashMap();
		workitemQueues = new HashMap<String, Set<IWorkitem>>();
		userActivities = new HashMap<String, Set<IClientActivity>>();
		workitemQueueListeners = new HashMap();
		activityListeners = new HashMap();
		globalActivityListeners = new HashMap();
	}
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture startService()
	{
		final Future ret = new Future();
		
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				final IExternalAccess exta = ia.getExternalAccess();
				authlistener = new IAuthenticationListener()
				{
					public IFuture deauthenticated(final IComponentIdentifier client, ClientInfo info)
					{
						return exta.scheduleStep(new IComponentStep<Void>()
						{
							
							public IFuture<Void> execute(IInternalAccess ia)
							{
								processWorkitemListeners.remove(client);
								workitemQueues.remove(client);
								userActivities.remove(client);
								workitemQueueListeners.remove(client);
								activityListeners.remove(client);
								globalActivityListeners.remove(client);
								return IFuture.DONE;
							}
						});
					}
					
					public IFuture authenticated(IComponentIdentifier client, ClientInfo info)
					{
						return IFuture.DONE;
					}
				};
				
				as.addAuthenticationListener(authlistener).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
			}
		}));
		return ret;
	}
	
	/**
	 *  Stops the service.
	 */
	@ServiceShutdown
	public IFuture shutdownService()
	{
		final Future ret = new Future();
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				as.removeAuthenticationListener(authlistener);
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 *  Queues a new Workitem.
	 *  @param workitem the workitem
	 *  @param listener result listener
	 *  @return Null, when done.
	 */
	public IFuture<Void> queueWorkitem(final IWorkitem workitem, IResultListener listener)
	{
		ComponentChangeEvent.getTimeStamp(ia.getServiceContainer()).addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				LogService.dispatchLogServiceEvent(ia.getServiceContainer(), new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_CREATION, SOURCE_CATEGORY_WORKITEM, workitem.getName(), workitem.getId(), workitem.getProcess(), workitem.getProcessCreationTime(), null, null, (Long) result));
			}
		}));
		
		requeueWorkitem(workitem, listener);
		return IFuture.DONE;
	}
	
	protected void requeueWorkitem(final IWorkitem workitem, IResultListener listener)
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
	 *  Withdraws a Workitem/Activity.
	 *  @param workitem the workitem being terminated
	 *  @return Null, when done.
	 */
	public IFuture<Void> withdrawWorkitem(final IWorkitem workitem)
	{
		final Future<Void> ret = new Future<Void>();
		terminateActivity((IClientActivity) workitem).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IResultListener listener = (IResultListener) processWorkitemListeners.remove(workitem);
				listener.exceptionOccurred(new RuntimeException("Workitem terminated."));
				Set<IWorkitem> workitems = workitemQueues.get(workitem.getRole());
				if (workitems.remove(workitem))
				{
					fireWorkitemRemovedEvent(workitem);
				}
				
				ComponentChangeEvent.getTimeStamp(ia.getServiceContainer()).addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						LogService.dispatchLogServiceEvent(ia.getServiceContainer(), new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_DISPOSAL, SOURCE_CATEGORY_WORKITEM, workitem.getName(), workitem.getId(), workitem.getProcess(), workitem.getProcessCreationTime(), "Withdrawb", null, (Long) result));
					}
				}));
				
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @return current activities for all users
	 */
	public IFuture<Map<String, Set<IClientActivity>>> getUserActivities()
	{
		Map ret = new HashMap();
		for (Iterator<Map.Entry<String, Set<IClientActivity>>> it = userActivities.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, Set<IClientActivity>> userEntry = it.next();
			Set<IClientActivity> activities = userEntry.getValue();
			ret.put(userEntry.getKey(), new HashSet<IClientActivity>(activities));
		}
		
		return new Future<Map<String, Set<IClientActivity>>>(ret);
	}
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param activity the activity
	 * @return Null, when done.
	 */
	public IFuture<Void> terminateActivity(final IClientActivity activity)
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
			final String id = activity.getActivityId();
			final String user = userName;
			activity.setActivityId(null);
			ComponentChangeEvent.getTimeStamp(ia.getServiceContainer()).addResultListener(ia.createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					LogService.dispatchLogServiceEvent(ia.getServiceContainer(), new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_DISPOSAL, SOURCE_CATEGORY_ACTIVITY, activity.getName(), id, activity.getProcess(), activity.getProcessCreationTime(), "Terminated", user, (Long) result));
				}
			}));
			((Set) userActivities.get(userName)).remove(activity);
			fireActivityRemovedEvent(userName, activity);
			requeueWorkitem((IWorkitem) activity, null);
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Finishes an Activity.
	 *  @param userName the user name
	 *  @param activity the activity being finished
	 *  @return Null, when done.
	 */
	public IFuture<Void> finishActivity(final String userName, IClientActivity activity)
	{
		final IWorkitem workitem = (IWorkitem) activity;
		final String id = activity.getActivityId();
		activity.setActivityId(null);
		ComponentChangeEvent.getTimeStamp(ia.getServiceContainer()).addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				LogService.dispatchLogServiceEvent(ia.getServiceContainer(), new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_DISPOSAL, SOURCE_CATEGORY_ACTIVITY, workitem.getName(), id, workitem.getProcess(), workitem.getProcessCreationTime(), "Finished", userName, (Long) result));
				LogService.dispatchLogServiceEvent(ia.getServiceContainer(), new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_DISPOSAL, SOURCE_CATEGORY_WORKITEM, workitem.getName(), workitem.getId(), workitem.getProcess(), workitem.getProcessCreationTime(), "Finished", null, (Long) result));
			}
		}));
		IResultListener listener = (IResultListener) processWorkitemListeners.remove(activity);
		((HashSet) userActivities.get(userName)).remove(activity);
		fireActivityRemovedEvent(userName, activity);
		listener.resultAvailable(activity);
		return IFuture.DONE;
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param userName the user name
	 *  @param workitem the workitem being requested for the activity
	 *  @return Null, when done.
	 */
	public IFuture<Void> beginActivity(final String userName, final IWorkitem workitem)
	{
		final IClientActivity activity = (IClientActivity) workitem;
		activity.setActivityId(SUtil.createUniqueId("Activity"));
		ComponentChangeEvent.getTimeStamp(ia.getServiceContainer()).addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ComponentChangeEvent event = new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_CREATION, SOURCE_CATEGORY_ACTIVITY, activity.getName(), activity.getActivityId(), activity.getProcess(), activity.getProcessCreationTime(), "Requested", userName, (Long) result);
				event.setParent(activity.getId());
				LogService.dispatchLogServiceEvent(ia.getServiceContainer(), event);
			}
		}));
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
			activities.add(activity);
			fireActivityAddedEvent(userName, activity);
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Cancel an activity.
	 *  @param userName the user name
	 *  @param activity the activity being canceled
	 *  @return Null, when done.
	 */
	public IFuture<Void> cancelActivity(final String userName, final IClientActivity activity)
	{
		final String id = activity.getActivityId();
		activity.setActivityId(null);
		ComponentChangeEvent.getTimeStamp(ia.getServiceContainer()).addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				LogService.dispatchLogServiceEvent(ia.getServiceContainer(), new ComponentChangeEvent(ComponentChangeEvent.EVENT_TYPE_DISPOSAL, SOURCE_CATEGORY_ACTIVITY, activity.getName(), id, activity.getProcess(), activity.getProcessCreationTime(), "Cancelled", userName, (Long) result));
			}
		}));
		((HashSet) userActivities.get(userName)).remove(activity);
		fireActivityRemovedEvent(userName, activity);
		requeueWorkitem((IWorkitem) activity, null);
		return IFuture.DONE;
	}
	
	/**
	 *  Gets the available workitems for the given user.
	 *  
	 *  @param userName The user.
	 *  @return The available workitems.
	 */
	public IFuture<Set<IWorkitem>> getAvailableWorkitems(final String username)
	{
		final Future ret = new Future();
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IAAAService) result).getRoles(username).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						Set roles = (Set) result;
						Set<IWorkitem> workitems = new HashSet();
						if (roles.contains(IAAAService.ALL_ROLES))
						{
							for (Iterator<Set<IWorkitem>> it = workitemQueues.values().iterator(); it.hasNext(); )
							{
								Set<IWorkitem> roleItems = it.next();
								workitems.addAll(roleItems);
							}
						}
						else
						{
							for (Iterator it = roles.iterator(); it.hasNext(); )
							{
								Set<IWorkitem> roleItems = workitemQueues.get(it.next());
								if (roleItems != null)
									workitems.addAll(roleItems);
							}
						}
						ret.setResult(workitems);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Returns all activities available to a client.
	 *  @param userName the user name
	 *  @return a set of activities that are available for this client
	 */
	public IFuture<Set<IClientActivity>> getAvailableActivities(final String userName)
	{
		final Future ret = new Future();
		ret.setResult(new HashSet<IClientActivity>(userActivities.get(userName)));
		return ret;
	}
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture<Void> addWorkitemListener(final IComponentIdentifier client, final IWorkitemListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				as.getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						String username = (String) result;
						getAvailableWorkitems(username).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
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
								ret.setResult(null);
							}
						}));
					}
				}));
			}
		}));
		
		
		return ret;
	}
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture<Void> removeWorkitemListener(final IComponentIdentifier client, final IWorkitemListener listener)
	{
		//final Future ret = new Future();
		Set listeners = (Set) workitemQueueListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 *  @return Null, when done.
	 */
	public IFuture<Void> addGlobalActivityListener(IComponentIdentifier client, IActivityListener listener)
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
		return IFuture.DONE;
	}
	
	/**
	 *  Removes a listener for activity changes.
	 *  @param listener activity listener
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeGlobalActivityListener(IComponentIdentifier client, IActivityListener listener)
	{
		Set listeners = (Set) globalActivityListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture<Void> addActivityListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				as.getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						String username = (String) result;
						Set listeners = (Set) activityListeners.get(client);
						if (listeners == null)
						{
							listeners = new HashSet();
							activityListeners.put(client, listeners);
						}
						listeners.add(listener);
						Set activities = (Set) userActivities.get(username);
						if (activities != null)
						{
							for (Iterator it = activities.iterator(); it.hasNext(); )
							{
								listener.activityAdded(new ActivityEvent(username, (IClientActivity) it.next()));
							}
						}
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture<Void> removeActivityListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		//final Future ret = new Future();
		Set listeners = (Set) activityListeners.get(client);
		if (listeners != null)
			listeners.remove(listener);
		
		return IFuture.DONE;
	}
	
	private void fireWorkitemAddedEvent(final IWorkitem workitem)
	{
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IAAAService as = (IAAAService) result;
				for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) it.next();
					IComponentIdentifier client = (IComponentIdentifier) entry.getKey();
					final Set listeners = (Set) entry.getValue();
					
					as.getUserName(client).addResultListener(ia.createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							String username = (String) result;
							as.getRoles(username).addResultListener(ia.createResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object result)
								{
									Set roles = (Set) result;
									if (roles.contains(workitem.getRole()) ||
										workitem.getRole().equals(IAAAService.ANY_ROLE) ||
										roles.contains(IAAAService.ALL_ROLES))
									{
										WorkitemEvent evt = new WorkitemEvent(workitem);
										for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
											((IWorkitemListener) it2.next()).workitemAdded(evt);
									}
								}
							}));
						}
					}));
				}
			}
		}));
	}
	
	private void fireWorkitemRemovedEvent(final IWorkitem workitem)
	{
		// Workitem Disposed event CANNOT be here since its semantics are different
		// (workitems are not "disposed" when an activity starts)
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				final IAAAService as = (IAAAService) result;
				for (Iterator it = workitemQueueListeners.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Map.Entry) it.next();
					IComponentIdentifier client = (IComponentIdentifier) entry.getKey();
					final Set listeners = (Set) entry.getValue();
					
					as.getUserName(client).addResultListener(ia.createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							String username = (String) result;
							as.getRoles(username).addResultListener(ia.createResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object result)
								{
									Set roles = (Set) result;
									if (roles.contains(workitem.getRole()) ||
										workitem.getRole().equals(IAAAService.ANY_ROLE) ||
										roles.contains(IAAAService.ALL_ROLES))
									{
										WorkitemEvent evt = new WorkitemEvent(workitem);
										for (Iterator it2 = listeners.iterator(); it2.hasNext(); )
											((IWorkitemListener) it2.next()).workitemRemoved(evt);
									}
								}
							}));
						}
					}));
				}
			}
		}));
	}
	
	private void fireActivityAddedEvent(final String userName, final IClientActivity activity)
	{
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				as.getAuthenticatedClients(userName).addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						Set clients = (Set) result;
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
				}));
			}
		}));
	}
	
	private void fireActivityRemovedEvent(final String userName, final IClientActivity activity)
	{
		ia.getServiceContainer().getService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				as.getAuthenticatedClients(userName).addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						Set clients = (Set) result;
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
				}));
			}
		}));
	}
}
