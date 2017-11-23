package jadex.bridge.service.types.registry;

import java.util.Set;

import jadex.bridge.service.IService;

/**
 *  Interface for registry events.
 */
public interface IRegistryEvent
{
	public static final String CLIENTTYPE_CLIENT = "client";
	public static final String CLIENTTYPE_SUPERPEER_LEVEL0 = "superpeer_0";
	public static final String CLIENTTYPE_SUPERPEER_LEVEL1 = "superpeer_1";
	
	/**
	 *  Get the addedservices.
	 *  @return The addedservices
	 */
	public Set<IService> getAddedServices();
//	public Set<IServiceIdentifier> getAddedServices();

	/**
	 *  Get the removedservices.
	 *  @return The removedservices
	 */
	public Set<IService> getRemovedServices();
//	public Set<IServiceIdentifier> getAddedServices();
	
	/**
	 *  Flag if event contains full registry content or only partial delta.
	 */
	public boolean isDelta();
	
	/**
	 *  Get the size of the event in terms of the number of subevents.
	 *  @return The number of contained changes.
	 */
	public int size();
	
	
	
	/**
	 *  Get the client type.
	 *  @return The client type.
	 */
	public String getClientType();
	
	/**
	 *  Get the networknames.
	 *  @return The networknames.
	 */
	public String[] getNetworkNames();
}
