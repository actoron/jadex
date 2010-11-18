package jadex.micro;

import jadex.bridge.IArgument;
import jadex.commons.SUtil;

import java.util.Collections;
import java.util.Map;

/**
 *  Meta info for micro agents.
 */
public class MicroAgentMetaInfo
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;
	
	/** The configurations. */
	protected String[] configs;
	
	/** The arguments. */
	protected IArgument[] args;
	
	/** The results. */
	protected IArgument[] results;
	
	/** The breakpoints. */
	protected String[] breakpoints;
	
	/** The property map. */
	protected Map properties;
	
	/** The required services. */
	protected Class[] requiredservices;
	
	/** The provided services. */
	protected Class[] providedservices;
	
	//-------- constructors --------
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs)
	{
		this(description, configs, (IArgument[])null, null);
	}
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs, 
		IArgument[] args, IArgument[] results)
	{
		this(description, configs, args, results, null, null);
	}
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs, 
		Class[] requiredservices, Class[] providedservices)
	{
		this(description, configs, null, null, null, null, requiredservices, providedservices);
	}
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs, 
		IArgument[] args, IArgument[] results, String[] breakpoints, Map properties)
	{
		this(description, configs, args, results, breakpoints, properties, null, null);
	}
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs, 
		IArgument[] args, IArgument[] results, String[] breakpoints, Map properties,
		Class[] requiredservices, Class[] providedservices)
	{
		this.description = description;
		this.configs = configs == null? SUtil.EMPTY_STRING_ARRAY: configs;
		this.args = args == null? new IArgument[0]: args;
		this.results = results == null? new IArgument[0]: results;
		this.breakpoints = breakpoints == null? new String[0]: breakpoints;
		this.properties = properties==null? Collections.EMPTY_MAP: properties;
		this.requiredservices = requiredservices==null? SUtil.EMPTY_CLASS_ARRAY: requiredservices;
		this.providedservices = providedservices==null? SUtil.EMPTY_CLASS_ARRAY: providedservices;
	}

	//-------- methods --------
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public String[] getConfigurations()
	{
		return configs;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		return args;
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults()
	{
		return results;
	}

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Get the breakpoints.
	 *  @return The breakpoints.
	 */
	public String[] getBreakpoints()
	{
		return breakpoints;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties;
	}

	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public Class[] getRequiredServices()
	{
		return requiredservices;
	}

	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public Class[] getProvidedServices()
	{
		return providedservices;
	}
}
