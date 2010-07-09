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

/**
 *  A service container is a simple infrastructure for a collection of
 *  services. It allows for starting/shutdowning the container and fetching
 *  service by their type/name.
 */
public class BasicServiceContainer implements  IServiceContainer
{	
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map services;
	
	/** The platform name. */
	protected Object id;

	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public BasicServiceContainer(Object id)
	{
		this.id = id;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get all services of a typ.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
	{
		return manager.searchServices(this, decider, selector, services);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		return new Future(null);
	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture	getChildren()
	{
		return new Future(null);
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object	getId()
	{
		return id;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a service to the platform.
	 *  Does NOT start the service automatically.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param id The name.
	 *  @param service The service.
	 */
	public void addService(Class type, Object service)
	{
//		final Future ret = new Future();
		
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		synchronized(this)
		{
			Collection tmp = services!=null? (Collection)services.get(type): null;
			if(tmp == null)
			{
				tmp = Collections.synchronizedList(new ArrayList());
				if(services==null)
					services = Collections.synchronizedMap(new LinkedHashMap());
				services.put(type, tmp);
			}
			tmp.add(service);
		}
		
//		ret.setResult(null);
//		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param id The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, Object service)
	{
//		final Future ret = new Future();
		
		//		System.out.println("Removing service: " + type + " " + service);
		synchronized(this)
		{
			Collection tmp = services!=null? (Collection)services.get(type): null;
			if(tmp == null || (service != null && !tmp.contains(service)))
				throw new RuntimeException("Service not found: " + service);
	
			boolean removed = false;
			if(service == null && tmp.size() == 1)
			{
				service = tmp.iterator().next();
				tmp.remove(service);
				removed = true;
			}
			else
			{
				for(Iterator it=tmp.iterator(); !removed && it.hasNext(); )
				{
					Object key = it.next();
					if(tmp.equals(service))
					{
						service	= key;
						tmp.remove(key);
						removed = true;
					}
				}
			}
	
			if(!removed)
				throw new RuntimeException("Service not found: " + service);

			if(tmp.isEmpty())
				services.remove(type);
		}
		
		if(service instanceof IService)
			((IService)service).shutdownService();

	
//		if(!removed)
//			ret.setException(new RuntimeException("Service not found: " + service));
//		else
//			ret.setResult(null);
//		
//		return ret;
	}
	
	//-------- internal methods --------
	
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
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "BasicServiceContainer(name="+getId()+")";
	}
	
}
