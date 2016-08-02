package jadex.bridge.service.types.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;

/**
 *  Registry event for notifications from the registry.
 */
public class RegistryEvent implements IRegistryEvent
{
	/** The added services. */
	protected Map<ClassInfo, Set<IService>> addedservices;
//	protected Set<IServiceIdentifier> addedservices;
	
	/** The removed services. */
	protected Map<ClassInfo, Set<IService>> removedservices;
//	protected Set<IServiceIdentifier> removedservices;

	/** The number of events that must have occured before a remote message is sent. */
	protected int eventslimit;
	
	/** The timestamp of the first event (change). */
	protected long timestamp; 
	
	/** The time limit. */
	protected long timelimit;
	
	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent()
	{
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 *  Create a new registry event.
	 *  @param addedservices The added services.
	 *  @param removedservices The removed services.
	 */
	public RegistryEvent(Map<ClassInfo, Set<IService>> addedservices, Map<ClassInfo, Set<IService>> removedservices, int eventslimit, long timelimit)
	{
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
		this.timestamp = System.currentTimeMillis();
		setAddedServices(addedservices);
		setRemovedServices(removedservices);
	}
	
	/**
	 *  Get the addedservices.
	 *  @return the addedservices
	 */
	public Map<ClassInfo, Set<IService>> getAddedServices()
	{
		return addedservices;
	}
	
	/**
	 *  Set the added services.
	 */
	public void setAddedServices(Map<ClassInfo, Set<IService>> services)
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
	public Map<ClassInfo, Set<IService>> getRemovedServices()
	{
		return removedservices;
	}
	
	/**
	 *  Set the removed services.
	 */
	public void setRemovedServices(Map<ClassInfo, Set<IService>> services)
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
	public boolean addAddedService(ClassInfo type, IService service)
	{
		if(addedservices==null)
			addedservices = new HashMap<ClassInfo, Set<IService>>();
		Set<IService> tmp = addedservices.get(type);
		if(tmp==null)
		{
			tmp = new HashSet<IService>();
			addedservices.put(type, tmp);
		}
		return tmp.add(service);
	}
	
	/**
	 *  Add an added service.
	 *  @return True, if changed.
	 */
	public boolean addRemovedService(ClassInfo type, IService service)
	{
		if(removedservices==null)
			removedservices = new HashMap<ClassInfo, Set<IService>>();
		Set<IService> tmp = removedservices.get(type);
		if(tmp==null)
		{
			tmp = new HashSet<IService>();
			removedservices.put(type, tmp);
		}
		return tmp.add(service);
	}
	
	/**
	 * Returns the number of elements added to this event.
	 */
	public int size()
	{
		int	size = 0;
		if(addedservices!=null)
		{
			for(Map.Entry<ClassInfo, Set<IService>> entry: addedservices.entrySet())
			{
				Collection<IService> coll = entry.getValue();
				size += (coll != null ? coll.size() : 0);
			}
		}
		if(removedservices!=null)
		{
			for(Map.Entry<ClassInfo, Set<IService>> entry: removedservices.entrySet())
			{
				Collection<IService> coll = entry.getValue();
				size += (coll != null ? coll.size() : 0);
			}
		}
		return size;
	}
	
	/**
	 *  Check if this event is due and should be sent.
	 *  @param True, if the event is due and should be sent.
	 */
	public boolean isDue()
	{
		int size = size();
		return size>=eventslimit || (System.currentTimeMillis()-timestamp>timelimit && size>0);
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
