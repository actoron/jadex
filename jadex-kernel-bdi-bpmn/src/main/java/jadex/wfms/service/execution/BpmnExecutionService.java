package jadex.wfms.service.execution;

import jadex.bpmn.BpmnExecutor;
import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;
import jadex.wfms.IProcessModel;
import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.repository.IModelRepositoryService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class BpmnExecutionService implements IExecutionService
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
	public BpmnExecutionService(IWfms wfms)
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
		final ILibraryService libservice = (ILibraryService)wfms.getService(ILibraryService.class);
		loader.setClassLoader(libservice.getClassLoader());
		ILibraryServiceListener lsl = new ILibraryServiceListener()
		{
			public void urlAdded(URL url)
			{
				loader.setClassLoader(libservice.getClassLoader());
			}
			
			public void urlRemoved(URL url)
			{
				loader.setClassLoader(libservice.getClassLoader());
			}
		};
		libservice.addLibraryServiceListener(lsl);
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
						synchronized (BpmnExecutionService.this)
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
