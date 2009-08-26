package jadex.wfms;

import java.util.HashMap;
import java.util.Map;


/**
 *  Basic wfms implementation.
 */
public class BasicWfms implements IWfms
{
	//-------- attributes --------
	
	/** Wfms services */
	protected Map services;
	
	//-------- constructors --------
	
	/**
	 *  Create a new wfms.
	 */ 
	public BasicWfms()
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Add a service to the Wfms.
	 *  @param type type of service
	 *  @param service The service.
	 */
	public void addService(Class type, Object service)
	{
		if(services==null)
			services = new HashMap();
		services.put(type, service);
	}
	
	/**
	 *  Removes a service from the Wfms.
	 *  @param type type of service
	 */
	public void removeService(Class type)
	{
		if(services!=null)
			services.remove(type);
	}
	
	/**
	 *  Get a Wfms-service.
	 *  @param type The service interface/type.
	 *  @return The corresponding wfms-service.
	 */
	public Object getService(Class type)
	{
		return services==null? null: services.get(type);
	}
}
