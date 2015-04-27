package jadex.platform.service.address;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Service that manages the transport addresses of remote platforms.
 */
@Service
public class TransportAddressService implements ITransportAddressService
{
	/** The managed addresses. */
	private TransportAddressBook addresses;
	
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@ServiceStart
	public void started()
	{
		addresses = TransportAddressBook.getAddressBook(agent);
	}
	
	/**
	 *  Set the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> addPlatformAddresses(ITransportComponentIdentifier platform)
	{
		addresses.addPlatformAddresses(platform);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> removePlatformAddresses(ITransportComponentIdentifier platform)
	{
		addresses.removePlatformAddresses(platform);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<String[]> getPlatformAddresses(IComponentIdentifier component)
	{
		String[] ret = addresses.getPlatformAddresses(component);
		return new Future<String[]>(ret);
	}
	
	/**
	 *  Create a transport component identifier.
	 *  @param The component identifier.
	 *  @return The transport component identifier.
	 */
	public IFuture<ITransportComponentIdentifier> getTransportComponentIdentifier(IComponentIdentifier component)
	{
		Future<ITransportComponentIdentifier> ret = new Future<ITransportComponentIdentifier>();
		
		ITransportComponentIdentifier res = addresses.getTransportComponentIdentifier(component);
		if(res!=null)
		{
			ret.setResult(res);
		}
		else
		{
			ret.setException(new RuntimeException("Not contained: "+component.getPlatformName()));
		}
		
		return ret;
	}
	
	/**
	 *  Create a transport component identifiers.
	 *  @param The component identifiers.
	 *  @return The transport component identifiers.
	 */
	public IFuture<ITransportComponentIdentifier[]> getTransportComponentIdentifiers(IComponentIdentifier[] components)
	{
		Future<ITransportComponentIdentifier[]> ret = new Future<ITransportComponentIdentifier[]>();
		
		ITransportComponentIdentifier[] res = addresses.getTransportComponentIdentifiers(components);
		if(res!=null)
		{
			ret.setResult(res);
		}
		else
		{
			ret.setException(new RuntimeException("Not contained: "+SUtil.arrayToString(components)));
		}
		
		return ret;
	}

	/**
	 *  Get direct access to the map of the addresses.
	 *  @return The map.
	 */
	public IFuture<TransportAddressBook> getTransportAddresses()
	{
		return new Future<TransportAddressBook>(addresses);
	}
	
	/**
	 *  Internal convert method for identifiers.
	 */
	public static ITransportComponentIdentifier getTransportComponentIdentifier(IComponentIdentifier cid, TransportAddressBook addresses)
	{
		ITransportComponentIdentifier ret = null;
		
		// Todo: add saved addresses also for custom transport identifier?
		if(cid instanceof ITransportComponentIdentifier)
		{
			ret = (ITransportComponentIdentifier)cid;
		}
		else
		{
			String[] adrs = addresses.getPlatformAddresses(cid);
			if(adrs!=null)
			{
				ret = new ComponentIdentifier(cid.getName(), adrs);
			}
			else
			{
				throw new RuntimeException("Not contained: "+cid.getName());
			}
		}
		
		return ret;
	}
}
