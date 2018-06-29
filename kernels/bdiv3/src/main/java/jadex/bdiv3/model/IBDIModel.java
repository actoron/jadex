package jadex.bdiv3.model;

import jadex.bridge.modelinfo.IModelInfo;

/**
 *  Common interface for micro- and xml-based BDI agent models.
 */
public interface IBDIModel 
{
	/**
	 *  Get the component model.
	 */
	public IModelInfo	getModelInfo();
	
	/**
	 *  Get the mcapa.
	 *  @return The mcapa.
	 */
	public MCapability getCapability();
}
