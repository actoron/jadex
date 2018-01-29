package jadex.bridge.service.types.registry;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.IService;

/**
 *  Registry event for notifications from the registry.
 */
public class RegistryEvent extends ARegistryEvent
{
	/** The service entry lease time. */
	// Set this variable to another value in your app NOT HERE
	public static long LEASE_TIME = 10000;
	
	/** The added services. */
	protected Set<IService> addedservices;
//	protected Set<IServiceIdentifier> addedservices;
	
	/** The removed services. */
	protected Set<IService> removedservices;
//	protected Set<IServiceIdentifier> removedservices;

	/** Flag if is delta (or full) registry content. */
	protected boolean delta;
	
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
		this(null, null, 1000, LEASE_TIME, delta, null);
	}
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent(boolean delta, String clienttype)
	{
		this(null, null, 1000, LEASE_TIME, delta, clienttype);
	}
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent(boolean delta, long timelimit)
	{
		this(null, null, 1000, timelimit, delta, null);
	}
	
	/**
	 *  Create a new registry event.
	 *  @param addedservices The added services.
	 *  @param removedservices The removed services.
	 */
	public RegistryEvent(Set<IService> addedservices, Set<IService> removedservices, int eventslimit, long timelimit, boolean delta, String clienttype)
	{
		super(eventslimit, timelimit);
		this.timestamp = System.currentTimeMillis();
		this.delta = delta;
		this.clienttype = clienttype==null? CLIENTTYPE_CLIENT: clienttype;
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
		if(fini)
		{
			System.out.println("thread: "+Thread.currentThread());
			Thread.dumpStack();
		}
		
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		int added = addedservices!=null? addedservices.size(): 0;
		int rem = removedservices!=null? removedservices.size(): 0;
//		return "RegistryEvent(addedservices=" + (addedservices!=null? addedservices.size(): 0)  + ", removedservices=" + (removedservices!=null? removedservices.size(): 0) + ", delta=" + delta + ")";
		return "RegistryEvent(id="+id+", addedservices="+ added +" "+ addedservices  + ", removedservices=" + rem +" "+ removedservices + ", delta=" + delta + ")";
	}
	
	public boolean fini = false;
	
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
