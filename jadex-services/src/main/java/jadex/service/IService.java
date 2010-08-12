package jadex.service;

import java.util.Map;


/**
 *  The interface for platform services.
 */
public interface IService
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
}
