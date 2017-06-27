package jadex.bridge.service.types.address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;

/**
 *  Management of transport addresses, i.e. what a platform knows about communication to other platforms.
 */
public class TransportAddressBook
{
	/** The managed addresses: target platform -> (transport name -> transport addresses) */
	protected Map<IComponentIdentifier, Map<String, List<String>>> addresses = new HashMap<IComponentIdentifier, Map<String, List<String>>>();
	
	/** The listeners. */
	protected List<IChangeListener<IComponentIdentifier>> listeners;
	
	/** Creates the address book. */
	public TransportAddressBook()
	{
		this.listeners = Collections.synchronizedList(new LinkedList<IChangeListener<IComponentIdentifier>>());
	}
	
	/**
	 *  Add addresses for a platform.
	 *  @param platform	The component identifier of the platform.
	 *  @param transport	The transport name used as protocol scheme, e.g. 'tcp'.
	 *  @param addresses	A list of addresses (e.g. 'host:port').
	 */
	public synchronized void addPlatformAddresses(IComponentIdentifier platform, String transport, String[] addresses)
	{
//		System.out.println("New addr:" + platform + " " + Arrays.toString(addresses));
		if(addresses!=null && addresses.length>0)
		{
			Map<String, List<String>>	platformaddresses	= this.addresses.get(platform.getRoot());
			if(platformaddresses==null)
			{
				platformaddresses	= new HashMap<String, List<String>>();
				this.addresses.put(platform.getRoot(), platformaddresses);
			}
			
			List<String>	laddresses	= platformaddresses.get(transport);		
			if(laddresses==null)
			{
				laddresses	= new ArrayList<String>();
				platformaddresses.put(transport, laddresses);
			}
			
			for(String address: addresses)
			{
				laddresses.add(address);
			}
		}
		
		notifyListeners(platform.getRoot());
	}
	
	/**
	 *  Remove the addresses of a platform.
	 *  @param platform The component identifier of the platform.
	 */
	public synchronized void removePlatformAddresses(IComponentIdentifier platform)
	{
		addresses.remove(platform.getRoot());
		notifyListeners(platform.getRoot());
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
	
	/**
	 *  Gets all addresses of a specific platform.
	 *  
	 *  @param platform The platform.
	 *  @return All known addresses of the platform.
	 */
	public synchronized Map<String, String[]> getAllPlatformAddresses(IComponentIdentifier platform)
	{
		Map<String, List<String>> addr = addresses.get(platform);
		Map<String, String[]> ret = new HashMap<String, String[]>();
		if (addr != null)
		{
			for (Map.Entry<String, List<String>> entry : addr.entrySet())
			{
				String[] addresses = entry.getValue().toArray(new String[entry.getValue().size()]);
				ret.put(entry.getKey(), addresses);
			}
		}
		return ret;
	}
	
	/**
	 *  Sets all addresses of a specific platform.
	 *  
	 *  @param platform The platform.
	 *  @param addr All known addresses of the platform.
	 *  @return True, if addresses were added.
	 */
	public synchronized boolean mergePlatformAddresses(IComponentIdentifier platform, Map<String, String[]> addr)
	{
		boolean ret = false;
		Map<String, List<String>> bookaddr = addresses.get(platform);
		if (addr != null)
		{
			if (bookaddr == null)
			{
				bookaddr = new HashMap<String, List<String>>();
				addresses.put(platform, bookaddr);
				ret = true;
			}
			
			for (Map.Entry<String, String[]> entry : addr.entrySet())
			{
				List<String> bookentries = bookaddr.get(entry.getKey());
				if (bookentries == null)
				{
					bookaddr.put(entry.getKey(), new ArrayList<String>(Arrays.asList(entry.getValue())));
					ret = true;
				}
				else
				{
					Set<String> known = new HashSet<String>(bookentries);
					for (String singleaddr : entry.getValue())
					{
						if (!known.contains(singleaddr))
						{
							bookentries.add(singleaddr);
							ret = true;
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Adds a change listener.
	 *  
	 *  @param listener The change listener.
	 */
	public void addListener(IChangeListener<IComponentIdentifier> listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Removes a change listener.
	 *  
	 *  @param listener The change listener.
	 */
	public void removeListener(IChangeListener<IComponentIdentifier> listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Notifies the listeners.
	 */
	protected void notifyListeners(IComponentIdentifier affectedplatform)
	{
		synchronized (listeners)
		{
//			System.out.println("Notify listeners: " + listeners.size() + " " + affectedplatform);
			for (IChangeListener<IComponentIdentifier> listener : listeners)
			{
				listener.changeOccurred(new ChangeEvent<IComponentIdentifier>(affectedplatform));
			}
		}
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
	/**
	 *  Get the address book from a component.
	 */
	public static TransportAddressBook getAddressBook(IComponentIdentifier platform)
	{
		return (TransportAddressBook)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_ADDRESSBOOK);
	}
	
	/**
	 *  Get the address book from a component.
	 */
	public static TransportAddressBook getAddressBook(IInternalAccess agent)
	{
		return getAddressBook(agent.getComponentIdentifier());
	}
}
