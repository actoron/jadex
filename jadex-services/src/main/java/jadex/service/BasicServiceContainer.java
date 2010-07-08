package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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

	//-------- constructors --------

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
	public IFuture getServices(Class type, IVisitDecider decider)
	{
		Future ret = new Future();
		
		if(decider.searchNode(null, this, false) && services!=null)
		{
			Collection sers = (Collection)services.get(type);
			ret.setResult(sers==null? Collections.EMPTY_SET: sers);
		}
		else
		{
			ret.setResult(null);
		}

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
	public IFuture getService(Class type,  IVisitDecider decider)
	{
		Future ret = new Future();
		
		if(decider.searchNode(null, this, false) && services!=null)
		{
			Collection sers = (Collection)services.get(type);
			ret.setResult(sers!=null && sers.size()>0? sers.iterator().next(): null);
		}
		else
		{
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public IFuture getServicesTypes(IVisitDecider decider)
	{
		Future ret = new Future();
		
		ret.setResult(decider.searchNode(null, this, false) && services!=null? new Class[0]: 
			(Class[])services.keySet().toArray(new Class[services.size()]));
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a service to the platform.
	 *  Does NOT start the service automatically.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture addService(Class type, Object service)
	{
		final Future ret = new Future();
		
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		Collection tmp = services!=null? (Collection)services.get(type): null;
		if(tmp == null)
		{
			tmp = new ArrayList();
			if(services==null)
				services = new LinkedHashMap();
			services.put(type, tmp);
		}
		tmp.add(service);
		
		ret.setResult(null);
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture removeService(Class type, Object service)
	{
		final Future ret = new Future();
		
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

		if(tmp.size() == 0)
			services.remove(type);
		
		if(!removed)
			ret.setException(new RuntimeException("Service not found: " + service));
		else
			ret.setResult(null);
		
		return ret;
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
			CounterResultListener lis = new CounterResultListener(services.size())
			{
				public void finalResultAvailable(Object source, Object result)
				{
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			};
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
//		Thread.dumpStack();
//		System.out.println("shutdown called: "+getName());
		final Future ret = new Future();
		
		// Stop the services.
		if(services!=null && services.size()>0)
		{
			List allservices = new ArrayList();
			for(Iterator it=services.values().iterator(); it.hasNext(); )
			{
				allservices.addAll((Collection)it.next());
			}
			
//			System.out.println("all services: "+allservices);
			shutdownServices(allservices, allservices.size()-1).addResultListener(new DelegationResultListener(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown services loop.
	 */
	protected IFuture shutdownServices(final List sers, final int i)
	{
		final Future ret = new Future();
		
		if(i>=0)
		{
			if(sers.get(i) instanceof IService)
			{
//				System.out.println("shutdown: "+i+" "+sers.get(i));
				
				((IService)sers.get(i)).shutdownService().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
//						System.out.println("shutdown finished: "+i+" "+sers.get(i));
						shutdownServices(sers, i-1).addResultListener(new DelegationResultListener(ret));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						shutdownServices(sers, i-1).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
			else
			{
				shutdownServices(sers, i-1).addResultListener(new DelegationResultListener(ret));
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
}
