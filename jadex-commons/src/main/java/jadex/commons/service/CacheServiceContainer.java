package jadex.commons.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateFuture;
import jadex.commons.Tuple;
import jadex.commons.collection.Cache;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.clock.IClockService;

import java.util.Collection;
import java.util.Iterator;

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
	
	/** Flag if cache is turned on. */
	protected boolean cacheon = true;
	
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
	public IIntermediateFuture getServices(final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		final IntermediateFuture ret = new IntermediateFuture();
		
//		final Tuple key = manager.getCacheKey()!=null && decider.getCacheKey()!=null && selector.getCacheKey()!=null
//			? new Tuple(manager.getCacheKey(), decider.getCacheKey(), selector.getCacheKey()) : null;

		final Tuple key = decider.getCacheKey()!=null && selector.getCacheKey()!=null
			? new Tuple(decider.getCacheKey(), selector.getCacheKey()) : null;
			
		Object data = null;
		// Todo: cast hack??? While no clock service found (during init) search without cache.
		final long now = clock!=null && ((IInternalService)clock).isValid()? clock.getTime(): -1;
		
		if(cacheon && !manager.isForcedSearch())
		{
			synchronized(cache)
			{
				// todo: currently services of unfinished containers can be searched
				// should be strict and a container should exposed only when running.
				
				// In case the clock if not available caching will not be used
				// till it is available.
				if(key!=null && cache.containsKey(key))
				{	
					data = cache.get(key, now);
					if(data!=null)
					{
						// Replace non-expireable entry
						if(!cache.canExpire(key))
						{
							cache.put(key, data, now);
						}
					}
					
//					if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Clock")!=-1)
//					{	
//						System.out.println("hit: "+selector+" "+getId());
//					}
					
//					if(data!=null && data.getClass().getName().indexOf("ComponentManagement")!=-1)
//					{
//						System.out.println("hit: "+data+" "+getId());
//					}
					
					if(data instanceof IInternalService)
					{
						if(!((IInternalService)data).isValid())
						{
							cache.remove(key);
							data = null;
						}
						else
						{
							if(!ret.getIntermediateResults().contains(data))
							{
//								if(data.getClass().getName().indexOf("Shop")!=-1)
//									System.out.println("cache add: "+data+" to: "+results);

								ret.addIntermediateResult(data);
							}
						}
						ret.setFinished();
					}
					else if(data instanceof Collection)
					{
						Collection coll = (Collection)data;
						// Check if all results are still ok.
						for(Iterator it=coll.iterator(); it.hasNext(); )
						{
							Object	next	= it.next();
							if(next instanceof IInternalService)
							{
								if(!((IInternalService)next).isValid())
								{
									// if one is invalid whole result is invalid
									cache.remove(key);
									data = null;
								}
							}
						}
						if(data!=null)
						{
							for(Iterator it=coll.iterator(); it.hasNext(); )
							{
								Object	next	= it.next();
								if(!ret.getIntermediateResults().contains(next))
								{
//									if(data.getClass().getName().indexOf("Shop")!=-1)
//										System.out.println("cache add: "+data+" to: "+results);

									ret.addIntermediateResult(next);
								}
							}
						}
						ret.setFinished();
					}
					else if(data!=null)
					{
						ret.setException(new RuntimeException("Unknown service type: "+data));
						return ret;
					}
				}
				else
				{
//					if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Clock")!=-1)
//					{
//						System.out.println("no hit: "+selector+" "+getId()+" "+now);
//					}
				}
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
				public void resultAvailable(Object result)
				{	
					if(key!=null && result!=null)
					{
						synchronized(cache)
						{
//							if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Clock")!=-1)
//							{
//								System.out.println("putting: "+getId()+" "+result);
//							}
							// Put result in cache, even if service may not (yet) be valid.
							// May lead to cache hit and still service put in cache again.
							cache.put(key, result, now);
						}
					}
//					if(result==null)
//						System.out.println("found null: "+key);
					ret.setResult(result);
				}
				
				public void exceptionOccurred(Exception exception)
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
		final Future ret = new Future();
		
//		System.out.println("search clock: "+getId());
		SServiceProvider.getServiceUpwards(this, IClockService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				clock = (IClockService)result;
//				System.out.println("Has clock: "+getId()+" "+clock);
				
				// Services may need other services and thus need to be able to search
				// the container.
				container.start().addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Exception exception)
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
	public IFuture addService(IInternalService service)
	{
		return container.addService(service);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public IFuture removeService(IServiceIdentifier sid)
	{
		return container.removeService(sid);
	}

	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture getRequiredService(RequiredServiceInfo info, boolean rebind)
	{
		return container.getRequiredService(info, rebind);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IIntermediateFuture getRequiredServices(RequiredServiceInfo info, boolean rebind)
	{
		return container.getRequiredServices(info, rebind);
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CacheServiceContainer(name="+getId()+", container="+container+")";
	}
}
