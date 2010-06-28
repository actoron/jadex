package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Basic service provider implementation.
 *  Allows for fetching services by type.
 */
public class BasicServiceProvider implements IServiceProvider
{
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map services;

	//-------- interface methods --------
		
	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 */
	public IFuture getServices(Class type)
	{
		Future ret = new Future();
		
		Collection	coll = null;
		Map tmp = getServiceMap(type);
		if(tmp != null)
			coll = tmp.values();
		else
			coll = Collections.EMPTY_SET;
//			throw new RuntimeException("No services found of type: " + type);

		ret.setResult(coll);
		return ret;
	}

	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public IFuture getService(Class type, String name)
	{
		Future ret = new Future();
		
		Object res	= null;
		Map tmp = getServiceMap(type);
		if(tmp != null)
			res = tmp.get(name);
//		if(ret == null)
//			throw new RuntimeException("Service not found");
		
		ret.setResult(res);
		return ret;
	}

	/**
	 *  Get the first declared platform service of a given type.
	 *  @param type The type.
	 *  @return The corresponding platform service.
	 */
	public IFuture getService(Class type)
	{
		Future ret = new Future();
		
		Object res	= null;
		Map tmp = getServiceMap(type);
		if(tmp != null && !tmp.isEmpty())
			res = tmp.values().iterator().next();
//		if(ret == null)
//			throw new RuntimeException("Service not found");
		
		ret.setResult(res);
		return ret;
	}
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public IFuture getServicesTypes()
	{
		Future ret = new Future();
		ret.setResult(services==null? new Class[0]: (Class[])services.keySet().toArray(new Class[services.size()]));
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void addService(Class type, String name, Object service)
	{
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		Map tmp = getServiceMap(type);
		if(tmp == null)
		{
			tmp = new HashMap();
			if(services==null)
				services = new HashMap();
			services.put(type, tmp);
		}
		Object old = tmp.put(name, service);
		if(old instanceof IService)
			((IService)old).shutdownService();
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, Object service)
	{
		//		System.out.println("Removing service: " + type + " " + service);
		Map tmp = getServiceMap(type);
		if(tmp == null || (service != null && !tmp.containsValue(service)))
			throw new RuntimeException("Service not found: " + service);

		boolean removed = false;
		if(service == null && tmp.size() == 1)
		{
			IService rem = (IService)tmp.remove(tmp.keySet().iterator().next());
			rem.shutdownService();
			removed = true;
		}
		else
		{
			for(Iterator it = tmp.keySet().iterator(); it.hasNext();)
			{
				Object key = it.next();
				if(tmp.get(key).equals(service))
				{
					Object rem = tmp.remove(key);
					if(rem instanceof IService)
						((IService)rem).shutdownService();
					removed = true;
				}
			}
		}

		if(!removed)
			throw new RuntimeException("Service not found: " + service);

		if(tmp.size() == 0)
			services.remove(type);
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 */
	protected Map getServiceMap(Class type)
	{
		return services==null? null: (Map)services.get(type);
	}
}
