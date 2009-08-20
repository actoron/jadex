package jadex.wfms;

import jadex.bridge.IPlatform;

import java.util.HashMap;
import java.util.Map;


/**
 *  Basic Wfms implementation.
 */
public class BasicWfms implements IWfms
{
	private IPlatform platform;
	
	/** Wfms services */
	private Map services;
	
	/** Initializes the Wfms */
	public BasicWfms()
	{
		services = new HashMap();
	}
	
	/**
	 *  Add a service to the Wfms.
	 *  @param type type of service
	 *  @param service The service.
	 */
	public synchronized void addService(Class type, Object service)
	{
		services.put(type, service);
	}
	
	/**
	 *  Removes a service from the Wfms.
	 *  @param type type of service
	 */
	public synchronized void removeService(Class type)
	{
		services.remove(type);
	}
	
	/**
	 *  Get a Wfms-service.
	 *  @param type The service interface/type.
	 *  @return The corresponding Wfms-service.
	 */
	public synchronized Object getService(Class type)
	{
		return services.get(type);
	}
}
