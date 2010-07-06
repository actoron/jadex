package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CounterListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  A service container is a simple infrastructure for a collection of
 *  services. It allows for starting/shutdowning the container and fetching
 *  service by their type/name.
 *  
 *  The configuration of services is not handled here but can be done
 *  e.g. in subclasses like the PropertyServiceContainer.
 */
public class BasicServiceContainer implements  IServiceProvider, IServiceContainer
{	
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map services;
	
	/** The platform name. */
	protected String name;

	//-------- aconstructors --------

	/**
	 *  Create a new service container.
	 */
	public BasicServiceContainer(String name)
	{
		this.name = name;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 */
	public IFuture getServices(Class type)
	{
		final Future ret = new Future();
		
		getServicesOfType(type, Collections.synchronizedSet(new HashSet())).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ret.setResult(result==null? Collections.EMPTY_SET: result);
			}
		});

		return ret;
	}

	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 * /
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
	}*/

	
	/**
	 *  Get the first declared platform service of a given type.
	 *  @param type The type.
	 *  @return The corresponding platform service.
	 */
	public IFuture getService(Class type)
	{
		final Future ret = new Future();
		
		getServiceOfType(type, Collections.synchronizedSet(new HashSet())).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
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
//	public void addService(Class type, String name, Object service)
	public void addService(Class type, Object service)
	{
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		Collection tmp = services!=null? (Collection)services.get(type): null;
		if(tmp == null)
		{
			tmp = new ArrayList();
			if(services==null)
				services = new HashMap();
			services.put(type, tmp);
		}
		tmp.add(service);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, Object service)
	{
		//		System.out.println("Removing service: " + type + " " + service);
		Collection tmp = services!=null? (Collection)services.get(type): null;
		if(tmp == null || (service != null && !tmp.contains(service)))
			throw new RuntimeException("Service not found: " + service);

		boolean removed = false;
		if(service == null && tmp.size() == 1)
		{
			IService rem = (IService)tmp.iterator().next();
			tmp.remove(rem);
			rem.shutdownService();
			removed = true;
		}
		else
		{
			for(Iterator it = tmp.iterator(); it.hasNext();)
			{
				Object key = it.next();
				if(tmp.equals(service))
				{
					tmp.remove(key);
					if(key instanceof IService)
						((IService)key).shutdownService();
					removed = true;
				}
			}
		}

		if(!removed)
			throw new RuntimeException("Service not found: " + service);

		if(tmp.size() == 0)
			services.remove(type);
	}
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 */
	public IFuture getServiceOfType(Class type, Set visited)
	{
		Future ret = new Future();
		
		if((visited==null || !visited.contains(getName())) && services!=null)
		{
			Collection sers = (Collection)services.get(type);
			ret.setResult(sers!=null && sers.size()>0? sers.iterator().next(): null);
		}
		else
		{
			ret.setResult(null);
		}

		if(visited!=null && !visited.contains(getName()))
		{
			visited.add(getName());
		}

		return ret;
	}
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 */
	public IFuture getServicesOfType(Class type, Set visited)
	{
		Future ret = new Future();
		
		if((visited==null || !visited.contains(getName())) && services!=null)
		{
			ret.setResult(services.get(type));
		}
		else
		{
			ret.setResult(null);
		}

		if(visited!=null && !visited.contains(getName()))
		{
			visited.add(getName());
		}

		return ret;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 * /
	protected Map getServiceMap(Class type)
	{
		return services==null? null: (Map)services.get(type);
	}*/
	
	
	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		
		// Start the services.
		if(services!=null && services.size()>0)
		{
			// Start notifies the future when all services have been started.
			CounterListener lis = new CounterListener(services.size(), new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					ret.setResult(null);
				}
			});
			for(Iterator it=services.keySet().iterator(); it.hasNext(); )
			{
				Object key = it.next();
				Collection tmp = (Collection)services.get(key);
				if(tmp!=null)
				{
					for(Iterator it2=tmp.iterator(); it2.hasNext(); )
					{
						IService service = (IService)it2.next();
//						IService service = (IService)tmp.get(key2);
						service.startService().addResultListener(lis);
					}
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 */
	public IFuture shutdown()
	{
		final Future ret = new Future();
		
		// Stop the services.
		if(services!=null && services.size()>0)
		{
			CounterListener lis = new CounterListener(services.size(), new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					ret.setResult(null);
				}
			});
			for(Iterator it = services.keySet().iterator(); it.hasNext();)
			{
				Object key = it.next();
				Collection tmp = (Collection)services.get(key);
				if(tmp != null)
				{
					for(Iterator it2 = tmp.iterator(); it2.hasNext();)
					{
						IService service = (IService)it2.next();
						service.shutdownService().addResultListener(lis);
					}
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
}
