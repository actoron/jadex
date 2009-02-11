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

	/** The list of contained parameters. */
	protected List parameters;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public Agent()
	{
		this.parameters = new ArrayList();
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
		Map ret = new HashMap();
		for(int i=0; i<parameters.size(); i++)
		{
			Object tmp = parameters.get(i);
			if(tmp instanceof Parameter)
			{
				
			}
			else
			{
				
			}
		}
		
		return null;
	}
	
}
