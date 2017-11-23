package jadex.bridge.service.types.registry;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.IService;

/**
 *  Registry event for notifications from the registry.
 */
public class RegistryEvent implements IRegistryEvent
{
	/** The added services. */
	protected Set<IService> addedservices;
//	protected Set<IServiceIdentifier> addedservices;
	
	/** The removed services. */
	protected Set<IService> removedservices;
//	protected Set<IServiceIdentifier> removedservices;

	/** The number of events that must have occured before a remote message is sent. */
	protected int eventslimit;
	
	/** The timestamp of the first event (change). */
	protected long timestamp; 
	
	/** The time limit. */
	protected long timelimit;
	
	/** Flag if is delta (or full) registry content. */
	protected boolean delta;
	
	
	//-------- for superpeer supersuperpeer interaction --------
	
	/** The client type (client, superpeer_1, superpeer_0, ...). */
	protected String clienttype;
	
	/** The network names. */
	protected String[] networknames;
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent()
	{
		this(true);
	}
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent(boolean delta)
	{
		this(null, null, 50, 5000, delta, null);
	}
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent(boolean delta, String clienttype)
	{
		this(null, null, 50, 5000, delta, clienttype);
	}
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent(boolean delta, long timelimit)
	{
		this(null, null, 50, timelimit, delta, null);
	}
	
	/**
	 *  Create a new registry event.
	 *  @param addedservices The added services.
	 *  @param removedservices The removed services.
	 */
	public RegistryEvent(Set<IService> addedservices, Set<IService> removedservices, int eventslimit, long timelimit, boolean delta, String clienttype)
	{
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
		this.timestamp = System.currentTimeMillis();
		this.delta = delta;
		this.clienttype = clienttype==null? IRegistryEvent.CLIENTTYPE_CLIENT: clienttype;
		setAddedServices(addedservices);
		setRemovedServices(removedservices);
	}
	
	/**
	 *  Get the addedservices.
	 *  @return the addedservices
	 */
	public Set<IService> getAddedServices()
	{
		return addedservices;
	}
	
	/**
	 *  Set the added services.
	 */
	public void setAddedServices(Set<IService> services)
	{
		this.addedservices = services;
//		if(services!=null && services.size()>0)
//		{
//			addedservices = new HashSet<IServiceIdentifier>();
//			for(IService ser: services)
//			{
//				addedservices.add(ser.getServiceIdentifier());
//			}
//		}
	}
	
	/**
	 *  Get the removedservices.
	 *  @return the removedservices
	 */
	public Set<IService> getRemovedServices()
	{
		return removedservices;
	}
	
	/**
	 *  Set the removed services.
	 */
	public void setRemovedServices(Set<IService> services)
	{
		this.removedservices = services;
//		if(services!=null && services.size()>0)
//		{
//			removedservices = new HashSet<IServiceIdentifier>();
//			for(IService ser: services)
//			{
//				removedservices.add(ser.getServiceIdentifier());
//			}
//		}
	}
	
	/**
	 *  Add an added service.
	 *  @return True, if changed.
	 */
	public boolean addAddedService(IService service)
	{
		if(service==null)
			throw new IllegalArgumentException("Service must not be null");
		if(addedservices==null)
			addedservices = new HashSet<IService>();
		return addedservices.add(service);
	}
	
	/**
	 *  Add an added service.
	 *  @return True, if changed.
	 */
	public boolean addRemovedService(IService service)
	{
		if(service==null)
			throw new IllegalArgumentException("Service must not be null");
		if(removedservices==null)
			removedservices = new HashSet<IService>();
		return removedservices.add(service);
	}
	
	/**
	 *  Get the delta.
	 *  @return the delta
	 */
	public boolean isDelta()
	{
		return delta;
	}

	/**
	 *  Set the delta.
	 *  @param delta The delta to set
	 */
	public void setDelta(boolean delta)
	{
		this.delta = delta;
	}
	
	/**
	 *  Get the client type.
	 *  @return The client type.
	 */
	public String getClientType()
	{
		return clienttype;
	}

	/**
	 *  Set the client type.
	 *  @param clienttype The client type to set.
	 */
	public void setClientType(String clienttype)
	{
		this.clienttype = clienttype;
	}
	
	/**
	 *  Get the networknames.
	 *  @return The networknames.
	 */
	public String[] getNetworkNames()
	{
		return networknames;
	}

	/**
	 *  Set the network names.
	 *  @param networknames The networknames to set.
	 */
	public void setNetworkNames(String[] networknames)
	{
		this.networknames = networknames;
	}

	/**
	 * Returns the number of elements added to this event.
	 */
	public int size()
	{
		int	size = 0;
		if(addedservices!=null)
			size += addedservices.size();
		if(removedservices!=null)
			size += removedservices.size();
		return size;
	}
	
	/**
	 *  Check if this event is due and should be sent.
	 *  @param True, if the event is due and should be sent.
	 */
	public boolean isDue()
	{
		int size = size();
		// Send event if more than eventlimit events have been collected
		// OR
		// timeout has been reached (AND and at least one event has been collected)
		// The last aspect is not used because lease times are used
		// so an empty event at least renews the lease
		return size>=eventslimit || (System.currentTimeMillis()-timestamp>=timelimit);// && size>0);
	}

	/**
	 *  Get the time until this event is due.
	 *  @return The time until the event is due.
	 */
	public long getTimeUntilDue()
	{
		long wait = timelimit-(System.currentTimeMillis()-timestamp);
		return wait>0? wait: 0;
	}
	
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RegistryEvent(addedservices=" + addedservices + ", removedservices=" + removedservices + ", delta=" + delta + ")";
	}
	
//	/**
//	 *  Get the addedservices.
//	 *  @return The addedservices
//	 */
//	public Set<IServiceIdentifier> getAddedServices()
//	{
//		return addedservices;
//	}
//
//	/**
//	 *  Set the addedservices.
//	 *  @param addedservices The addedservices to set
//	 */
//	public void setAddedServices(Set<IServiceIdentifier> addedservices)
//	{
//		this.addedservices = addedservices;
//	}
//	
//	/**
//	 *  Get the removedservices.
//	 *  @return The removedservices
//	 */
//	public Set<IServiceIdentifier> getRemovedServices()
//	{
//		return removedservices;
//	}
//
//	/**
//	 *  Set the removedservices.
//	 *  @param removedservices The removedservices to set
//	 */
//	public void setRemovedServices(Set<IServiceIdentifier> removedservices)
//	{
//		this.removedservices = removedservices;
//	}
}
