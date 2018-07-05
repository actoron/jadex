package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.service.RequiredServiceInfo;

/**
 *  Interface for internal service access methods.
 */
public interface IInternalRequiredServicesFeature
{
	/**
	 *  Get the required service info for a name.
	 *  @param name	The required service name.
	 */
	// Hack!!! used by multi invoker?
	public RequiredServiceInfo	getServiceInfo(String name);
	
	/**
	 *  Get a service raw (i.e. w/o required proxy).
	 */
	// Hack???
	public <T>	T	getRawService(Class<T> type);

	/**
	 *  Get a service raw (i.e. w/o required proxy).
	 */
	// Hack???
	public <T>	Collection<T>	getRawServices(Class<T> type);

	//-------- all declared services (e.g. JCC component details) --------
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getServiceInfos();
}
