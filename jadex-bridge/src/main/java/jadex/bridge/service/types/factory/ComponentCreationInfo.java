package jadex.bridge.service.types.factory;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;

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
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	/** The real time flag. */
	protected boolean	realtime;
	
	/** The copy flag. */
	protected boolean	copy;
	
	//-------- constructors --------
	
	/**
	 *  Create an info object.
	 *  @param model	The model (required).
	 *  @param config	The configuration name or null for default (if any).
	 *  @param arguments	The arguments (if any).
	 *  @param cid	The component identifier (required).
	 *  @param realtime	The real time flag.
	 *  @param copy	The copy flag.
	 */
	public ComponentCreationInfo(IModelInfo model, String config, Map<String, Object> arguments, IComponentIdentifier cid, boolean realtime, boolean copy)
	{
		this.model	= model;
		this.config = config!=null ? config : model.getConfigurationNames().length>0 ? model.getConfigurationNames()[0] : null;
		this.arguments	= arguments;
		this.cid	= cid;
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
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return this.cid;
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