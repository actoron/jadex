package jadex.bridge.service.types.address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;

/**
 *  Management of transport addresses, i.e. what a platform knows about communication to other platforms.
 */
public class TransportAddressBook
{
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, Map<String, List<String>>> addresses = new HashMap<IComponentIdentifier, Map<String, List<String>>>();
	
	/**
	 *  Set the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 *  @param addresses A list of addresses in the form 'transportname://address' (uses '' as transportname, if none given).
	 */
	public synchronized void addPlatformAddresses(IComponentIdentifier platform, String[] addresses)
	{
		if(addresses!=null)
		{
			Map<String, List<String>>	platformaddresses	= this.addresses.get(platform.getRoot());
			if(platformaddresses==null)
			{
				platformaddresses	= new HashMap<String, List<String>>();
				this.addresses.put(platform.getRoot(), platformaddresses);
			}
			
			for(String address: addresses)
			{
				String	key	= "";
				int index	= address.indexOf("://");
				if(index!=-1)
				{
					key = address.substring(0, index);	
					address	= address.substring(index+3);
				}
				
				List<String>	list	= platformaddresses.get(key);
				if(list==null)
				{
					list	= new ArrayList<String>();
					platformaddresses.put(key, list);
				}
				list.add(address);
			}
		}
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public synchronized void removePlatformAddresses(IComponentIdentifier platform)
	{
		addresses.remove(platform.getRoot());
	}
	
	/**
	 *  Get the transport specific addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 *  @param transport The transport name
	 */
	public synchronized String[] getPlatformAddresses(IComponentIdentifier platform, String transport)
	{
		List<String>	list	= null;
		Map<String, List<String>>	platformaddresses	= this.addresses.get(platform.getRoot());
		if(platformaddresses!=null)
		{
			list	= platformaddresses.get(transport);
		}

		return list!=null ? list.toArray(new String[list.size()]) : null;
	}
	
//	/**
//	 *  Create a transport component identifier.
//	 *  @param The component identifier.
//	 *  @return The transport component identifier.
//	 */
//	public synchronized ITransportComponentIdentifier getTransportComponentIdentifier(IComponentIdentifier component)
//	{
//		ITransportComponentIdentifier ret = null;
//		if(addresses==null || !addresses.containsKey(component.getPlatformName()))
//		{
//			if(component instanceof ComponentIdentifier)
//			{
//				ret = (ITransportComponentIdentifier)component;
//			}
////			else
////			{
////				throw new RuntimeException("Not contained: "+component.getPlatformName());
////			}
//		}
//		else
//		{
//			ret = new ComponentIdentifier(component.getName(), addresses.get(component.getPlatformName()));
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Create a transport component identifiers.
//	 *  @param The component identifiers.
//	 *  @return The transport component identifiers.
//	 */
//	public synchronized ITransportComponentIdentifier[] getTransportComponentIdentifiers(IComponentIdentifier[] components)
//	{
//		ITransportComponentIdentifier[] ret = null;
//		if(components!=null && components.length>0)
//		{
//			ITransportComponentIdentifier[] res = new ITransportComponentIdentifier[components.length];
//			for(int i=0; i<components.length; i++)
//			{
//				if(addresses==null || !addresses.containsKey(components[i].getPlatformName()))
//				{
//					if(components[i] instanceof ComponentIdentifier)
//					{
//						res[i] = ((ITransportComponentIdentifier)components[i]);
//					}
////					else
////					{
////						throw new RuntimeException("Not contained: "+components[i].getPlatformName());
////					}
//				}
//				else
//				{
//					res[i] = new ComponentIdentifier(components[i].getName(), addresses.get(components[i].getPlatformName()));
//				}
//			}
//		}
////		else
////		{
////			ret = null;
////		}
//		return ret;
//	}
//
//	/**
//	 *  Get direct access to the map of the addresses.
//	 *  @return The map.
//	 */
//	public synchronized Map<String, String[]> getTransportAddresses()
//	{
//		return Collections.unmodifiableMap(addresses);
//	}
//	
//	/**
//	 *  Internal convert method for identifiers.
//	 */
//	public static ITransportComponentIdentifier getTransportComponentIdentifier(IComponentIdentifier cid, Map<String, String[]> addresses)
//	{
//		ITransportComponentIdentifier ret = null;
//		
//		// Todo: add saved addresses also for custom transport identifier?
//		if(cid instanceof ITransportComponentIdentifier)
//		{
//			ret = (ITransportComponentIdentifier)cid;
//		}
//		else
//		{
//			String[] adrs = addresses.get(cid.getPlatformName());
//			if(adrs!=null)
//			{
//				ret = new ComponentIdentifier(cid.getName(), adrs);
//			}
//			else
//			{
//				throw new RuntimeException("Not contained: "+cid.getName());
//			}
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the address book from a component.
//	 */
//	public static TransportAddressBook getAddressBook(IComponentIdentifier platform)
//	{
//		return (TransportAddressBook)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_ADDRESSBOOK);
//	}
//	
//	/**
//	 *  Get the address book from a component.
//	 */
//	public static TransportAddressBook getAddressBook(IInternalAccess agent)
//	{
//		return getAddressBook(agent.getComponentIdentifier());
//	}
}
