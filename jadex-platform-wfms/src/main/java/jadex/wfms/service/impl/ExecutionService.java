package jadex.wfms.service.impl;

import jadex.base.SComponentFactory;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.CreationInfo;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.wfms.IProcess;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.listeners.IAuthenticationListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.ProcessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  The meta execution service wraps all specific process execution services.
 */
public class ExecutionService extends BasicService implements IExecutionService
{
	//-------- attributes --------
	
	/** The WFMS */
	protected IComponentIdentifier wfms;
	
	protected IExternalAccess exta;
	
	/** Running process instances (id -> IProcess) */
	protected Map processes;
	
	/** Counter for instances of processes (ILoadableComponentModel -> num)*/
	protected Map instancecnt;
	
	/** The process listeners */
	private Map<IComponentIdentifier, Set<IProcessListener>> procListeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new execution service.
	 */
	public ExecutionService(IComponentIdentifier wfms, IExternalAccess exta)
	{
		super(exta.getServiceProvider().getId(), IExecutionService.class, null);
		//super(BasicService.createServiceIdentifier(provider.getId(), ExecutionService.class));

		this.processes = new HashMap();
		this.wfms = wfms;
		this.exta = exta;
		
		//TODO: hack!
		/*this.exeservices = new ArrayList();
		this.exeservices.add(wfms.getService(IBpmnProcessService.class));
		this.exeservices.add(wfms.getService(IGpmnProcessService.class));*/
		
		//if(exeservices==null || exeservices.size()==0)
			//throw new RuntimeException("Meta execution service needs at least one sub service.");
		//this.exeservices = exeservices;
	}
	
	//-------- methods --------
	
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @return The process model.
	 */
	public IFuture loadModel(String filename, String[] imports)
	{
		//ILoadableComponentModel ret = null;
		return SComponentFactory.loadModel(exta, filename);
		/*for(int i=0; ret==null && i<exeservices.size(); i++)
		{
			IExecutionService es = (IExecutionService)exeservices.get(i);
			if(es.isLoadable(filename))
			{
				ret = es.loadModel(filename, imports);
			}
		}
		
		if(ret==null)
			throw new RuntimeException("Could not load process model: "+filename, null);
		return ret;*/
	}
	
	/**
	 *  Start a process instance.
	 */
	public IFuture startProcess(String modelname, Object id, Map arguments)
	{
		final Future ret = new Future();
		IComponentManagementService ces = (IComponentManagementService) SServiceProvider.getService(exta.getServiceProvider(), (Class) IComponentManagementService.class).get(new ThreadSuspendable());
		ces.createComponent(null, modelname, new CreationInfo(null, arguments, wfms, true), null).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IComponentIdentifier id = ((IComponentIdentifier) result);
				SServiceProvider.getService(exta.getServiceProvider(), IComponentManagementService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService) result;
						cms.addComponentListener(id, new ICMSComponentListener()
						{
							private List currentActivities = new ArrayList();
							
							private List activityHistory = new ArrayList();
							
							public IFuture componentRemoved(IComponentDescription desc, Map results)
							{
								Logger.getLogger("Wfms").log(Level.INFO, "Finished process " + id.toString());
								Logger.getLogger("Wfms").log(Level.INFO, "History: " + Arrays.toString(activityHistory.toArray()));
								fireProcessFinished(id);
								return new Future(null);
							}
							
							public IFuture componentChanged(IComponentDescription desc)
							{
								//System.out.println(desc.getName() + " " + desc.getState() + desc.getProcessingState() + " " + desc.getType());
								if ("BPMN Process".equals(desc.getType()))
								{
									cms.getExternalAccess(id).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											final jadex.bpmn.runtime.ExternalAccess ea = (jadex.bpmn.runtime.ExternalAccess) result;
											List newStates = new ArrayList();
											for (Iterator it = ea.getInterpreter().getThreadContext().getAllThreads().iterator(); it.hasNext(); )
											{
												ProcessThread p = (ProcessThread) it.next();
												newStates.add(p.getModelElement());
												//System.out.print(p.getModelElement().get + ", ");
											}
											if (!currentActivities.containsAll(newStates))
											{
												List diff = new ArrayList(newStates);
												diff.removeAll(currentActivities);
												//System.out.println(Arrays.toString(diff.toArray()));
												activityHistory.add(((MActivity) diff.get(0)).getActivityType() + ": " + ((MActivity) diff.get(0)).getName());
												currentActivities = newStates;
											}
											//System.out.println();
										}
									});
								}
								/*else if ("GPMN Process".equals(desc.getType()))
								{
									cms.getExternalAccess(id).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											final IBDIExternalAccess ea = (IBDIExternalAccess) result;
											List newStates = new ArrayList();
											for (Iterator it = ea.getInterpreter().getThreadContext().getAllThreads().iterator(); it.hasNext(); )
											{
												ProcessThread p = (ProcessThread) it.next();
												newStates.add(p.getModelElement());
												//System.out.print(p.getModelElement().get + ", ");
											}
											if (!currentActivities.containsAll(newStates))
											{
												List diff = new ArrayList(newStates);
												diff.removeAll(currentActivities);
												//System.out.println(Arrays.toString(diff.toArray()));
												activityHistory.add(((MActivity) diff.get(0)).getActivityType() + ": " + ((MActivity) diff.get(0)).getName());
												currentActivities = newStates;
											}
											//System.out.println();
										}
									});
								}*/
								return new Future(null);
							}
							
