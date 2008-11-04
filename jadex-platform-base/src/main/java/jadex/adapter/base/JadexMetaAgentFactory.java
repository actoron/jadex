package jadex.adapter.base;

import jadex.bridge.AgentCreationException;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IJadexAgent;
import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;

import java.util.List;
import java.util.Map;

/**
 *  Standard meta agent factory. Uses several sub
 *  factories and uses them according to their order
 *  and isLoadable() method.
 */
public class JadexMetaAgentFactory implements IJadexAgentFactory
{
	//-------- attributes --------
	
	/** The sub agent factories. */
	protected List factories;
	
	//-------- constructors --------
	
	/**
	 *  Create a new factory.
	 */
	public JadexMetaAgentFactory(List factories)
	{
		if(factories==null || factories.size()==0)
			throw new RuntimeException("Meta factory needs at least one sub factory.");
		this.factories = factories;
	}
	
	//-------- IJadexAgentFactory methods --------
	
	/**
	 *  Create a Jadex agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a jadex agent.
	 */
	public IJadexAgent createJadexAgent(IAgentAdapter adapter, String model, String config, Map arguments)
	{
		IJadexAgent ret = null;
		
		for(int i=0; ret==null && i<factories.size(); i++)
		{
			IJadexAgentFactory fac = (IJadexAgentFactory)factories.get(i);
			if(fac.isLoadable(model))
			{
				ret = fac.createJadexAgent(adapter, model, config, arguments);
			}
		}
		
		if(ret==null)
			throw new AgentCreationException(""+model, null);
		return ret;
	}
	
	

	/**
	 *  Load a Jadex model.
	 *  @param filename The filename.
	 */
	public IJadexModel loadModel(String filename)
	{
		IJadexModel ret = null;
		
		for(int i=0; ret==null && i<factories.size(); i++)
		{
			IJadexAgentFactory fac = (IJadexAgentFactory)factories.get(i);
			if(fac.isLoadable(filename))
			{
				ret = fac.loadModel(filename);
			}
		}
		
		if(ret==null)
			throw new RuntimeException("Could not load: "+filename);
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model)
	{
		boolean ret = false;
		
		for(int i=0; !ret && i<factories.size(); i++)
		{
			IJadexAgentFactory fac = (IJadexAgentFactory)factories.get(i);
			ret = fac.isLoadable(model);
		}
		
		return ret;
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and should therefore also be loadable).
	 */
	public boolean isStartable(String model)
	{
		boolean ret = false;
		
		for(int i=0; !ret && i<factories.size(); i++)
		{
			IJadexAgentFactory fac = (IJadexAgentFactory)factories.get(i);
			ret = fac.isStartable(model);
		}
		
		return ret;
	}
}
