package jadex.wfms.service.impl;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IAdministrationService;
import jadex.wfms.service.IWfmsClientService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class BpmnProcessService implements IExecutionService, IService
{
	//-------- attributes --------
	
	/** The WFMS */
	protected IServiceContainer wfms;
	
	/** Running process instances */
	protected Map processes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BpmnProcessService.
	 */
	public BpmnProcessService(IServiceContainer wfms)
	{
		this.wfms = wfms;
		this.processes = new HashMap();
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
	
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @return The process model.
	 */
	public ILoadableComponentModel loadModel(String filename, String[] imports)
	{
		ILoadableComponentModel ret = null;
		IComponentFactory factory = (IComponentFactory) wfms.getService(IComponentFactory.class, "bpmn_factory");
		ILibraryService ls = (ILibraryService) wfms.getService(ILibraryService.class);
		try
		{
			ret = factory.loadModel(ls.getClassLoader().getResource(filename).getPath());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 * Starts a BPMN process
	 * @param name name of the BPMN model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 */
	public Object startProcess(String modelname, final Object id, Map arguments, boolean stepmode)
	{
		try
		{
			IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
//			String path = mr.getProcessModelPath(modelname);
			IComponentFactory factory = (IComponentFactory) wfms.getService(IComponentFactory.class, "bpmn_factory");
			final MBpmnModel model = (MBpmnModel) factory.loadModel(modelname);
			
			Logger.getLogger("Wfms").log(Level.INFO, "Starting BPMN process " + id.toString());
			//final BpmnInterpreter instance = new BpmnInterpreter(adapter, model, arguments, config, handlers, fetcher);
			final IComponentExecutionService ces = (IComponentExecutionService)wfms.getService(IComponentExecutionService.class);
			//instance.setWfms(wfms);
			//BpmnExecutor executor = new BpmnExecutor(instance, true);
			ces.createComponent(String.valueOf(id), modelname, null, arguments, true, new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					processes.put(id, result);
					ces.addComponentListener((IComponentIdentifier) result, new IComponentListener() 
					{
						public void componentRemoved(IComponentDescription desc, Map results)
						{
							synchronized (BpmnProcessService.this)
							{
								processes.remove(id);
								
								Logger.getLogger("Wfms").log(Level.INFO, "Finished BPMN process " + id.toString());
								((AdministrationService) wfms.getService(IAdministrationService.class)).fireProcessFinished(id.toString());
							}
						}
						
						public void componentChanged(IComponentDescription desc)
						{
						}
						
						public void componentAdded(IComponentDescription desc)
						{
						}
					});
					ces.resumeComponent((IComponentIdentifier) result, null);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					Logger.getLogger("Wfms").log(Level.SEVERE, "Failed to start model: " + model.getFilename());
				}
			}, null, null, false);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return id;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param modelname The model name.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String modelname)
	{
		return modelname.endsWith(".bpmn");
	}
}
