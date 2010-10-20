package jadex.commons.service;

import jadex.commons.IFuture;
import jadex.commons.IProxyable;

import java.util.Map;


/**
 *  The interface for platform services.
 */
public interface IService extends IProxyable
{
	//-------- constants --------
	
	/** Empty service array. */
	public static IService[] EMPTY_SERVICES = new IService[0];
	
	//-------- methods --------

	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier();
	
	/**
	 *  Get the map of properties (considered as constant)..
	 *  @return The service property map (if any).
	 */
	public Map getPropertyMap();
	
	/**
	 *  Get a future that signals when the service is started.
	 *  @return A future that signals when the service has been started.
	 */
	public IFuture signalStarted();
}
