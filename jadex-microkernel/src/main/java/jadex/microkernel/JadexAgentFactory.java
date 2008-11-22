package jadex.microkernel;

import jadex.bridge.IAgentAdapter;
import jadex.bridge.IJadexAgent;
import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.bridge.ILibraryService;
import jadex.bridge.IPlatform;
import jadex.commons.SReflect;

import java.util.Map;

/**
 *  Factory for creating Jadex V2 BDI agents.
 */
public class JadexAgentFactory implements IJadexAgentFactory
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform platform;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public JadexAgentFactory(IPlatform platform)
	{
		this.platform = platform;
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
		MicroAgentModel lm = (MicroAgentModel)loadModel(model);
		return new MicroAgentInterpreter(adapter, lm);
	}
	
	/**
	 *  Load a Jadex model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public IJadexModel loadModel(String model)
	{
		System.out.println("loading micro: "+model);
		IJadexModel ret = null;
		ILibraryService libservice = (ILibraryService)platform.getService(ILibraryService.class);
		String clname = model.substring(0, model.indexOf(".class"));
		
		// Hack!
		clname = clname.substring(model.indexOf("classes")+8);
		clname = clname.replace("\\", ".");
		
		Class cma = SReflect.findClass0(clname, null, libservice.getClassLoader());
		System.out.println(clname+" "+cma+" "+ret);
		if(cma==null)// || !cma.isAssignableFrom(IMicroAgent.class))
			throw new RuntimeException("No micro agent file: "+model);
		ret = new MicroAgentModel(cma, model);
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
}
