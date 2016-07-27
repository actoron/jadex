package jadex.bridge.service.types.registry;

import java.util.Collection;
import java.util.HashSet;
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

	/**
	 *  Create a new registry event.
	 */
	public RegistryEvent()
	{
	}
	
	/**
	 *  Create a new registry event.
	 *  @param addedservices The added services.
	 *  @param removedservices The removed services.
	 */
	public RegistryEvent(Map<ClassInfo, Set<IService>> addedservices, Map<ClassInfo, Set<IService>> removedservices)
	{
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
