package jadex.bridge.service.types.address;

import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Service for translating platform names
 *  to communication addresses and address book
 *  management.
 *
 */
public interface ITransportAddressService
{
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<ITransportAddress>> getAddresses();
	
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<ITransportAddress>> getAddresses(String transporttype);
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<ITransportAddress>> getAddresses(IComponentIdentifier platformid);
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @param transporttype The transport type.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<ITransportAddress>> getAddresses(IComponentIdentifier platformid, String transporttype);
	
	/**
	 *  Recursively looks up the addresses of a platform for a specific transport type.
	 *  
	 *  @param platformid ID of the platform.
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<ITransportAddress>> getAddressesRecursively(IComponentIdentifier platformid, String transporttype);
}
