package jadex.gpmn;

import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.OAVAgentModel;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.gpmn.model.MGpmnModel;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for loading gpmn processes. 
 */
public class GpmnFactory implements IComponentFactory, IService
{
	//-------- constants --------
	
	/** The gpmn process file type. */
	public static final String	FILETYPE_GPMNPROCESS = "GPMN Process";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"gpmn_process",	SGUI.makeIcon(GpmnFactory.class, "/jadex/gpmn/images/gpmn_process.png"),
	});
	
	//-------- attributes --------
	
	/** The platform */
	protected IServiceContainer container;

	/** The gpmn loader. */
	protected GpmnModelLoader loader;

	/** The gpmn 2 bdiagent converter. */
	protected GpmnBDIConverter converter;
	
	/** The bdi agent factory. */
	protected BDIAgentFactory factory;
	
	/** The properties. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnFactory(IServiceContainer container, Map properties)
	{
		this.properties	= properties;
		this.container = container;
		this.loader = new GpmnModelLoader();
		this.converter = new GpmnBDIConverter();
		
		for(Iterator it=container.getServices(IComponentFactory.class).iterator(); 
			it.hasNext() && factory==null; )
		{
			IComponentFactory tmp = (IComponentFactory)it.next();
			if(tmp instanceof BDIAgentFactory)
				this.factory = (BDIAgentFactory)tmp;
		}
		if(factory == null)
			throw new RuntimeException("No bdi agent factory found.");
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
//		// Absolute start time (for testing and benchmarking).
//		long starttime = System.currentTimeMillis();
//		
//		// Initialize platform configuration from args.
//		String conffile = "jadex/bdibpmn/standalone_bpmn_conf.xml";
//		// Create an instance of the platform.
//		// Hack as long as no loader is present.
//		String[] args = new String[0];
//		if(args.length>0 && args[0].equals("-"+Platform.CONFIGURATION))
//		{
//			conffile = args[1];
//			String[] tmp= new String[args.length-2];
//			System.arraycopy(args, 2, tmp, 0, args.length-2);
//			args = tmp;
//		}
//		ClassLoader cl = Platform.class.getClassLoader();
////		Properties configuration = XMLPropertiesReader.readProperties(SUtil.getResource(conffile, cl), cl);
//		Properties configuration = null;
//		try
//		{
//			configuration = (Properties)PropertiesXMLHelper.getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
//		} 
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		System.out.println(configuration);
//		
//		container = new Platform(configuration, container);
//		((Platform)container).start();
//		
//		long startup = System.currentTimeMillis() - starttime;
//		((Platform)container).getLogger().info("Platform startup time: " + startup + " ms.");
		
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
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String model, String[] imports)
	{
		// Todo: support imports for GPMN models (-> use abstract model loader). 
		MGpmnModel ret = null;
		try
		{
			ret = GpmnXMLReader.read(model, ((ILibraryService)container.getService(ILibraryService.class)).getClassLoader(), null);
			ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
			ClassLoader	cl = libservice.getClassLoader();
			ret.setClassloader(cl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 * Starts a Gpmn process
	 * @param name name of the Gpmn model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 * /
	public Object startProcess(String modelname, final Object id, Map arguments, boolean stepmode)
	{
		final String name = id.toString();
		final IComponentManagementService ces = (IComponentManagementService)container.getService(IComponentManagementService.class);
		ces.createComponent(name, modelname, null, null, new IResultListener()
		{
			
			public void resultAvailable(Object result)
			{
				ces.startComponent((IComponentIdentifier)result, null);
				processes.put(id, result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}, null);
		
		return id;
	}*/
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model, String[] imports)
	{
		return model.endsWith(".gpmn");
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model, String[] imports)
	{
		return model.endsWith(".gpmn");
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_GPMNPROCESS};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getComponentTypeIcon(String type)
	{
		return type.equals(FILETYPE_GPMNPROCESS) ? icons.getIcon("gpmn_process") : null;
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public String getComponentType(String model, String[] imports)
	{
		return model.toLowerCase().endsWith(".gpmn") ? FILETYPE_GPMNPROCESS: null;
	}
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public IComponentInstance createComponentInstance(IComponentAdapter adapter, ILoadableComponentModel model, String config, Map arguments, IExternalAccess parent)
	{
		ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
		
		MGpmnModel gmodel = (MGpmnModel)model;
		OAVAgentModel amodel	= converter.convertGpmnModelToBDIAgents(gmodel, libservice.getClassLoader());

		return factory.createComponentInstance(adapter, amodel, config, arguments, parent);
		
//		try
//		{
//			FileOutputStream os = new FileOutputStream("wurst.xml");
//			Writer writer = OAVBDIXMLReader.getWriter();
//			writer.write(agent.getState().getRootObjects().next(), os, libservice.getClassLoader(), agent.getState());
//			os.close();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type)
	{
		return FILETYPE_GPMNPROCESS.equals(type)
		? properties : null;
	}
}
