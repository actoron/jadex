package jadex.commons.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DelegationResultListener;

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
	
	/** True, if the container is started. */
	protected boolean	started;

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
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector, Collection results)
	{
		return manager.searchServices(this, decider, selector, services!=null ? services : Collections.EMPTY_MAP, results);
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
	 *  Add a service to the container.
	 *  @param id The name.
	 *  @param service The service.
	 */
	public IFuture	addService(BasicService service)
	{
		final Future ret = new Future();
		
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		synchronized(this)
		{
			Collection tmp = services!=null? (Collection)services.get(service.getServiceIdentifier().getServiceType()): null;
			if(tmp == null)
			{
				tmp = Collections.synchronizedList(new ArrayList());
				if(services==null)
					services = Collections.synchronizedMap(new LinkedHashMap());
				services.put(service.getServiceIdentifier().getServiceType(), tmp);
			}
			tmp.add(service);
			
			if(started)
			{
				service.startService().addResultListener(new DelegationResultListener(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param id The name.
	 *  @param service The service.
	 */
	public IFuture removeService(BasicService service)
	{
		Future ret = new Future();
		
		//		System.out.println("Removing service: " + type + " " + service);
		synchronized(this)
		{
			Collection tmp = services!=null? (Collection)services.get(service.getServiceIdentifier().getServiceType()): null;
			if(tmp == null || (service != null && !tmp.contains(service)))
				throw new RuntimeException("Service not found: " + service);
	
			boolean removed = false;
			for(Iterator it=tmp.iterator(); !removed && it.hasNext(); )
			{
				Object key = it.next();
				if(tmp.equals(service))
				{
					service	= (BasicService)key;
					tmp.remove(key);
					if(started)
					{
						service.shutdownService().addResultListener(new DelegationResultListener(ret));
					}
					else
					{
						ret.setResult(null);
					}
						
					removed = true;
				}
			}
	
			if(!removed)
				throw new RuntimeException("Service not found: " + service);

			if(tmp.isEmpty())
				services.remove(service.getServiceIdentifier().getServiceType());
		}
		
		return ret;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Start the service.
	 */
	public IFuture start()
	{
		assert	!started;
		started	= true;
		
		final Future ret = new Future();
		
		// Start the services.
		if(services!=null && services.size()>0)
		{
			List allservices = new ArrayList();
			for(Iterator it=services.values().iterator(); it.hasNext(); )
			{
				allservices.addAll((Collection)it.next());
			}
			CounterResultListener	crl	= new CounterResultListener(allservices.size())
			{
				public void finalResultAvailable(Object source, Object result)
				{
					ret.setResult(result);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			};
			for(Iterator it=allservices.iterator(); it.hasNext(); )
			{
				((BasicService)it.next()).startService().addResultListener(crl);
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
		assert started;
		
		started	= false;
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
			CounterResultListener	crl	= new CounterResultListener(allservices.size())
			{
				public void finalResultAvailable(Object source, Object result)
				{
					ret.setResult(result);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			};
			for(Iterator it=allservices.iterator(); it.hasNext(); )
			{
				((BasicService)it.next()).shutdownService().addResultListener(crl);
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

	/** 
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return ((id == null) ? 0 : id.hashCode());
	}

	/** 
	 *  Test if the object eqquals another one.
	 *  @param obj The object.
	 *  @return true, if both are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof IServiceContainer && ((IServiceContainer)obj).getId().equals(getId());
	}
	
	
	
}
