package jadex.bdiv3.model;

import java.util.Map;

/**
 *  Common interface for micro- and xml-based BDI agent models.
 */
public interface IBDIModel
{
	/**
	 *  Get the mcapa.
	 *  @return The mcapa.
	 */
	public MCapability getCapability();
	
	/**
	 *  Get the belief mappings (target->source).
	 */
	public Map<String, String> getBeliefMappings();
}
