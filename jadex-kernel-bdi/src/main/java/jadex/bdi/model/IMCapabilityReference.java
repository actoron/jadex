package jadex.bdi.model;


/**
 *  Interface for capability reference.
 */
public interface IMCapabilityReference extends IMElement
{
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename();
	
	/**
	 *  Get the capability.
	 *  @return The capability.
	 */
	public IMCapability getCapability();
}

