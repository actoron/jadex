package jadex.wfms.service.impl;

import jadex.base.SComponentFactory;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicService;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.wfms.IProcess;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IExecutionService;

import java.util.Collection;
import java.util.HashMap;
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
		ces.createComponent(null, modelname, new CreationInfo(arguments, wfms), null).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentIdentifier id = ((IComponentIdentifier) result);
				((IComponentManagementService) SServiceProvider.getService(provider, IComponentManagementService.class).get(new ThreadSuspendable())).addComponentListener(id, new IComponentListener()
				{
					
					public void componentRemoved(IComponentDescription desc, Map results)
					{
						Logger.getLogger("Wfms").log(Level.INFO, "Finished process " + id.toString());
						((AdministrationService) SServiceProvider.getService(provider,IAdministrationService.class).get(new ThreadSuspendable())).fireProcessFinished(id);
					}
					
					public void componentChanged(IComponentDescription desc)
					{
					}
					
					public void componentAdded(IComponentDescription desc)
					{
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
