package jadex.bpmn;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Foctory for loading bpmn processes.
 */
public class BpmnFactory implements IComponentFactory
{
	//-------- constants --------
	
	/** The micro agent file type. */
	public static final String	FILETYPE_BPMNPROCESS = "BPMN Process";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"bpmn_process",	SGUI.makeIcon(BpmnFactory.class, "/jadex/bpmn/images/bpmn_process.png"),
	});
	
	//-------- attributes --------
	
	/** The WFMS */
	protected IServiceContainer container;
	
	/** Running process instances */
	protected Map processes;
	
	/** The model loader */
	protected BpmnModelLoader loader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BpmnProcessService.
	 */
	public BpmnFactory(IServiceContainer container)
	{
		this.container = container;
		this.processes = new HashMap();
		this.loader = new BpmnModelLoader();
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		final ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
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
	public ILoadableComponentModel loadModel(String filename)
	{
		MBpmnModel ret = null;
//		System.out.println("filename: "+filename);
		try
		{
			ret = loader.loadBpmnModel(filename, null);
			ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
			ClassLoader	cl = libservice.getClassLoader();
			ret.setClassloader(cl);
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
	 * /
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
						synchronized (BpmnFactory.this)
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
	}*/
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param modelname The model name.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String modelname)
	{
		return modelname.endsWith(".bpmn");
	}

	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model)
	{
		return model.endsWith(".bpmn");
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes()
	{
		return new String[]{FILETYPE_BPMNPROCESS};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		return type.equals(FILETYPE_BPMNPROCESS)? icons.getIcon("bpmn_process") : null;
	}

	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		return model.toLowerCase().endsWith(".bpmn") ? FILETYPE_BPMNPROCESS: null;
	}
	
	/**
	 * Create a kernel agent.
	 * @param model The agent model file (i.e. the name of the XML file).
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @return An instance of a kernel agent.
	 */
	public IComponentInstance createComponentInstance(IComponentAdapter adapter, ILoadableComponentModel model, String config, Map arguments)
	{
		return new BpmnInterpreter(adapter, (MBpmnModel)model, arguments, config, null, null);
	}

}
