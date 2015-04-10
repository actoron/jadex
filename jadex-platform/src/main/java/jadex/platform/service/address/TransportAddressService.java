package jadex.platform.service.address;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *  Service that manages the transport addresses of remote platforms.
 */
@Service
public class TransportAddressService implements ITransportAddressService
{
	/** The managed addresses. */
	protected Map<String, String[]> addresses = Collections.synchronizedMap(new HashMap<String, String[]>());
	
	/**
	 *  Set the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> addPlatformAddresses(ITransportComponentIdentifier platform)
	{
		// Must be synchronized due to direct access via getMap()
//		if(addresses==null)
//			addresses = Collections.synchronizedMap(new HashMap<String, String[]>());
		addresses.put(platform.getName(), platform.getAddresses());
		
		System.out.println("added: "+platform.getName()+" "+SUtil.arrayToString(platform.getAddresses()));
		return Future.DONE;
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<Void> removePlatformAddresses(ITransportComponentIdentifier platform)
	{
//		if(addresses!=null)
		addresses.remove(platform.getName());
		return Future.DONE;
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public IFuture<String[]> getPlatformAddresses(IComponentIdentifier component)
	{
		Future<String[]> ret = new Future<String[]>();
		if(addresses==null || !addresses.containsKey(component.getName()))
		{
			if(component instanceof ComponentIdentifier)
			{
				ret.setResult(((ITransportComponentIdentifier)component).getAddresses());
			}
			else
			{
				ret.setException(new RuntimeException("Not contained: "+component.getName()));
			}
		}
		else
		{
			ret.setResult(addresses.get(component.getName()));
		}
		return ret;
	}
	
	/**
	 *  Create a transport component identifier.
	 *  @param The component identifier.
	 *  @return The transport component identifier.
	 */
	public IFuture<ITransportComponentIdentifier> getTransportComponentIdentifier(IComponentIdentifier component)
	{
		Future<ITransportComponentIdentifier> ret = new Future<ITransportComponentIdentifier>();
		if(addresses==null || !addresses.containsKey(component.getName()))
		{
			if(component instanceof ComponentIdentifier)
			{
				ret.setResult((ITransportComponentIdentifier)component);
			}
			else
			{
				ret.setException(new RuntimeException("Not contained: "+component.getName()));
			}
		}
		else
		{
			ret.setResult(new ComponentIdentifier(component.getName(), addresses.get(component.getName())));
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
		if(components!=null && components.length>0)
		{
			ITransportComponentIdentifier[] res = new ITransportComponentIdentifier[components.length];
			for(int i=0; i<components.length; i++)
			{
				if(addresses==null || !addresses.containsKey(components[i].getName()))
				{
					if(components[i] instanceof ComponentIdentifier)
					{
						res[i] = ((ITransportComponentIdentifier)components[i]);
					}
					else
					{
						ret.setException(new RuntimeException("Not contained: "+components[i].getName()));
						break;
					}
				}
				else
				{
					res[i] = new ComponentIdentifier(components[i].getName(), addresses.get(components[i].getName()));
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}

	/**
	 *  Get direct access to the map of the addresses.
	 *  @return The map.
	 */
	public IFuture<Map<String, String[]>> getTransportAddresses()
	{
		return new Future<Map<String, String[]>>(Collections.unmodifiableMap(addresses));
	}
	
	/**
	 *  Internal convert method for identifiers.
	 */
	public static ITransportComponentIdentifier getTransportComponentIdentifier(IComponentIdentifier cid, Map<String, String[]> addresses)
	{
		ITransportComponentIdentifier ret = null;
		
		// Todo: add saved addresses also for custom transport identifier?
		if(cid instanceof ITransportComponentIdentifier)
		{
			ret = (ITransportComponentIdentifier)cid;
		}
		else
		{
			String[]	adrs	= addresses.get(cid.getPlatformName());
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
