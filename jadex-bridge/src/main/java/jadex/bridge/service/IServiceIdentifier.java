package jadex.bridge.service;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;


/**
 *  Interface for service identifier.
 */
public interface IServiceIdentifier
{
	/**
	 *  Get the service provider identifier.
	 *  @return The provider id.
	 */
	public IComponentIdentifier	getProviderId();
	
	/**
	 *  Get the service name.
	 *  @return The service name.
	 */
	public String getServiceName();

	/**
	 *  Get the service type name.
	 *  @return The service type name.
	 */
	public ClassInfo getServiceType();

	/** 
	 *  Get the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier();
}
