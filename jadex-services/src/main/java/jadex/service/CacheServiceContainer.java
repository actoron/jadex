package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.IResultListener;

import java.util.Collections;
import java.util.Map;

/**
 *  Service container that uses caching for fast service access.
 */
public class CacheServiceContainer	implements IServiceContainer
{
	//-------- attributes --------
	
	/** The original service container. */
	protected IServiceContainer container;
	
	/** The LRU storing the last searches. */
	protected Map	cache;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public CacheServiceContainer(IServiceContainer container)
	{
		this.container = container;
		this.cache	= Collections.synchronizedMap(new LRU(25));
	}
	
	//-------- methods --------

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
	{
		final Future ret = new Future();
		
		final Tuple key = manager.getCacheKey()!=null && decider.getCacheKey()!=null && selector.getCacheKey()!=null
			? new Tuple(manager.getCacheKey(), decider.getCacheKey(), selector.getCacheKey()) : null;
		
		if(key!=null && cache.containsKey(key))
		{
			Object res = cache.get(key);
			ret.setResult(res);
		}
		else
		{
			container.getServices(manager, decider, selector).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					if(key!=null)
						cache.put(key, result);
					ret.setResult(result);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture getParent()
	{
		return container.getParent();
	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture getChildren()
	{
		return container.getChildren();
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object getId()
	{
		return container.getId();
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture start()
	{
		return container.start();
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture shutdown()
	{
		return container.shutdown();
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void addService(Class type, Object service)
	{
		container.addService(type, service);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, Object service)
	{
		container.removeService(type, service);
	}
}
