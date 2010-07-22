package jadex.bdi;

import jadex.bdi.model.OAVAgentModel;
import jadex.bdi.model.OAVCapabilityModel;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SGUI;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.service.BasicService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for creating Jadex V2 BDI agents.
 */
public class BDIAgentFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The BDI agent file type. */
	public static final String	FILETYPE_BDIAGENT	= "BDI Agent";
	
	/** The BDI capability file type. */
	public static final String	FILETYPE_BDICAPABILITY	= "BDI Capability";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"bdi_agent",	SGUI.makeIcon(BDIAgentFactory.class, "/jadex/bdi/images/bdi_agent.png"),
		"bdi_capability",	SGUI.makeIcon(BDIAgentFactory.class, "/jadex/bdi/images/bdi_capability.png")
	});

	//-------- attributes --------
	
	/** The factory properties. */
	protected Map props;
	
	/** The model loader. */
	protected OAVBDIModelLoader loader;
	
	/** The platform. */
	protected IServiceContainer container;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public BDIAgentFactory(Map props, IServiceContainer container)
	{
		this.props = props;
		this.loader	= new OAVBDIModelLoader();
		this.container = container;
	}
	
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
	
	//-------- IAgentFactory interface --------
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public Object[] createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, ILoadableComponentModel model, String config, Map arguments, IExternalAccess parent)
	{
//		init();
		
		OAVAgentModel amodel = (OAVAgentModel)model;
		
		// Create type model for agent instance (e.g. holding dynamically loaded java classes).
		OAVTypeModel tmodel	= new OAVTypeModel(desc.getName().getLocalName()+"_typemodel", ((OAVAgentModel)model).getTypeModel().getClassLoader());
//		OAVTypeModel tmodel	= new OAVTypeModel(model.getName()+"_typemodel", ((OAVAgentModel)model).getTypeModel().getClassLoader());
		tmodel.addTypeModel(amodel.getTypeModel());
		tmodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		IOAVState	state	= OAVStateFactory.createOAVState(tmodel); 
		state.addSubstate(amodel.getState());
		
		BDIInterpreter interpreter = new BDIInterpreter(desc, factory, state, amodel, config, arguments, parent, props);
		return new Object[]{interpreter, interpreter.getAgentAdapter()};
	}
	
	/**
	 *  Load a  model.
	 *  @param filename The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String filename, String[] imports, ClassLoader classloader)
	{
//		init();
		
		try
		{
//			System.out.println("loading bdi: "+filename);
			OAVCapabilityModel loaded = (OAVCapabilityModel)loader.loadModel(filename, imports, classloader);
			return loaded;
		}
		catch(Exception e)
		{
			System.err.println(filename);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model, String[] imports, ClassLoader classloader)
	{
//		init();

		return model.toLowerCase().endsWith(".agent.xml") || model.toLowerCase().endsWith(".capability.xml");
//		return loader.isLoadable(model, null);
//		return model.toLowerCase().endsWith(".agent.xml") || model.toLowerCase().endsWith(".capability.xml");
		
//		boolean ret =  model.indexOf("/bdi/")!=-1 || model.indexOf(".bdi.")!=-1 || model.indexOf("\\bdi\\")!=-1 
//			|| model.indexOf("v2")!=-1 || model.indexOf("V2")!=-1;
	
//		System.out.println(model+" "+ret);
		
//		return ret;
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return model!=null && model.toLowerCase().endsWith(".agent.xml");
//		return SXML.isAgentFilename(model);
	}


	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_BDIAGENT, FILETYPE_BDICAPABILITY};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getComponentTypeIcon(String type)
	{
		return type.equals(FILETYPE_BDIAGENT) ? icons.getIcon("bdi_agent")
			: type.equals(FILETYPE_BDICAPABILITY) ? icons.getIcon("bdi_capability") : null;
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public String getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return model.toLowerCase().endsWith(".agent.xml") ? FILETYPE_BDIAGENT
			: model.toLowerCase().endsWith(".capability.xml") ? FILETYPE_BDICAPABILITY
			: null;
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
		return FILETYPE_BDIAGENT.equals(type) || FILETYPE_BDICAPABILITY.equals(type)
			? props : null;
	}
}
