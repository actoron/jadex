package jadex.bridge.service.types.address;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Service that manages the platform transport addresses.
 */
public interface ITransportAddressService
{
	/**
	 *  Set the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> addPlatformAddresses(ITransportComponentIdentifier platform);
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> removePlatformAddresses(ITransportComponentIdentifier platform);
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<String[]> getPlatformAddresses(IComponentIdentifier component);
	
	/**
	 *  Create a transport component identifier.
	 *  @param The component identifier.
	 *  @return The transport component identifier.
	 */
	public IFuture<ITransportComponentIdentifier> getTransportComponentIdentifier(IComponentIdentifier component); 
	
	/**
	 *  Create a transport component identifiers.
	 *  @param The component identifiers.
	 *  @return The transport component identifiers.
	 */
	public IFuture<ITransportComponentIdentifier[]> getTransportComponentIdentifiers(IComponentIdentifier[] component); 
}
