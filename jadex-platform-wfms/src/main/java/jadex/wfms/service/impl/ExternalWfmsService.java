package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.ICommand;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.AccessControlCheck;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IExternalWfmsService;
import jadex.wfms.service.ILogService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IWorkitemHandlerService;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.IWorkitemListener;

import java.util.HashMap;
import java.util.HashSet;

public class ExternalWfmsService extends BasicService implements IExternalWfmsService
{
	/** WfMS Service Container */
	protected IServiceContainer provider;
	
	public ExternalWfmsService(IServiceContainer provider)
	{
		super(provider.getId(), IExternalWfmsService.class, new HashMap());
		this.provider = provider;
	}
	
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture authenticate(final IComponentIdentifier client, final ClientInfo info)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				boolean auth = ((IAAAService) result).authenticate(client, info);
				if (auth)
					ret.setResult(Boolean.TRUE);
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
	public IFuture deauthenticate(final IComponentIdentifier client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IAAAService) result).deauthenticate(client);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 * Returns the capabilities of the client
	 * @param client the client
	 * @return set of capabilities
	 */
	public IFuture getCapabilities(final IComponentIdentifier client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IAAAService as = (IAAAService) result;
				ret.setResult(as.getCapabilities(as.getSecurityRole(as.getUserName(client))));
			}
		});
		return ret;
	}
	
	/**
	 *  Starts a new process
	 *  @param client the client
	 *  @param name name of the process
	 */
	public IFuture startProcess(IComponentIdentifier client, final String name)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final String filename = ((IModelRepositoryService) result).getProcessFileName(name);
						SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								((IExecutionService) result).startProcess(filename, null, null).addResultListener(new DelegationResultListener(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Gets the names of all available process models
	 *  @param client the client
	 *  @return the names of all available process models
	 */
	public IFuture getModelNames(IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_NAMES)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(((IModelRepositoryService) result).getModelNames());
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param activity the activity being finished
	 */
	public IFuture finishActivity(final IComponentIdentifier client, final IClientActivity activity)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.COMMIT_WORKITEM)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final String userName = ((IAAAService) result).getUserName(client);
						SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
								wh.finishActivity(userName , activity);
								ret.setResult(null);
							}
						});
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public IFuture beginActivity(final IComponentIdentifier client, final IWorkitem workitem)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ACQUIRE_WORKITEM)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final String userName = ((IAAAService) result).getUserName(client);
						SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
								wh.beginActivity(userName, workitem);
								ret.setResult(null);
							}
						});
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public IFuture cancelActivity(final IComponentIdentifier client, final IClientActivity activity)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.RELEASE_WORKITEM)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final String userName = ((IAAAService) result).getUserName(client);
						SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
								wh.cancelActivity(userName, activity);
								ret.setResult(null);
							}
						});
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns all workitems available to a client.
	 *  @param client the client
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public IFuture getAvailableWorkitems(final IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REQUEST_AVAILABLE_WORKITEMS)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final String userName = ((IAAAService) result).getUserName(client);
						SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
								wh.getAvailableWorkitems(userName).addResultListener(new DelegationResultListener(ret));
							};
						});
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
	public IFuture getAvailableActivities(final IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REQUEST_AVAILABLE_ACTIVITIES)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final String userName = ((IAAAService) result).getUserName(client);
						SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
								wh.getAvailableActivities(userName).addResultListener(new DelegationResultListener(ret));
							};
						});
					}
				});
			}
		});
		
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
		(new AccessControlCheck(client, IAAAService.ADD_WORKITEM_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.addWorkitemListener(client, listener).addResultListener(new DelegationResultListener(ret));
					};
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
		(new AccessControlCheck(client, IAAAService.REMOVE_WORKITEM_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.removeWorkitemListener(client, listener).addResultListener(new DelegationResultListener(ret));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADD_ACTIVITY_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.addActivityListener(client, listener).addResultListener(new DelegationResultListener(ret));
					};
				});
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
		(new AccessControlCheck(client, IAAAService.REMOVE_ACTIVITY_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.removeActivityListener(client, listener).addResultListener(new DelegationResultListener(ret));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Adds a process model resource to the repository
	 * @param client the client
	 * @param url url to the model resource
	 */
	public IFuture addProcessResource(IComponentIdentifier client, final ProcessResource resource)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_ADD_PROCESS_MODEL)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.addProcessResource(resource);
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 * Removes a process model resource from the repository
	 * @param client the client
	 * @param url url of the model resource
	 */
	public IFuture removeProcessResource(final IComponentIdentifier client, final String resourceName)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REMOVE_PROCESS_MODEL)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.removeProcessResource(resourceName);
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public IFuture getProcessModel(IComponentIdentifier client, final String name)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_PROCESS_MODEL)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.getProcessModel(name).addResultListener(new DelegationResultListener(future));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param client the client
	 * @param path path of the model
	 * @param imports the imports
	 * @return the model
	 */
	public IFuture loadProcessModel(final IComponentIdentifier client, final String path, final String[] imports)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_PROCESS_MODEL)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExecutionService es = (IExecutionService) result;
						es.loadModel(path, imports).addResultListener(new DelegationResultListener(future));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public IFuture getProcessModelNames(IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_NAMES)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						ret.setResult(new HashSet(mr.getModelNames()));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	public IFuture getLoadableModelPaths(IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_PATHS)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						ret.setResult(new HashSet(mr.getLoadableModels()));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessRepositoryListener(final IComponentIdentifier client, final IProcessRepositoryListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_ADD_REPOSITORY_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.addProcessRepositoryListener(client, listener);
						ret.setResult(null);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeProcessRepositoryListener(final IComponentIdentifier client, final IProcessRepositoryListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REMOVE_REPOSITORY_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.removeProcessRepositoryListener(client, listener);
						ret.setResult(null);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @param client the client
	 * @return current activities for all users
	 */
	public IFuture getUserActivities(IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADMIN_REQUEST_ALL_ACTIVITIES)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						ret.setResult(new HashMap(wh.getUserActivities()));
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param client the client issuing the termination request
	 * @param activity the activity
	 */
	public IFuture terminateActivity(IComponentIdentifier client, final IClientActivity activity)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADMIN_TERMINATE_ACTIVITY)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.terminateActivity(activity);
						ret.setResult(null);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Adds a user activities listener which will trigger for
	 * any activity event, even activities unrelated to the client.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addActivitiesListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADMIN_ADD_ACTIVITIES_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.addGlobalActivityListener(client, listener);
						ret.setResult(null);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Removes a user activities listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeActivitiesListener(final IComponentIdentifier client, final IActivityListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADMIN_REMOVE_ACTIVITIES_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IWorkitemHandlerService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.removeGlobalActivityListener(client, listener);
						ret.setResult(null);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Adds a log listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 * @param pastEvents True, if the listener wishes to receive past events.
	 */
	public IFuture addLogListener(final IComponentIdentifier client, final ILogListener listener, final boolean pastEvents)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADMIN_ADD_LOG_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, ILogService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ILogService ls = ((ILogService) result);
						ls.addLogListener(client, listener, pastEvents);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Removes a log listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeLogListener(final IComponentIdentifier client, final ILogListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.ADMIN_REMOVE_LOG_LISTENER)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, ILogService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ILogService ls = ((ILogService) result);
						ls.removeLogListener(client, listener);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessListener(final IComponentIdentifier client, final IProcessListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExecutionService es = ((IExecutionService) result);
						es.addProcessListener(client, listener).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeProcessListener(final IComponentIdentifier client, final IProcessListener listener)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExecutionService es = ((IExecutionService) result);
						es.removeProcessListener(client, listener).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		});
		
		return ret;
	}
}
