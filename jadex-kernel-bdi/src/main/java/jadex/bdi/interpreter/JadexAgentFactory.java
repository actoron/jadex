package jadex.bdi.interpreter;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IJadexAgent;
import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.bridge.ILibraryService;
import jadex.bridge.ILibraryServiceListener;
import jadex.bridge.IPlatform;
import jadex.rules.state.IOAVState;
import jadex.rules.state.javaimpl.OAVState;

import java.io.IOException;
import java.util.Map;

/**
 *  Factory for creating Jadex V2 BDI agents.
 */
public class JadexAgentFactory implements IJadexAgentFactory
{
	//-------- constants --------
	
	/** Loader for XML agent models. */
//	public static final OAVBDIModelLoader	LOADER	= new OAVBDIModelLoader();

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
	public JadexAgentFactory(Map props, IPlatform platform)
	{
//		this.kernelprops = kernelprops;
		this.props = props;
		this.loader	= new OAVBDIModelLoader(props);
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
			ILibraryServiceListener lsl = new ILibraryServiceListener()
			{
				public void jarAdded(String path)
				{
					loader.setClassLoader(libservice.getClassLoader());
				}
				
				public void jarRemoved(String path)
				{
					loader.setClassLoader(libservice.getClassLoader());
				}
				
				public void pathAdded(String path)
				{
					loader.setClassLoader(libservice.getClassLoader()); 
				}
				
				public void pathRemoved(String path)
				{
					loader.setClassLoader(libservice.getClassLoader());
				}
			};
			libservice.addLibraryServiceListener(lsl);
		}
	}
	
	//-------- IJadexAgentFactory interface --------
	
	/**
	 *  Create a Jadex agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a jadex agent.
	 */
	public IJadexAgent	createJadexAgent(IAgentAdapter adapter, String model, String config, Map arguments)
	{
		init();
		
		OAVAgentModel loaded  = (OAVAgentModel)loadModel(model);
		IOAVState	state	= new OAVState(OAVBDIRuntimeModel.bdi_rt_model); 
		state.addSubstate(loaded.getState());
		return new BDIInterpreter(adapter, state, loaded, config, arguments, props);
	}
	
	/**
	 *  Load a Jadex model.
	 *  @param filename The filename.
	 */
	public IJadexModel loadModel(String filename)
	{
		init();
		
		try
		{
//			System.out.println("loading: "+filename);
			OAVCapabilityModel loaded = loader.loadModel(filename, null, null);
			return loaded;
		}
		catch(IOException e)
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
	public boolean	isLoadable(String model)
	{
		return model.toLowerCase().endsWith(".agent.xml") || model.toLowerCase().endsWith(".capability.xml");
		
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
}
