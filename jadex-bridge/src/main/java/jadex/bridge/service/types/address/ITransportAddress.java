package jadex.bridge.service.types.address;

import jadex.bridge.IComponentIdentifier;

/**
 *  Interface representing a transport address of a specific platform.
 *
 */
public interface ITransportAddress
{
	/**
	 *  Gets the ID of the platform owning the address.
	 * 
	 *  @return The ID of the platform owning the address.
	 */
	public IComponentIdentifier getPlatformId();
	
	/**
	 *  Gets the type of transport using the address.
	 *  
	 *  @return The type of transport.
	 */
	public String getTransportType();
	
	/**
	 *  Gets the address.
	 *  
	 *  @return The address.
	 */
	public String getAddress();
}
