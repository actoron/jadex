package jadex.micro;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for creating micro agents.
 */
public class MicroAgentFactory implements IComponentFactory, IService
{
	//-------- constants --------
	
	/** The micro agent file type. */
	public static final String	FILETYPE_MICROAGENT	= "Micro Agent";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"micro_agent",	SGUI.makeIcon(MicroAgentFactory.class, "/jadex/micro/images/micro_agent.png"),
	});

	//-------- attributes --------
	
	/** The platform. */
	protected IServiceContainer container;
	
	/** The properties. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public MicroAgentFactory(IServiceContainer container, Map properties)
	{
		this.container = container;
		this.properties = properties;
	}
	
	//-------- constructors --------
	
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
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String model, String[] imports)
	{
//		System.out.println("loading micro: "+model);
		ILoadableComponentModel ret = null;
		ILibraryService libservice = (ILibraryService)container.getService(ILibraryService.class);
		
		String clname = model;
		
		// Hack! for extracting clear classname
		if(clname.endsWith(".class"))
			clname = model.substring(0, model.indexOf(".class"));
		clname = clname.replace('\\', '.');
		clname = clname.replace('/', '.');
		
		ClassLoader	cl	= libservice.getClassLoader();
		Class cma = SReflect.findClass0(clname, imports, cl);
//		System.out.println(clname+" "+cma+" "+ret);
		int idx;
		while(cma==null && (idx=clname.indexOf('.'))!=-1)
		{
			clname	= clname.substring(idx+1);
			cma = SReflect.findClass0(clname, imports, cl);
//			System.out.println(clname+" "+cma+" "+ret);
		}
		if(cma==null)// || !cma.isAssignableFrom(IMicroAgent.class))
			throw new RuntimeException("No micro agent file: "+model);
		ret = new MicroAgentModel(cma, model, cl);
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model, String[] imports)
	{
		boolean ret = model.toLowerCase().endsWith("agent.class");
//		if(model.toLowerCase().endsWith("Agent.class"))
//		{
//			ILibraryService libservice = (ILibraryService)platform.getService(ILibraryService.class);
//			String clname = model.substring(0, model.indexOf(".class"));
//			Class cma = SReflect.findClass0(clname, null, libservice.getClassLoader());
//			ret = cma!=null && cma.isAssignableFrom(IMicroAgent.class);
//			System.out.println(clname+" "+cma+" "+ret);
//		}
		return ret;
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model, String[] imports)
	{
		return isLoadable(model, imports);
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_MICROAGENT};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getComponentTypeIcon(String type)
	{
		return type.equals(FILETYPE_MICROAGENT) ? icons.getIcon("micro_agent") : null;
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public String getComponentType(String model, String[] imports)
	{
		return model.toLowerCase().endsWith("agent.class") ? FILETYPE_MICROAGENT
			: null;
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
		return new MicroAgentInterpreter(adapter, (MicroAgentModel)model, arguments, config, parent);
	}
	
	/**
	 *  Get the element type.
	 *  @return The element type (e.g. an agent, application or process).
	 * /
	public String getElementType()
	{
		return IComponentFactory.ELEMENT_TYPE_AGENT;
	}*/
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type)
	{
		return FILETYPE_MICROAGENT.equals(type)
		? properties: null;
	}
}