							public IFuture componentAdded(IComponentDescription desc)
							{
								return new Future(null);
							}
						});
						
						cms.resumeComponent(id);
					}
				});
						
				
				ret.setResult(id);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}

	/**
	 *  Stop a process instance.
	 *  @param name The model name.
	 */
	public void stopProcess(IProcess id)
	{
//		IProcess ret = null;
//		
//		for(int i=0; ret==null && i<exeservices.size(); i++)
//		{
//			IExecutionService es = (IExecutionService)exeservices.get(i);
//			if(es.isLoadable(name))
//			{
//				ret = es.startProcess(client, name, arguments, stepmode);
//			}
//		}
//		
//		if(ret==null)
//			throw new RuntimeException("Could not create process: "+name, null);
//		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public IFuture isLoadable(String name)
	{
		return SComponentFactory.isLoadable(exta, name);
	}
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessListener(IComponentIdentifier client, IProcessListener listener)
	{
		Map<IComponentIdentifier, Set<IProcessListener>> processListeners =  getListeners();
		Future ret = new Future();
		synchronized(processListeners)
		{
			Set<IProcessListener> listeners = processListeners.get(client);
			if (listeners == null)
			{
				listeners = new HashSet<IProcessListener>();
				processListeners.put(client, listeners);
			}
			listeners.add(listener);
		}
		ret.setResult(null);
		return ret;
	}
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeProcessListener(IComponentIdentifier client, IProcessListener listener)
	{
		Map<IComponentIdentifier, Set<IProcessListener>> processListeners =  getListeners();
		Future ret = new Future();
		synchronized(processListeners)
		{
			Set<IProcessListener> listeners = processListeners.get(client);
			if (listeners != null)
				listeners.remove(listener);
		}
		ret.setResult(null);
		return ret;
	}
	
	protected Map<IComponentIdentifier, Set<IProcessListener>> getListeners()
	{
		synchronized (this)
		{
			if (procListeners == null)
			{
				procListeners = Collections.synchronizedMap(new HashMap<IComponentIdentifier, Set<IProcessListener>>());
				SServiceProvider.getService(exta.getServiceProvider(), IAAAService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IAAAService as = (IAAAService) result;
						as.addAuthenticationListener(new IAuthenticationListener()
						{
							public void deauthenticated(IComponentIdentifier client, ClientInfo info)
							{
								procListeners.remove(client);
							}
							
							public void authenticated(IComponentIdentifier client, ClientInfo info)
							{
							}
						});
					}
				});
			}
		}
		return procListeners;
	}
	
	protected void fireProcessFinished(Object id)
	{
		Map<IComponentIdentifier, Set<IProcessListener>> processListeners =  getListeners();
		synchronized(processListeners)
		{
			for (Iterator it = processListeners.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry entry = (Map.Entry) it.next();
				Set<IProcessListener> listeners = (Set<IProcessListener>) entry.getValue();
				ProcessEvent evt = new ProcessEvent(String.valueOf(id));
				for (Iterator<IProcessListener> it2 = listeners.iterator(); it2.hasNext(); )
					it2.next().processFinished(evt);
			}
		}
	}
}
