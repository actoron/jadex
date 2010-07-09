package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.IResultListener;

/**
 *  Service container that uses caching for fast service access.
 */
public class CacheServiceContainer
{
	//-------- attributes --------
	
	/** The original service container. */
	protected IServiceContainer container;
	
	/** The LRU storing the last searches. */
	protected LRU cache;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public CacheServiceContainer(IServiceContainer container)
	{
		this.container = container;
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
		
		final Tuple key = new Tuple(manager.getCacheKey(), decider.getCacheKey(), selector.getCacheKey());
		
		Object res = cache.get(key);
		
		if(cache.containsKey(key))
		{
			ret.setResult(res);
		}
		else
		{
			container.getServices(manager, decider, selector).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
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
}
