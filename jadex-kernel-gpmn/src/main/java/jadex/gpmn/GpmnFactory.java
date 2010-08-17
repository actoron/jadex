package jadex.gpmn;

import jadex.bdi.BDIAgentFactory;
import jadex.bdi.model.OAVAgentModel;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.Future;
import jadex.commons.ResourceInfo;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.BasicService;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for loading gpmn processes. 
 */
public class GpmnFactory extends BasicService implements IComponentFactory
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
	
	/** The provider. */
	protected IServiceProvider provider;

	/** The gpmn loader. */
	protected GpmnModelLoader loader;
	
	/** The legacy gpmn bdiagent converter. */
	protected GpmnBDIConverter legacyconverter;

	/** The gpmn 2 bdiagent converter. */
	protected GpmnBDIConverter2 converter;
	
	/** The bdi agent factory. */
	protected BDIAgentFactory factory;
	
	/** The properties. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new GpmnProcessService.
	 */
	public GpmnFactory(IServiceContainer provider, Map properties)
	{
		super(provider.getId(), IComponentFactory.class, null);
		
		this.properties	= properties;
		this.provider = provider;
		this.loader = new GpmnModelLoader();
		this.legacyconverter = new GpmnBDIConverter();
		this.converter = new GpmnBDIConverter2();
		
		SServiceProvider.getService(provider, new ComponentFactorySelector(BDIAgentFactory.FILETYPE_BDIAGENT)).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				factory = (BDIAgentFactory)result;
				if(factory == null)
					throw new RuntimeException("No bdi agent factory found.");
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return super.startService();
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 * /
	public synchronized IFuture	shutdownService()
	{
		return super.shutdownService();
	}*/
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IModelInfo loadModel(String model, String[] imports, ClassLoader classloader)
	{
		// Todo: support imports for GPMN models (-> use abstract model loader). 
		IModelInfo ret = null;
		try
		{
//			ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
//			ClassLoader	cl = libservice.getClassLoader();
			ResourceInfo rinfo = SUtil.getResourceInfo0(model, classloader);
			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
			br.readLine();
			if(br.readLine().contains("version=\"2.0\""))
			{
				ret = GpmnXMLReader2.read(model, classloader, null).getModelInfo();
			}
			else
			{
				ret = GpmnXMLReader.read(model, classloader, null).getModelInfo();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		return model.endsWith(".gpmn");
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model, String[] imports, ClassLoader classloader)
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
	public String getComponentType(String model, String[] imports, ClassLoader classloader)
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
	public Object[] createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, 
		IModelInfo modelinfo, String config, Map arguments, IExternalAccess parent, Future inited)
	{
//		ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
		
		try
		{
			Object ret = null;
			ResourceInfo rinfo = SUtil.getResourceInfo0(modelinfo.getFilename(), modelinfo.getClassLoader());
			BufferedReader br = new BufferedReader(new InputStreamReader(rinfo.getInputStream()));
			br.readLine();
			if(br.readLine().contains("version=\"2.0\""))
			{
				ret = GpmnXMLReader2.read(modelinfo.getFilename(), modelinfo.getClassLoader(), null);
			}
			else
			{
				ret = GpmnXMLReader.read(modelinfo.getFilename(), modelinfo.getClassLoader(), null);
			}
				
			if(ret instanceof jadex.gpmn.model2.MGpmnModel)
				ret = converter.convertGpmnModelToBDIAgents((jadex.gpmn.model2.MGpmnModel)ret, modelinfo.getClassLoader());
			else
				ret = legacyconverter.convertGpmnModelToBDIAgents((jadex.gpmn.model.MGpmnModel)ret, modelinfo.getClassLoader());
	
			return this.factory.createComponentInstance(desc, factory, (OAVAgentModel)ret, config, arguments, parent, inited);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
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
