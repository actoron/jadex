package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type. */
	protected String type;
	
	/** The configuration. */
	protected String configuration;

	/** The start flag. */
	protected boolean start;
	
	/** The list of contained parameters. */
	protected List parameters;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public Agent()
	{
		this.parameters = new ArrayList();
		this.start = true;
	}
	
	//-------- methods --------
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return the configuration
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}
	
	/**
	 * @return the start
	 */
	public boolean isStart()
	{
		return this.start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(boolean start)
	{
		this.start = start;
	}

	/**
	 * 
	 */
	public void addParameters(Parameter param)
	{
		this.parameters.add(param);
	}
	
	/**
	 * 
	 */
	public void addParameters(ParameterSet paramset)
	{
		this.parameters.add(paramset);
	}

	/**
	 * @return the parameters
	 */
	public List getParameters()
	{
		return this.parameters;
	}
	
	/**
	 * 
	 */
	public Map getArguments()
	{
		Map ret = null;

		if(parameters!=null)
		{
			ret = new HashMap();
			for(int i=0; i<parameters.size(); i++)
			{
				Object tmp = parameters.get(i);
				if(tmp instanceof Parameter)
				{
					Parameter p = (Parameter)tmp;
					ret.put(p.getName(), p.getValue());
				}
				else //if(tmp instanceof ParameterSet)
				{
					ParameterSet ps = (ParameterSet)tmp;
					ret.put(ps.getName(), ps.getValues());
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public String getModel(ApplicationType apptype)
	{
		String ret = null;
		List agenttypes = apptype.getAgentTypes();
		for(int i=0; ret==null && i<agenttypes.size(); i++)
		{
			AgentType at = (AgentType)agenttypes.get(i);
			if(at.getName().equals(getType()))
				ret = at.getFilename();
		}
		return ret;
	}
}
