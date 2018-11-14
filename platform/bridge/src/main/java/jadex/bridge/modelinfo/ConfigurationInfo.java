package jadex.bridge.modelinfo;


import java.util.ArrayList;
import java.util.List;

import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;

/**
 *  Information contained in a component configuration.
 */
public class ConfigurationInfo extends Startable
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of contained components. */
	protected List<ComponentInstanceInfo> components;
	
	/** The list of argument default values. */
	protected List<UnparsedExpression> arguments;
	
	/** The list of result default values. */
	protected List<UnparsedExpression> results;
	
	/** The provided service overridings. */
	protected List<ProvidedServiceInfo> providedservices;
	
	/** The required service overridings. */
	protected List<RequiredServiceInfo> requiredservices;
	
	/** The initial steps. */
	protected List<UnparsedExpression> initialsteps;
	
	/** The end steps. */
	protected List<UnparsedExpression> endsteps;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application.
	 */
	public ConfigurationInfo()
	{
		this(null);
	}
	
	/**
	 *  Create a new application.
	 */
	public ConfigurationInfo(String name)
	{
		this.name = name;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Add a component.
	 *  @param component The component.
	 */
	public void addComponentInstance(ComponentInstanceInfo component)
	{
		if(components==null)
			components = new ArrayList<ComponentInstanceInfo>();
		this.components.add(component);
	}
	
	/**
	 *  Removes a component.
	 *  @param component The component.
	 */
	public void removeComponentInstance(ComponentInstanceInfo component)
	{
		if(components!=null)
		{
			this.components.remove(component);
		}
	}
	
	/**
	 *  Get all components.
	 *  @return The components.
	 */
	public ComponentInstanceInfo[] getComponentInstances()
	{
		return components!=null? components.toArray(new ComponentInstanceInfo[components.size()]): new ComponentInstanceInfo[0];
	}
	
	/**
	 *  Test if has a component instance.
	 */
	public boolean hasComponentInstance(String name, String typename)
	{
		boolean ret = false;
		if(components!=null)
		{
			for(ComponentInstanceInfo cci: components)
			{
				ret = cci.getName()==null && cci.getTypeName().equals(typename)
					|| (cci.getName()!=null && cci.getName().equals(name));
				if(ret)
					break;
			}
		}
		return ret;
	}
	
	
	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public UnparsedExpression[] getArguments()
	{
		return arguments!=null? arguments.toArray(new UnparsedExpression[arguments.size()]): new UnparsedExpression[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(UnparsedExpression[] arguments)
	{
		this.arguments = SUtil.arrayToList(arguments);
	}

	/**
	 *  Add an argument.
	 *  @param arg The argument.
	 */
	public void addArgument(UnparsedExpression argument)
	{
		if(arguments==null)
			arguments = new ArrayList<UnparsedExpression>();
		arguments.add(argument);
	}
	
	/**
	 *  Test if has an argument.
	 */
	public boolean hasArgument(String name)
	{
		boolean ret = false;
		if(arguments!=null)
		{
			for(UnparsedExpression arg: arguments)
			{
				ret = arg.getName().equals(name);
				if(ret)
					break;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the list of results.
	 *  @return The results.
	 */
	public UnparsedExpression[] getResults()
	{
		return results!=null? results.toArray(new UnparsedExpression[results.size()]): new UnparsedExpression[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setResults(UnparsedExpression[] results)
	{
		this.results = SUtil.arrayToList(results);
	}
	
	/**
	 *  Test if has a result.
	 */
	public boolean hasResult(String name)
	{
		boolean ret = false;
		if(results!=null)
		{
			for(UnparsedExpression res: results)
			{
				ret = res.getName().equals(name);
				if(ret)
					break;
			}
		}
		return ret;
	}

	/**
	 *  Add a result.
	 *  @param res The result.
	 */
	public void addResult(UnparsedExpression res)
	{
		if(results==null)
			results = new ArrayList<UnparsedExpression>();
		results.add(res);
	}
	
	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public ProvidedServiceInfo[] getProvidedServices()
	{
		return providedservices==null? new ProvidedServiceInfo[0]: 
			providedservices.toArray(new ProvidedServiceInfo[providedservices.size()]);
	}

	/**
	 *  Set the provided services.
	 *  @param provided services The provided services to set.
	 */
	public void setProvidedServices(ProvidedServiceInfo[] providedservices)
	{
		this.providedservices = SUtil.arrayToList(providedservices);
	}
	
	/**
	 *  Add a provided service.
	 *  @param providedservice The provided service.
	 */
	public void addProvidedService(ProvidedServiceInfo providedservice)
	{
		if(providedservices==null)
			providedservices = new ArrayList<ProvidedServiceInfo>();
		providedservices.add(providedservice);
	}
	
	/**
	 *  Remove a provided service.
	 *  @param providedservice The provided service.
	 */
	public void removeProvidedService(ProvidedServiceInfo providedservice)
	{
		if(providedservices!=null)
		{
			providedservices.remove(providedservice);
		}
	}
	
	/**
	 *  Test if has a provided service.
	 */
	public boolean hasProvidedService(String name)
	{
		boolean ret = false;
		if(providedservices!=null)
		{
			for(ProvidedServiceInfo psi: providedservices)
			{
				ret = psi.getName().equals(name);
				if(ret)
					break;
			}
		}
		return ret;
	}
	
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getServices()
	{
		return requiredservices==null? new RequiredServiceInfo[0]: 
			requiredservices.toArray(new RequiredServiceInfo[requiredservices.size()]);
	}

	/**
	 *  Set the required services.
	 *  @param required services The required services to set.
	 */
	public void setRequiredServices(RequiredServiceInfo[] requiredservices)
	{
		this.requiredservices = SUtil.arrayToList(requiredservices);
	}
	
	/**
	 *  Add a required service.
	 *  @param requiredservice The required service.
	 */
	public void addRequiredService(RequiredServiceInfo requiredservice)
	{
		if(requiredservices==null)
			requiredservices = new ArrayList<RequiredServiceInfo>();
		requiredservices.add(requiredservice);
	}
	
	/**
	 *  Remove a required service.
	 *  @param requiredservice The required service.
	 */
	public void removeRequiredService(RequiredServiceInfo requiredservice)
	{
		if(requiredservices!=null)
		{
			requiredservices.remove(requiredservice);
		}
	}
	
	/**
	 *  Test if has a required service.
	 */
	public boolean hasRequiredService(String name)
	{
		boolean ret = false;
		if(requiredservices!=null)
		{
			for(RequiredServiceInfo rsi: requiredservices)
			{
				ret = rsi.getName().equals(name);
				if(ret)
					break;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the initial steps.
	 *  @return The initial steps.
	 */
	public UnparsedExpression[] getInitialSteps()
	{
		return initialsteps==null? new UnparsedExpression[0]: 
			initialsteps.toArray(new UnparsedExpression[initialsteps.size()]);
	}

	/**
	 *  Set the initial steps.
	 *  @param initial steps The initial steps to set.
	 */
	public void setInitialSteps(UnparsedExpression[] initialsteps)
	{
		this.initialsteps = SUtil.arrayToList(initialsteps);
	}
	
	/**
	 *  Add a initial step.
	 *  @param initialstep The initial step.
	 */
	public void addInitialStep(UnparsedExpression initialstep)
	{
		if(initialsteps==null)
			initialsteps = new ArrayList<UnparsedExpression>();
		initialsteps.add(initialstep);
	}
	
	/**
	 *  Get the end steps.
	 *  @return The end steps.
	 */
	public UnparsedExpression[] getEndSteps()
	{
		return endsteps==null? new UnparsedExpression[0]: 
			endsteps.toArray(new UnparsedExpression[endsteps.size()]);
	}

	/**
	 *  Set the end steps.
	 *  @param end steps The end steps to set.
	 */
	public void setEndSteps(UnparsedExpression[] endsteps)
	{
		this.endsteps = SUtil.arrayToList(endsteps);
	}
	
	/**
	 *  Add a end step.
	 *  @param endstep The end step.
	 */
	public void addEndStep(UnparsedExpression endstep)
	{
		if(endsteps==null)
			endsteps = new ArrayList<UnparsedExpression>();
		endsteps.add(endstep);
	}
}
