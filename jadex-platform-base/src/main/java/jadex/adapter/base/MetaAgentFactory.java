package jadex.adapter.base;

import jadex.bridge.AgentCreationException;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IKernelAgent;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IAgentModel;
import jadex.commons.SUtil;

import java.util.List;
import java.util.Map;

import javax.swing.Icon;

/**
 *  Standard meta agent factory. Uses several sub
 *  factories and uses them according to their order
 *  and isLoadable() method.
 */
public class MetaAgentFactory implements IAgentFactory
{
	//-------- attributes --------
	
	/** The sub agent factories. */
	protected List factories;
	
	//-------- constructors --------
	
	/**
	 *  Create a new factory.
	 */
	public MetaAgentFactory(List factories)
	{
		if(factories==null || factories.size()==0)
			throw new RuntimeException("Meta factory needs at least one sub factory.");
		this.factories = factories;
	}
	
	//-------- IAgentFactory methods --------
	
	/**
	 *  Create a kernel agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a kernel agent.
	 */
	public IKernelAgent createKernelAgent(IAgentAdapter adapter, String model, String config, Map arguments)
	{
		IKernelAgent ret = null;
		
		for(int i=0; ret==null && i<factories.size(); i++)
		{
			IAgentFactory fac = (IAgentFactory)factories.get(i);
			if(fac.isLoadable(model))
			{
				ret = fac.createKernelAgent(adapter, model, config, arguments);
			}
		}
		
		if(ret==null)
			throw new AgentCreationException(""+model, null);
		return ret;
	}
	
	

	/**
	 *  Load an agent model.
	 *  @param filename The filename.
	 */
	public IAgentModel loadModel(String filename)
	{
		IAgentModel ret = null;
		
		for(int i=0; ret==null && i<factories.size(); i++)
		{
			IAgentFactory fac = (IAgentFactory)factories.get(i);
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
			IAgentFactory fac = (IAgentFactory)factories.get(i);
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
			IAgentFactory fac = (IAgentFactory)factories.get(i);
			ret = fac.isStartable(model);
		}
		
		return ret;
	}


	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes()
	{
		String[]	ret	= new String[0];
		for(int i=0; i<factories.size(); i++)
		{
			IAgentFactory fac = (IAgentFactory)factories.get(i);
			ret	= (String[]) SUtil.joinArrays(ret, fac.getFileTypes());
		}
		return ret;
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type)
	{
		Icon	ret = null;
		for(int i=0; ret==null && i<factories.size(); i++)
		{
			IAgentFactory fac = (IAgentFactory)factories.get(i);
			ret = fac.getFileTypeIcon(type);
		}
		return ret;
	}


	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model)
	{
		String	ret = null;
		for(int i=0; ret==null && i<factories.size(); i++)
		{
			IAgentFactory fac = (IAgentFactory)factories.get(i);
			ret = fac.getFileType(model);
		}
		return ret;
	}
}
