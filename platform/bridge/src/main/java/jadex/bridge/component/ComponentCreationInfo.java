package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.types.cms.IComponentDescription;

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
	
	/** The provided service infos. */
	protected ProvidedServiceInfo[]	infos;
	
	/** The required service bindings. */
	protected RequiredServiceBinding[]	bindings;
	
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
	public ComponentCreationInfo(IModelInfo model, String config, Map<String, Object> arguments, 
		IComponentDescription desc, ProvidedServiceInfo[] infos, RequiredServiceBinding[] bindings)
	{
		this.model	= model;
		this.config = config!=null ? config : model.getConfigurationNames().length>0 ? model.getConfigurationNames()[0] : null;
		this.arguments	= arguments;
		this.desc	= desc;
		this.infos	= infos;
		this.bindings	= bindings;
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
	 *  Get the provided service infos.
	 *  
	 *  @return The provided service infos..
	 */
	public ProvidedServiceInfo[]	getProvidedServiceInfos()
	{
		return infos;
	}
	
	/**
	 *  Get the bindings.
	 *  @return The bindings.
	 */
	public RequiredServiceBinding[] getRequiredServiceBindings()
	{
		return bindings;
	}
}