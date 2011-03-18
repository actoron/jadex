package jadex.micro;

import jadex.bridge.IArgument;
import jadex.bridge.IModelValueProvider;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;

import java.util.Collections;
import java.util.HashMap;
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
	protected RequiredServiceInfo[] requiredservices;
	
	/** The provided services. */
	protected ProvidedServiceInfo[] providedservices;
	
	/** The master flag provider. */
	protected IModelValueProvider master;
	
	/** The daemon flag provider. */
	protected IModelValueProvider daemon;
	
	/** The autoshutdown flag provider. */
	protected IModelValueProvider autoshutdown;
	
	//-------- constructors --------
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo()
	{
	}
	
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
		RequiredServiceInfo[] requiredservices, ProvidedServiceInfo[] providedservices)
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
		RequiredServiceInfo[] requiredservices, ProvidedServiceInfo[] providedservices)
	{
		this(description, configs, args, results, breakpoints, properties, requiredservices, providedservices, null, null, null);
	}
	
	/**
	 *  Create a new meta info.
	 */
	public MicroAgentMetaInfo(String description, String[] configs, 
		IArgument[] args, IArgument[] results, String[] breakpoints, Map properties,
		RequiredServiceInfo[] requiredservices, ProvidedServiceInfo[] providedservices, 
		IModelValueProvider master, IModelValueProvider daemon, IModelValueProvider autoshutdown)
	{
		this.description = description;
		this.configs = configs;
		this.args = args;
		this.results = results;
		this.breakpoints = breakpoints;
		this.properties = properties;
		this.requiredservices = requiredservices;
		this.providedservices = providedservices;
		this.master = master;
		this.daemon = daemon;
		this.autoshutdown = autoshutdown;
	}

	//-------- methods --------
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public String[] getConfigurations()
	{
		return configs==null? SUtil.EMPTY_STRING_ARRAY: configs;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		return args==null? new IArgument[0]: args;
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults()
	{
		return results==null? new IArgument[0]: results;
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
		return breakpoints==null? SUtil.EMPTY_STRING_ARRAY: breakpoints;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public Map getProperties()
	{
		return properties==null? Collections.EMPTY_MAP: properties;
	}
	
	/**
	 *  Put a property in.
	 *  @param name The property name.
	 *  @param value The value.
	 */
	public void putPropertyValue(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap();
		properties.put(name, value);
	}

	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getRequiredServices()
	{
		return requiredservices==null? new RequiredServiceInfo[0]: requiredservices;
	}

	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public ProvidedServiceInfo[] getProvidedServices()
	{
		return providedservices==null? new ProvidedServiceInfo[0]: providedservices;
	}

	/**
	 *  Get the master.
	 *  @return the master.
	 */
	public IModelValueProvider getMaster()
	{
		return master;
	}

	/**
	 *  Get the daemon.
	 *  @return the daemon.
	 */
	public IModelValueProvider getDaemon()
	{
		return daemon;
	}

	/**
	 *  Get the autoshutdown.
	 *  @return the autoshutdown.
	 */
	public IModelValueProvider getAutoShutdown()
	{
		return autoshutdown;
	}

	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Set the configs.
	 *  @param configs The configs to set.
	 */
	public void setConfigs(String[] configs)
	{
		this.configs = configs;
	}

	/**
	 *  Set the args.
	 *  @param args The args to set.
	 */
	public void setArguments(IArgument[] args)
	{
		this.args = args;
	}

	/**
	 *  Set the results.
	 *  @param results The results to set.
	 */
	public void setResults(IArgument[] results)
	{
		this.results = results;
	}

	/**
	 *  Set the breakpoints.
	 *  @param breakpoints The breakpoints to set.
	 */
	public void setBreakpoints(String[] breakpoints)
	{
		this.breakpoints = breakpoints;
	}

	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setProperties(Map properties)
	{
		this.properties = properties;
	}

	/**
	 *  Set the requiredservices.
	 *  @param requiredservices The requiredservices to set.
	 */
	public void setRequiredServices(RequiredServiceInfo[] requiredservices)
	{
		this.requiredservices = requiredservices;
	}

	/**
	 *  Set the providedservices.
	 *  @param providedservices The providedservices to set.
	 */
	public void setProvidedServices(ProvidedServiceInfo[] providedservices)
	{
		this.providedservices = providedservices;
	}

	/**
	 *  Set the master.
	 *  @param master The master to set.
	 */
	public void setMaster(IModelValueProvider master)
	{
		this.master = master;
	}

	/**
	 *  Set the daemon.
	 *  @param daemon The daemon to set.
	 */
	public void setDaemon(IModelValueProvider daemon)
	{
		this.daemon = daemon;
	}

	/**
	 *  Set the autoshutdown.
	 *  @param autoshutdown The autoshutdown to set.
	 */
	public void setAutoShutdown(IModelValueProvider autoshutdown)
	{
		this.autoshutdown = autoshutdown;
	}
	
}
