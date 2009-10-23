package jadex.microkernel;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IPlatform;
import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;
import jadex.service.library.ILibraryService;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for creating micro agents.
 */
public class MicroAgentFactory implements IComponentFactory
{
	//-------- constants --------
	
	/** The micro agent file type. */
	public static final String	FILETYPE_MICROAGENT	= "Micro Agent";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"micro_agent",	SGUI.makeIcon(MicroAgentFactory.class, "/jadex/microkernel/images/micro_agent.png"),
	});

	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform platform;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public MicroAgentFactory(IPlatform platform)
	{
		this.platform = platform;
	}
	
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
		if(listener!=null)
			listener.resultAvailable(null);
	}
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Create a kernel agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a kernel agent.
	 */
	public IComponentInstance	createComponentInstance(IAgentAdapter adapter, String model, String config, Map arguments)
	{
		MicroAgentModel lm = (MicroAgentModel)loadModel(model);
		return new MicroAgentInterpreter(adapter, lm, arguments, config);
	}
	
	/**
	 *  Load an agent model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String model)
	{
//		System.out.println("loading micro: "+model);
		ILoadableComponentModel ret = null;
		ILibraryService libservice = (ILibraryService)platform.getService(ILibraryService.class);
		
		String clname = model;
		
		// Hack! for extracting clear classname
		if(clname.endsWith(".class"))
			clname = model.substring(0, model.indexOf(".class"));
		clname = clname.replace('\\', '.');
		clname = clname.replace('/', '.');
		
		ClassLoader	cl	= libservice.getClassLoader();
		Class cma = SReflect.findClass0(clname, null, cl);
//		System.out.println(clname+" "+cma+" "+ret);
		int idx;
		while(cma==null && (idx=clname.indexOf('.'))!=-1)
		{
			clname	= clname.substring(idx+1);
			cma = SReflect.findClass0(clname, null, cl);
//			System.out.println(clname+" "+cma+" "+ret);
		}
		if(cma==null)// || !cma.isAssignableFrom(IMicroAgent.class))
			throw new RuntimeException("No micro agent file: "+model);
		ret = new MicroAgentModel(cma, model, cl);
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public boolean	isLoadable(String model)
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
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model)
	{
		return isLoadable(model);
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes()
	{
		return new String[]{FILETYPE_MICROAGENT};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		return type.equals(FILETYPE_MICROAGENT) ? icons.getIcon("micro_agent") : null;
	}

	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		return model.toLowerCase().endsWith("agent.class") ? FILETYPE_MICROAGENT
			: null;
	}
	
	/**
	 *  Get the element type.
	 *  @return The element type (e.g. an agent, application or process).
	 * /
	public String getElementType()
	{
		return IComponentFactory.ELEMENT_TYPE_AGENT;
	}*/
}
