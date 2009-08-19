package jadex.bdi.interpreter;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IKernelAgent;
import jadex.bridge.ILibraryService;
import jadex.bridge.ILibraryServiceListener;
import jadex.bridge.ILoadableElementModel;
import jadex.bridge.IPlatform;
import jadex.commons.SGUI;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;

import java.net.URL;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Factory for creating Jadex V2 BDI agents.
 */
public class BDIAgentFactory implements IAgentFactory
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
//	protected Properties kernelprops;
	protected Map props;
	
	/** The model loader. */
	protected OAVBDIModelLoader loader;
	
	/** The platform. */
	protected IPlatform platform;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public BDIAgentFactory(Map props, IPlatform platform)
	{
//		this.kernelprops = kernelprops;
		this.props = props;
		this.loader	= new OAVBDIModelLoader();
		this.platform = platform;
	}
	
	/**
	 *  Init the factory.
	 */
	protected void init()
	{
		if(libservice==null)
		{
			libservice = (ILibraryService)platform.getService(ILibraryService.class);
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
	public IKernelAgent	createKernelAgent(IAgentAdapter adapter, String model, String config, Map arguments)
	{
		init();
		
		OAVAgentModel	loaded 	= (OAVAgentModel)loadModel(model);
		// Create type model for agent instance (e.g. holding dynamically loaded java classes).
		OAVTypeModel	tmodel	= new OAVTypeModel(adapter.getAgentIdentifier().getLocalName()+"_typemodel", loaded.getTypeModel().getClassLoader());
		tmodel.addTypeModel(loaded.getTypeModel());
		tmodel.addTypeModel(OAVBDIRuntimeModel.bdi_rt_model);
		IOAVState	state	= OAVStateFactory.createOAVState(tmodel); 
		state.addSubstate(loaded.getState());
		return new BDIInterpreter(adapter, state, loaded, config, arguments, props);
	}
	
	/**
	 *  Load an agent model.
	 *  @param filename The filename.
	 */
	public ILoadableElementModel loadModel(String filename)
	{
		init();
		
		try
		{
//			System.out.println("loading: "+filename);
			OAVCapabilityModel loaded = (OAVCapabilityModel) loader.loadModel(filename, null);
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
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	// Todo: support imports when loading models.
	public boolean	isLoadable(String model)
	{
		return loader.isLoadable(model, null);
//		return model.toLowerCase().endsWith(".agent.xml") || model.toLowerCase().endsWith(".capability.xml");
		
//		boolean ret =  model.indexOf("/bdi/")!=-1 || model.indexOf(".bdi.")!=-1 || model.indexOf("\\bdi\\")!=-1 
//			|| model.indexOf("v2")!=-1 || model.indexOf("V2")!=-1;
	
//		System.out.println(model+" "+ret);
		
//		return ret;
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model)
	{
		return model!=null && model.toLowerCase().endsWith(".agent.xml");
//		return SXML.isAgentFilename(model);
	}


	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes()
	{
		return new String[]{FILETYPE_BDIAGENT, FILETYPE_BDICAPABILITY};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		return type.equals(FILETYPE_BDIAGENT) ? icons.getIcon("bdi_agent")
			: type.equals(FILETYPE_BDICAPABILITY) ? icons.getIcon("bdi_capability") : null;
	}

	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		return model.toLowerCase().endsWith(".agent.xml") ? FILETYPE_BDIAGENT
			: model.toLowerCase().endsWith(".capability.xml") ? FILETYPE_BDICAPABILITY
			: null;
	}
}
