package jadex.bridge.service;

import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  A service container is a simple infrastructure for a collection of
 *  services. It allows for starting/shutdowning the container and fetching
 *  service by their type/name.
 */
public abstract class BasicServiceContainer implements  IServiceContainer
{	
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map services;
	
	/** The platform name. */
	protected Object id;
	
	/** True, if the container is started. */
	protected boolean started;

	
	/** The service fetch method table (name -> fetcher). */
	protected Map reqservicefetchers;
	
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
	public IIntermediateFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
	{
		return manager.searchServices(this, decider, selector, services!=null ? services : Collections.EMPTY_MAP);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public abstract IFuture	getParent();
//	{
//		return new Future(null);
//	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public abstract IFuture	getChildren();
//	{
//		return new Future(null);
//	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object	getId()
	{
		return id;
	}
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return "basic"; 
	}

	//-------- methods --------
	
	/**
	 *  Add a service to the container.
	 *  @param id The name.
	 *  @param service The service.
	 */
	public IFuture	addService(IInternalService service)
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
	public IFuture removeService(IServiceIdentifier sid)
	{
		Future ret = new Future();
		
		if(sid==null)
		{
			ret.setException(new IllegalArgumentException("Service identifier nulls."));
			return ret;
		}
		
		// System.out.println("Removing service: " + type + " " + service);
		synchronized(this)
		{
			Collection tmp = services!=null? (Collection)services.get(sid.getServiceType()): null;
			
			IInternalService service = null;
			if(tmp!=null)
			{
				for(Iterator it=tmp.iterator(); it.hasNext() && service==null; )
				{
					IInternalService tst = (IInternalService)it.next();
					if(tst.getServiceIdentifier().equals(sid))
					{
						service = tst;
						tmp.remove(service);
						if(started)
						{
							service.shutdownService().addResultListener(new DelegationResultListener(ret));
						}
						else
						{
							ret.setResult(null);
						}
					}
				}
			}
			if(service==null)
			{
				ret.setException(new IllegalArgumentException("Service not found: "+sid));
				return ret;
			}
	
			if(tmp.isEmpty())
				services.remove(sid.getServiceType());
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
			CounterResultListener	crl	= new CounterResultListener(allservices.size(), new DelegationResultListener(ret));
			for(Iterator it=allservices.iterator(); it.hasNext(); )
			{
				((IInternalService)it.next()).startService().addResultListener(crl);
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
			final List allservices = new ArrayList();
			for(Iterator it=services.values().iterator(); it.hasNext(); )
			{
				allservices.addAll((Collection)it.next());
			}
			
			// Shutdown services in reverse order as later services might depend on earlier ones.
			IInternalService	service	= (IInternalService)allservices.remove(allservices.size()-1);
			service.shutdownService().addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					if(!allservices.isEmpty())
					{
						IInternalService	service	= (IInternalService)allservices.remove(allservices.size()-1);
						service.shutdownService().addResultListener(this);						
					}
					else
					{
						super.customResultAvailable(result);
					}
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		return getRequiredService(info, binding, false);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		return getRequiredServices(info, binding, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		if(info==null)
		{
			Future ret = new Future();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		return fetcher.getService(info, binding, this, rebind);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public IIntermediateFuture getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		if(info==null)
		{
			IntermediateFuture ret = new IntermediateFuture();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		return fetcher.getServices(info, binding, this, rebind);
	}
	
	/**
	 *  Get a required service fetcher.
	 *  @param name The required service name.
	 *  @return The service fetcher.
	 */
	protected IRequiredServiceFetcher getRequiredServiceFetcher(String name)
	{
		IRequiredServiceFetcher ret = reqservicefetchers!=null? 
			(IRequiredServiceFetcher)reqservicefetchers.get(name): null;
		if(ret==null)
		{
			ret = createServiceFetcher(name);
			if(reqservicefetchers==null)
				reqservicefetchers = new HashMap();
			reqservicefetchers.put(name, ret);
		}
		return ret;
	}
		
	/**
	 *  Create a service fetcher.
	 */
	public abstract IRequiredServiceFetcher createServiceFetcher(String name);
	
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
