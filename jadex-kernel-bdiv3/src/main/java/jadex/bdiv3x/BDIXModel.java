package jadex.bdiv3x;

import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MCapability;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;

/**
 *  Model for BDI v3x agents
 */
public class BDIXModel	extends ModelInfo implements IBDIModel
{
	//-------- attributes --------
	
	/** The capability. */
	protected MCapability	capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model.
	 */
	public BDIXModel()
	{
		this.capa	= new MCapability();
	}
	
	//-------- IBDIModel interface --------
	
	/**
	 *  Get the component model.
	 */
	public IModelInfo	getModelInfo()
	{
		return this;
	}
	
	/**
	 *  Get the capability.
	 */
	public MCapability	getCapability()
	{
		return capa;
	}
	
	/**
	 *  Overridden to avoid null pointer when getRawModel() is used.
	 */
	public Object getRawModel()
	{
		return this;
	}
}
