package jadex.bridge.modelinfo;


import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  Information contained in a component configuration.
 */
public class ConfigurationInfo extends Startable
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of contained components. */
	protected List components;
	
	/** The list of argument default values. */
	protected List arguments;
	
	/** The list of extensions. */
	protected List extensions;
	
	/** The provided service overridings. */
	protected List providedservices;
	
	/** The required service overridings. */
	protected List requiredservices;
	
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
	 *  Add an component.
	 *  @param component The component.
	 */
	public void addComponentInstance(ComponentInstanceInfo component)
	{
		if(components==null)
			components = new ArrayList();
		this.components.add(component);
	}
	
	/**
	 *  Get all components.
	 *  @return The components.
	 */
	public ComponentInstanceInfo[] getComponentInstances()
	{
		return components!=null? (ComponentInstanceInfo[])components.toArray(new ComponentInstanceInfo[components.size()]): new ComponentInstanceInfo[0];
	}
	
	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public UnparsedExpression[] getArguments()
	{
		return arguments!=null? (UnparsedExpression[])arguments.toArray(new UnparsedExpression[arguments.size()]): new UnparsedExpression[0];
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
			arguments = new ArrayList();
		arguments.add(argument);
	}
	
	/**
	 *  Get the extension names. 
	 */
	public IExtensionInstance[] getExtensions()
	{
		return extensions!=null? (IExtensionInstance[])extensions.toArray(new IExtensionInstance[extensions.size()]): new IExtensionInstance[0];
	}
	
	/**
	 *  Set the extension types.
	 */
	public void setExtensions(Object[] extensions)
	{
		this.extensions = SUtil.arrayToList(extensions);
	}
	
	/**
	 *  Add a extension type.
	 *  @param extension The extension type.
	 */
	public void addExtension(Object extension)
	{
		if(extensions==null)
			extensions = new ArrayList();
		extensions.add(extension);
	}
	
	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public ProvidedServiceInfo[] getProvidedServices()
	{
		return providedservices==null? new ProvidedServiceInfo[0]: 
			(ProvidedServiceInfo[])providedservices.toArray(new ProvidedServiceInfo[providedservices.size()]);
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
			providedservices = new ArrayList();
		providedservices.add(providedservice);
	}
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getRequiredServices()
	{
		return requiredservices==null? new RequiredServiceInfo[0]: 
			(RequiredServiceInfo[])requiredservices.toArray(new RequiredServiceInfo[requiredservices.size()]);
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
			requiredservices = new ArrayList();
		requiredservices.add(requiredservice);
	}
}
