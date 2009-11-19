package jadex.wfms.service.impl;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IResultListener;
import jadex.wfms.IProcessModel;
import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IWfmsClientService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 
 */
public class BpmnProcessService implements IExecutionService
{
	//-------- attributes --------
	
	/** The WFMS */
	protected IWfms wfms;
	
	/** Running process instances */
	protected Map processes;
	
	/** The model loader */
	protected BpmnModelLoader loader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BpmnProcessService.
	 */
	public BpmnProcessService(IWfms wfms)
	{
		this.wfms = wfms;
		this.processes = new HashMap();
		this.loader = new BpmnModelLoader();
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
	}
	
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @return The process model.
	 */
	public IProcessModel loadModel(String filename, String[] imports)
	{
		IProcessModel ret = null;
		
		try
		{
			ret = (IProcessModel) loader.loadBpmnModel(filename, imports);
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
			final MBpmnModel model = loader.loadBpmnModel(modelname, mr.getImports());
			
			wfms.getLogger().log(Level.INFO, "Starting BPMN process " + id.toString());
			//final BpmnInterpreter instance = new BpmnInterpreter(adapter, model, arguments, config, handlers, fetcher);
			final IComponentExecutionService ces = (IComponentExecutionService)wfms.getService(IComponentExecutionService.class);
			//instance.setWfms(wfms);
			//BpmnExecutor executor = new BpmnExecutor(instance, true);
			ces.createComponent(String.valueOf(id), modelname, null, arguments, true, new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					processes.put(id, result);
					ces.addComponentListener((IComponentIdentifier) result, new IComponentListener() {
						
						public void componentRemoved(IComponentDescription desc)
						{
							synchronized (BpmnProcessService.this)
							{
								processes.remove(id);
								
								wfms.getLogger().log(Level.INFO, "Finished BPMN process " + id.toString());
								((IWfmsClientService) wfms.getService(IWfmsClientService.class)).fireProcessFinished(id.toString());
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
				
				public void exceptionOccurred(Exception exception)
				{
					wfms.getLogger().log(Level.SEVERE, "Failed to start model: " + model.getFilename());
				}
			}, null);
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
