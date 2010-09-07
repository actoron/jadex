package jadex.wfms.service.impl;

import jadex.base.SComponentFactory;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.wfms.IProcess;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IExecutionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	
	protected IServiceProvider provider;
	
	/** Running process instances (id -> IProcess) */
	protected Map processes;
	
	/** Counter for instances of processes (ILoadableComponentModel -> num)*/
	protected Map instancecnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new execution service.
	 */
	public ExecutionService(IComponentIdentifier wfms, IServiceProvider provider)
	{
		super(provider.getId(), IExecutionService.class, null);
		//super(BasicService.createServiceIdentifier(provider.getId(), ExecutionService.class));

		this.processes = new HashMap();
		this.wfms = wfms;
		this.provider = provider;
		
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
		return SComponentFactory.loadModel(provider, filename);
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
		IComponentManagementService ces = (IComponentManagementService) SServiceProvider.getService(provider, IComponentManagementService.class).get(new ThreadSuspendable());
		ces.createComponent(null, modelname, new CreationInfo(null, arguments, wfms, true, false), null).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentIdentifier id = ((IComponentIdentifier) result);
				SServiceProvider.getService(provider, IComponentManagementService.class).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						final IComponentManagementService cms = (IComponentManagementService) result;
						cms.addComponentListener(id, new IComponentListener()
						{
							private List currentActivities = new ArrayList();
							
							private List activityHistory = new ArrayList();
							
							public void componentRemoved(IComponentDescription desc, Map results)
							{
								Logger.getLogger("Wfms").log(Level.INFO, "Finished process " + id.toString());
								Logger.getLogger("Wfms").log(Level.INFO, "History: " + Arrays.toString(activityHistory.toArray()));
								((AdministrationService) SServiceProvider.getService(provider,IAdministrationService.class).get(new ThreadSuspendable())).fireProcessFinished(id);
							}
							
							public void componentChanged(IComponentDescription desc)
							{
								//System.out.println(desc.getName() + " " + desc.getState() + desc.getProcessingState() + " " + desc.getType());
								if ("BPMN Process".equals(desc.getType()))
								{
									cms.getExternalAccess(id).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
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
							}
							
							public void componentAdded(IComponentDescription desc)
							{
							}
						});
						
						cms.resumeComponent(id);
					}
				});
						
				
				ret.setResult(id);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
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
		return SComponentFactory.isLoadable(provider, name);
	}
	
	/**
	 *  Generate a process id.
	 */
	/*protected Object generateId(String modelname)
	{
		modelname = modelname.substring(modelname.lastIndexOf('/') + 1);
		Integer ret = new Integer(0);
		if(instancecnt==null)
		{
			instancecnt = new HashMap();
		}
		else
		{
			Integer modInt = (Integer) instancecnt.get(modelname);
			if (modInt != null)
				ret = new Integer(modInt.intValue()+1);
		}
		
		instancecnt.put(modelname, ret);
		
		return modelname+"_"+ret;
	}*/
}
