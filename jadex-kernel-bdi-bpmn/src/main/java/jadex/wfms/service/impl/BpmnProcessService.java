package jadex.wfms.service.impl;

import jadex.bpmn.BpmnExecutor;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IResultListener;
import jadex.wfms.IProcessModel;
import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;

import java.util.HashMap;
import java.util.Map;

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
			ret = loader.loadBpmnModel(filename, imports);
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
			MBpmnModel model = loader.loadBpmnModel(modelname, mr.getImports());
			
			final BpmnInstance instance = new BpmnInstance(model);
			instance.setWfms(wfms);
			BpmnExecutor executor = new BpmnExecutor(instance, true);
			
			processes.put(id, executor);
			instance.addChangeListener(new IChangeListener()
			{
				public void changeOccurred(ChangeEvent event)
				{
					if (instance.isFinished(null, null))
					{
						synchronized (BpmnProcessService.this)
						{
							processes.remove(id);
						}
					}
				}
			});
			
			executor.setStepmode(stepmode);
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
