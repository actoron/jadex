package jadex.bridge.service.types.factory;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;

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
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------
	
	/**
	 *  Create an info object.
	 *  @param model	The model (required).
	 *  @param config	The configuration name or null for default (if any).
	 *  @param cid	The component identifier (required).
	 */
	public ComponentCreationInfo(IModelInfo model, String config, IComponentIdentifier cid)
	{
		this.model	= model;
		this.config = config!=null ? config : model.getConfigurationNames().length>0 ? model.getConfigurationNames()[0] : null;
		this.cid	= cid;
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
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return this.cid;
	}
}