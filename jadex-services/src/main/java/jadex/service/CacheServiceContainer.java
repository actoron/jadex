package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.commons.collection.Cache;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.clock.IClockService;

import java.util.Collection;

/**
 *  Service container that uses caching for fast service access.
 */
public class CacheServiceContainer	implements IServiceContainer
{
	//-------- attributes --------
	
	/** The original service container. */
	protected IServiceContainer container;
	
	/** The LRU storing the last searches. */
	protected Cache cache;
	
	/** The clock service. */
	protected IClockService clock;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public CacheServiceContainer(IServiceContainer container)
	{
		this(container, 25, -1);
	}
	
	/**
	 *  Create a new service container.
	 */
	public CacheServiceContainer(IServiceContainer container, int max, long ttl)
	{
		this.container = container;
		this.cache	= new Cache(max, ttl);
	}
	
	//-------- methods --------

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture getServices(final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		final Future ret = new Future();
		
		final Tuple key = manager.getCacheKey()!=null && decider.getCacheKey()!=null && selector.getCacheKey()!=null
			? new Tuple(manager.getCacheKey(), decider.getCacheKey(), selector.getCacheKey()) : null;
		
		synchronized(cache)
		{
			Object data = null;
			
			// todo: currently services of unfinished containers can be searched
			// should be strict and a container should exposed only when running.
//			if(clock==null)
//				System.out.println("no clock: "+getId());
			
			final long now = clock!=null && clock.isValid()? clock.getTime(): -1;
			
			// In case the clock if not available caching will not be used
			// till it is available.
			if(now!=-1 && key!=null && cache.containsKey(key))
			{	
				data = cache.get(key, now);
				
				if(data instanceof IService)
				{
					if(!((IService)data).isValid())
					{
						cache.remove(key);
						data = null;
					}
				}
				else if(data instanceof Collection)
				{
					Collection coll = (Collection)data;
					IService[] sers = (IService[])coll.toArray(new IService[((Collection)data).size()]);
					
					// Check if all results are still ok.
					for(int i=0; data!=null && i<sers.length; i++)
					{
						if(!sers[i].isValid())
						{
							// if one is invalid whole result is invalid
							cache.remove(key);
							data = null;
						}
					}
				}
				else if(data!=null)
				{
					ret.setException(new RuntimeException("Unknown service type: "+data));
					return ret;
				}
			}
			
			if(data!=null)
			{
				ret.setResult(data);			
			}
			else
			{
				container.getServices(manager, decider, selector).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{	
						if(key!=null)
						{
							synchronized(cache)
							{
								cache.put(key, result, now);							
							}
						}
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
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
		final Future ret = new Future();
		
//		System.out.println("search clock: "+getId());
		SServiceProvider.getServiceUpwards(container, IClockService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				clock = (IClockService)result;
//				System.out.println("Has clock: "+getId());
				
				// Services may need other services and thus need to be able to search
				// the container.
				container.start().addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
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

	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CacheServiceContainer(name="+getId()+")";
	}
}
