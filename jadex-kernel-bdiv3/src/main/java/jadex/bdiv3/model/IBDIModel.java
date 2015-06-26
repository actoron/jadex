package jadex.bdiv3.model;

import jadex.bridge.modelinfo.IModelInfo;

import java.util.Map;

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
	
	/**
	 *  Get the fully qualified belief references (abstract/reference name -> concrete belief name).
	 */
	public Map<String, String> getBeliefReferences();
}
