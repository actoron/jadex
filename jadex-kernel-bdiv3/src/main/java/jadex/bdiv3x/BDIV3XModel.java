package jadex.bdiv3x;

import jadex.bdiv3.model.MCapability;
import jadex.bridge.modelinfo.ModelInfo;

/**
 *  Model for BDI v3x agents
 */
public class BDIV3XModel	extends ModelInfo
{
	//-------- attributes --------
	
	/** The capability. */
	protected MCapability	capa;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model.
	 */
	public BDIV3XModel()
	{
		this.capa	= new MCapability();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the capability.
	 */
	public MCapability	getCapability()
	{
		return capa;
	}
}
