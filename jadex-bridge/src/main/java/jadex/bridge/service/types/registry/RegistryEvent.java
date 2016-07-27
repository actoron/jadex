package jadex.bridge.service.types.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;

/**
 * 
 */
public class RegistryEvent implements IRegistryEvent
{
	/** The added services. */
//	protected Set<IService> addedservices;
	protected Set<IServiceIdentifier> addedservices;
	
	/** The removed services. */
	protected Set<IServiceIdentifier> removedservices;

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
	public RegistryEvent(Set<IService> addedservices, Set<IService> removedservices)
	{
		setAddedServices(addedservices);
		setRemovedServices(removedservices);
	}
	
	/**
	 *  Set the added services.
	 */
	protected void setAddedServices(Collection<IService> services)
	{
		if(services!=null && services.size()>0)
		{
			addedservices = new HashSet<IServiceIdentifier>();
			for(IService ser: services)
			{
				addedservices.add(ser.getServiceIdentifier());
			}
		}
	}
	
	/**
	 *  Set the removed services.
	 */
	protected void setRemovedServices(Collection<IService> services)
	{
		if(services!=null && services.size()>0)
		{
			removedservices = new HashSet<IServiceIdentifier>();
			for(IService ser: services)
			{
				removedservices.add(ser.getServiceIdentifier());
			}
		}
	}

	/**
	 *  Get the addedservices.
	 *  @return The addedservices
	 */
	public Set<IServiceIdentifier> getAddedServices()
	{
		return addedservices;
	}

	/**
	 *  Set the addedservices.
	 *  @param addedservices The addedservices to set
	 */
	public void setAddedServices(Set<IServiceIdentifier> addedservices)
	{
		this.addedservices = addedservices;
	}
	
	/**
	 *  Get the removedservices.
	 *  @return The removedservices
	 */
	public Set<IServiceIdentifier> getRemovedServices()
	{
		return removedservices;
	}

	/**
	 *  Set the removedservices.
	 *  @param removedservices The removedservices to set
	 */
	public void setRemovedServices(Set<IServiceIdentifier> removedservices)
	{
		this.removedservices = removedservices;
	}
}
