package jadex.bridge.component;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.search.LocalServiceRegistry;
import jadex.bridge.service.types.cms.IComponentDescription;

import java.util.Map;

/**
 *  Internal parameter object for data required during component initialization.
 */
public class ComponentCreationInfo
{
	//-------- attributes --------
	
	/** The model. */
	protected IModelInfo	model;
	
	/** The start configuration name. */
	protected String config;
	
	/** The arguments. */
	protected Map<String, Object> arguments;
	
	/** The component description. */
	// Hack??? Should be only available in CMS (single thread access)
	protected IComponentDescription desc;
	
	/** The service registry of the local platform. */
	protected LocalServiceRegistry	registry;
	
	/** The real time flag. */
	protected boolean	realtime;
	
	/** The copy flag. */
	protected boolean	copy;
	
	/** The provided service infos. */
	protected ProvidedServiceInfo[]	infos;
	
	//-------- constructors --------
	
	/**
	 *  Create an info object.
	 *  @param model	The model (required).
	 *  @param config	The configuration name or null for default (if any).
	 *  @param arguments	The arguments (if any).
	 *  @param desc	The component description (required).
	 *  @param registry	The service registry of the local platform.
	 *  @param realtime	The real time flag.
	 *  @param copy	The copy flag.
	 */
	public ComponentCreationInfo(IModelInfo model, String config, Map<String, Object> arguments, IComponentDescription desc, LocalServiceRegistry registry, ProvidedServiceInfo[] infos, boolean realtime, boolean copy)
	{
		this.model	= model;
		this.config = config!=null ? config : model.getConfigurationNames().length>0 ? model.getConfigurationNames()[0] : null;
		this.arguments	= arguments;
		this.registry	= registry;
		this.desc	= desc;
		this.infos	= infos;
		this.realtime	= realtime;
		this.copy	= copy;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the model.
	 */
	public IModelInfo	getModel()
	{
		return this.model;
	}
	
	/**
	 *  Get the configuration.
	 */
	public String	getConfiguration()
	{
		return this.config;
	}
	
	/**
	 *  Get the arguments.
	 */
	public Map<String, Object>	getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Get the component description.
	 */
	public IComponentDescription	getComponentDescription()
	{
		return this.desc;
	}
	
	/**
	 *  Get the local platform service registry.
	 *  
	 *  @return The local platform service registry.
	 */
	public LocalServiceRegistry	getServiceRegistry()
	{
		return registry;
	}

	/**
	 *  Get the provided service infos.
	 *  
	 *  @return The provided service infos..
	 */
	public ProvidedServiceInfo[]	getProvidedServiceInfos()
	{
		return infos;
	}

	/**
	 *  Get the real time flag.
	 */
	public boolean isRealtime()
	{
		return realtime;
	}

	/**
	 *  Get the copy flag.
	 */
	public boolean isCopy()
	{
		return copy;
	}
}