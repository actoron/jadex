package jadex.bridge.service.types.address;

import java.util.Collection;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for translating platform names
 *  to communication addresses and address book
 *  management.
 *
 */
@Service(system=true)
@Security(roles=Security.UNRESTRICTED)	// TODO: platform flag to turn on/off unrestricted
public interface ITransportAddressService
{
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<TransportAddress>> getAddresses();
	
	/**
	 *  Gets the addresses of the local platform.
	 *  
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<TransportAddress>> getAddresses(String transporttype);
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<TransportAddress>> getAddresses(IComponentIdentifier platformid);
	
	/**
	 *  Gets the addresses of another platform known to the local platform.
	 *  
	 *  @param platformid ID of the platform.
	 *  @param transporttype The transport type.
	 *  @return Addresses of the platform, if known.
	 */
	public IFuture<List<TransportAddress>> getAddresses(IComponentIdentifier platformid, String transporttype);
	
	/**
	 *  Resolves the addresses of a platform for a specific transport type using multiple methods.
	 *  
	 *  @param platformid ID of the platform.
	 *  @param transporttype The transport type.
	 *  @return Addresses of the local platform.
	 */
	public IFuture<List<TransportAddress>> resolveAddresses(IComponentIdentifier platformid, String transporttype);
	
	/**
	 *  Adds the addresses of the local platform.
	 *  
	 *  @param addresses Local platform addresses.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addLocalAddresses(Collection<TransportAddress> addresses);
	
	/**
	 *  Subscribe to local address changes.
	 *  
	 *  @return Address and true if removed.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<TransportAddress, Boolean>> subscribeToLocalAddresses();
	
	/**
	 *  Adds the addresses of the local platform.
	 *  
	 *  @param addresses Local platform addresses.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addManualAddresses(Collection<TransportAddress> addresses);
}
