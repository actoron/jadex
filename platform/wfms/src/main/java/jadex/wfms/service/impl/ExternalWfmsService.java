package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.ICommand;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
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
import jadex.wfms.service.ProcessResourceInfo;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.IWorkitemListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ExternalWfmsService implements IExternalWfmsService
{
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	public ExternalWfmsService()
	{
	}
	
	/**
	 *  Returns the name of the Workflow Management System.
	 *  @return Name of the Workflow Management System.
	 */
	public IFuture<IComponentIdentifier> getName()
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>(ia.getServiceContainer().getId());
		
		/*ia.getServiceContainer().getParent().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				System.out.println((((IServiceContainer) result).getId()));
				ret.setResult(((IServiceContainer) result).getId());
				((IServiceContainer) result).getParent().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						System.out.println((((IServiceContainer) result).getId()));
						ret.setResult(((IServiceContainer) result).getId());
					}
				});
			}
		});*/
		return ret;
	}
	
	/**
	 * Authenticate a new client.
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture<Void> authenticate(final ClientInfo info)
	{
		final Future ret = new Future();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IAAAService) result).authenticate(client, info).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
			}
		}));
		
		return ret;
	}
	
	/**
	 * De-authenticate a client.
	 */
	public IFuture<Void> deauthenticate()
	{
		final Future ret = new Future();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class))
			.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((IAAAService) result).deauthenticate(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
			}
		}));
		return ret;
	}
	
	/**
	 * Returns the capabilities of the client
	 * @return set of capabilities
	 */
	public IFuture<Set<Integer>> getCapabilities()
	{
		final Future ret = new Future();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IAAAService as = (IAAAService) result;
				as.getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener<String>(ret)
				{
					public void customResultAvailable(String result)
					{
						as.getSecurityRoles(result).addResultListener(ia.createResultListener(new DelegationResultListener<Set<String>>(ret)
						{
							public void customResultAvailable(Set<String> result)
							{
								as.getCapabilities(result).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
							}
						}));
					}
				}));
			}
		}));
		return ret;
	}
	
	/**
	 *  Starts a new process
	 *  @param client The client.
	 *  @param rid The resource identifier.
	 *  @param path The path.
	 */
	public IFuture<IComponentIdentifier> startProcess(final ProcessResourceInfo info)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IExecutionService.class)).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IExecutionService, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(IExecutionService result)
					{
						result.startProcess(info, null, null).addResultListener(ia.createResultListener(new DelegationResultListener<IComponentIdentifier>(ret)));
					}
				}));
				//(ia.createResultListener(new ExceptionDelegationResultListener(ret)
				/*SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IModelRepositoryService) result).getProcessFileName(name).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final String filename = (String) result;
								SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IExecutionService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										((IExecutionService) result).startProcess(filename, null, null).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
									}
								}));
							}
						}));
					}
				}));*/
			}
		});
		
		return ret;
	}
	
	/**
	 *  Gets the names of all available process models
	 *  @param client the client
	 *  @return the names of all available process models
	 */
	public IFuture<List<ProcessResourceInfo>> getModels()
	{
		final Future<List<ProcessResourceInfo>> ret = new Future<List<ProcessResourceInfo>>();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_NAMES)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IModelRepositoryService) result).getModels().addResultListener(ia.createResultListener(new DelegationResultListener<List<ProcessResourceInfo>>(ret)));
					}
				}));
			}
		});
		return ret;
	}
	
	/**
	 *  Finishes an Activity.
	 *  @param activity the activity being finished
	 */
	public IFuture<Void> finishActivity(final IClientActivity activity)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.COMMIT_WORKITEM)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IAAAService) result).getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final String username = (String) result;
								SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
										wh.finishActivity(username , activity);
										ret.setResult(null);
									}
								}));
							}
						}));
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Begins an activity for a client.
	 *  @param workitem the workitem being requested for the activity
	 */
	public IFuture<Void> beginActivity(final IWorkitem workitem)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ACQUIRE_WORKITEM)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IAAAService) result).getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final String username = (String) result;
								SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
										wh.beginActivity(username, workitem);
										ret.setResult(null);
									}
								}));
							}
						}));
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Cancel an activity.
	 *  @param activity the activity being canceled
	 */
	public IFuture<Void> cancelActivity(final IClientActivity activity)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.RELEASE_WORKITEM)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IAAAService) result).getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final String username = (String) result;
								SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
										wh.cancelActivity(username, activity);
										ret.setResult(null);
									}
								}));
							}
						}));
						
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns all workitems available to a client.
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public IFuture<Set<IWorkitem>> getAvailableWorkitems()
	{
		final Future<Set<IWorkitem>> ret = new Future<Set<IWorkitem>>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.REQUEST_AVAILABLE_WORKITEMS)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IAAAService) result).getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final String username = (String) result;
								SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
										wh.getAvailableWorkitems(username).addResultListener(ia.createResultListener(new DelegationResultListener<Set<IWorkitem>>(ret)));
									};
								}));
							}							
						}));
					}
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Returns all activities available to a client.
	 *  @return a set of activities that are available for this client
	 */
	public IFuture<Set<IClientActivity>> getAvailableActivities()
	{
		final Future<Set<IClientActivity>> ret = new Future<Set<IClientActivity>>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.REQUEST_AVAILABLE_ACTIVITIES)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IAAAService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						((IAAAService) result).getUserName(client).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final String username = (String) result;
								SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
										wh.getAvailableActivities(username).addResultListener(ia.createResultListener(new DelegationResultListener<Set<IClientActivity>>(ret)));
									};
								}));
							}
						}));
					}
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture<Void> addWorkitemListener(final IWorkitemListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADD_WORKITEM_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.addWorkitemListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture<Void> removeWorkitemListener(final IWorkitemListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.REMOVE_WORKITEM_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.removeWorkitemListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture<Void> addActivityListener(final IActivityListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADD_ACTIVITY_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.addActivityListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture<Void> removeActivityListener(final IActivityListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.REMOVE_ACTIVITY_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.removeActivityListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 * Adds a process model resource to the repository
	 * @param resource The process resource.
	 */
	public IFuture<Void> addProcessResource(final ProcessResource resource)
	{
		final Future<Void> ret = new Future<Void>();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.PD_ADD_PROCESS_MODEL)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.addProcessResource(resource).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					}
				}));
			}
		});
		return ret;
	}
	
	/**
	 * Removes a process model resource from the repository
	 * @param info The process resource information.
	 */
	public IFuture<Void> removeProcessResource(final ProcessResourceInfo info)
	{
		final Future ret = new Future();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.PD_REMOVE_PROCESS_MODEL)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.removeProcessResource(info).addResultListener(new DelegationResultListener<Void>(ret));
					}
				}));
			}
		});
		return ret;
	}
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	/*public IFuture getProcessModel(IComponentIdentifier client, final String name)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_PROCESS_MODEL)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.getProcessModel(name).addResultListener(ia.createResultListener(new DelegationResultListener(future)));
					};
				}));
			}
		});
		
		return ret;
	}*/
	
	/**
	 * Gets a process model information not listed in the model repository.
	 * @param info Process resource information
	 * @return The model info.
	 */
	public IFuture<IModelInfo> getProcessModelInfo(final ProcessResourceInfo info)
	{
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_PROCESS_MODEL)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IModelRepositoryService, IModelInfo>(ret)
				{
					public void customResultAvailable(IModelRepositoryService result)
					{
						result.getProcessModelInfo(info);
						//IExecutionService es = (IExecutionService) result;
						//es.loadModel(path, imports, rid).addResultListener(ia.createResultListener(new DelegationResultListener(future)));
					};
				}));
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
	/*public IFuture getProcessModelNames(IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_NAMES)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IModelRepositoryService, IModelInfo>(ret)
				{
					public void customResultAvailable(IModelRepositoryService result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.getModelNames().addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
					};
				}));
			}
		});
		
		return ret;
	}*/
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	/*public IFuture getLoadableModelPaths(IComponentIdentifier client)
	{
		final Future ret = new Future();
		(new AccessControlCheck(client, IAAAService.PD_REQUEST_MODEL_PATHS)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.getLoadableModels().addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
					};
				}));
			}
		});
		
		return ret;
	}*/
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessRepositoryListener(final IProcessRepositoryListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.PD_ADD_REPOSITORY_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.addProcessRepositoryListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
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
	public IFuture<Void> removeProcessRepositoryListener(final IProcessRepositoryListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.PD_REMOVE_REPOSITORY_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IModelRepositoryService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelRepositoryService mr = (IModelRepositoryService) result;
						mr.removeProcessRepositoryListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
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
	public IFuture<Map<String, Set<IClientActivity>>> getUserActivities()
	{
		final Future<Map<String, Set<IClientActivity>>> ret = new Future<Map<String, Set<IClientActivity>>>();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADMIN_REQUEST_ALL_ACTIVITIES)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.getUserActivities().addResultListener(ia.createResultListener(new DelegationResultListener<Map<String, Set<IClientActivity>>>(ret)));
					};
				}));
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
	public IFuture<Void> terminateActivity(final IClientActivity activity)
	{
		final Future<Void> ret = new Future<Void>();
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADMIN_TERMINATE_ACTIVITY)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.terminateActivity(activity).addResultListener(ia.createResultListener(new DelegationResultListener<Void>(ret)));
					};
				}));
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
	public IFuture<Void> addActivitiesListener(final IActivityListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADMIN_ADD_ACTIVITIES_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.addGlobalActivityListener(client, listener).addResultListener(new DelegationResultListener<Void>(ret));
					};
				}));
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
	public IFuture<Void> removeActivitiesListener(final IActivityListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADMIN_REMOVE_ACTIVITIES_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IWorkitemHandlerService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IWorkitemHandlerService wh = (IWorkitemHandlerService) result;
						wh.removeGlobalActivityListener(client, listener).addResultListener(new DelegationResultListener<Void>(ret));
					};
				}));
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
	public IFuture<Void> addLogListener(final ILogListener listener, final boolean pastEvents)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADMIN_ADD_LOG_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( ILogService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ILogService ls = ((ILogService) result);
						ls.addLogListener(client, listener, pastEvents).addResultListener(new DelegationResultListener<Void>(ret));
					}
				}));
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
	public IFuture<Void> removeLogListener(final ILogListener listener)
	{
		final Future ret = new Future();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.ADMIN_REMOVE_LOG_LISTENER)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( ILogService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ILogService ls = ((ILogService) result);
						ls.removeLogListener(client, listener).addResultListener(new DelegationResultListener<Void>(ret));
					}
				}));
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
	public IFuture<Void> addProcessListener(final IProcessListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IExecutionService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExecutionService es = ((IExecutionService) result);
						es.addProcessListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
					}
				}));
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
	public IFuture<Void> removeProcessListener(final IProcessListener listener)
	{
		final Future<Void> ret = new Future<Void>();
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		(new AccessControlCheck(client, IAAAService.START_PROCESS)).checkAccess(ret, ia.getServiceContainer(), new ICommand()
		{
			public void execute(Object args)
			{
				SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IExecutionService.class)).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExecutionService es = ((IExecutionService) result);
						es.removeProcessListener(client, listener).addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
					}
				}));
			}
		});
		
		return ret;
	}
}
