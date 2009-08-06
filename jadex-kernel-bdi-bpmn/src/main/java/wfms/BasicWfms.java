package wfms;

import jadex.bridge.IPlatformService;

import java.util.HashMap;
import java.util.Map;

/**
 *  Basic Wfms implementation.
 */
public class BasicWfms implements IWfms
{
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
	public synchronized void addService(Class type, IPlatformService service)
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
	
	public synchronized Object getService(Class type)
	{
		return services.get(type);
	}
}
