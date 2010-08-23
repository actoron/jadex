package jadex.wfms.service.impl;

import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.IWorkitemListener;
import jadex.wfms.service.AccessControlCheck;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IProcessDefinitionService;

public class ClientService extends BasicService implements IClientService
{
	private IServiceContainer provider;
	
	public ClientService(IServiceContainer provider)
	{
		super(provider.getId(), IClientService.class, null);
		this.provider = provider;
	}
	
	/**
	 * Requests the Process Definition Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public IFuture getProcessDefinitionService(final IClient client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REQUEST_PD_SERVICE)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IProcessDefinitionService.class).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 * Requests the Monitoring Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public IFuture getMonitoringService(final IClient client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.REQUEST_MONITORING_SERVICE)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IAdministrationService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void resultAvailable(Object source, Object result)
					{
						ret.setResult(result);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * Authenticated a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture authenticate(final IClient client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).authenticate(client).addResultListener(new DelegationResultListener(ret));
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
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).deauthenticate(client).addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 * Returns the capabilities of the client
	 * @param client the client
	 * @return set of capabilities
	 */
	public IFuture getCapabilities(final IClient client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				IAAAService as = (IAAAService) result;
				ret.setResult(as.getCapabilities(as.getSecurityRole(client.getUserName())));
			}
		});
		return ret;
	}
	
	/**
	 *  Starts a new process
	 *  @param client the client
	 *  @param name name of the process
	 */
	public IFuture startProcess(final IClient client, final String name)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void resultAvailable(Object source, Object result)
					{
						final String filename = ((IModelRepositoryService) result).getProcessFileName(name);
						SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DelegationResultListener(ret)
						{
							public void resultAvailable(Object source,
									Object result)
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
	public IFuture getModelNames(IClient client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_NAMES)).checkAccess(ret, provider, new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.getService(provider, IModelRepositoryService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void resultAvailable(Object source, Object result)
					{
						ret.setResult(((IModelRepositoryService) result).getModelNames());
					}
				});
			}
		});
		return ret;
	}
	//public Set getModelNames(IClient client);
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param activity the activity being finished
	 */
	public IFuture finishActivity(final IClient client, final IClientActivity activity)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).finishActivity(client, activity);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public IFuture beginActivity(final IClient client, final IWorkitem workitem)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).beginActivity(client, workitem);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public IFuture cancelActivity(final IClient client, final IClientActivity activity)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).cancelActivity(client, activity);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns all workitems available to a client.
	 *  @param client the client
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public IFuture getAvailableWorkitems(final IClient client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).getAvailableWorkitems(client).addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	//public Set getAvailableWorkitems(IClient client);
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(final IClient client)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).getAvailableActivities(client).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	//public Set getAvailableActivities(IClient client);
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture addWorkitemListener(final IClient client, final IWorkitemListener listener)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DelegationResultListener(ret)
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).addWorkitemListener(client, listener).addResultListener(new DelegationResultListener(ret));
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
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).removeWorkitemListener(client, listener).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(final IClient client, final IActivityListener listener)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).addActivityListener(client, listener).addResultListener(new DelegationResultListener(ret));
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
		SServiceProvider.getService(provider, ClientConnector.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				((ClientConnector) result).removeActivityListener(client, listener).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
}
