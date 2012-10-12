package jadex.bridge.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.Tuple;
import jadex.commons.collection.Cache;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

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
			
		
		// Todo: cast hack??? While no clock service found (during init) search without cache.
//		final long now = clock!=null && clock.isValid()? clock.getTime(): -1;
		
		getTime().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Object data = null;
				final long now = ((Long)result).longValue();
				
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
							
//							if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Clock")!=-1)
//							{	
//								System.out.println("hit: "+selector+" "+getId());
//							}
							
//							if(data!=null && data.getClass().getName().indexOf("ComponentManagement")!=-1)
//							{
//								System.out.println("hit: "+data+" "+getId());
//							}
							
							if(data instanceof IService)
							{
								addResult(ret, (IService)data, key).addResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										ret.setFinished();
									}
								});
							}
							else if(data instanceof Collection)
							{
								Collection coll = (Collection)data;
								// Check if all results are still ok.
								CounterResultListener lis = new CounterResultListener(coll.size(), true, new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										ret.setFinished();
									}
								});
								for(Iterator it=coll.iterator(); it.hasNext(); )
								{
									Object	next	= it.next();
									addResult(ret, (IService)next, key).addResultListener(lis);
								}
							}
						}
						else
						{
//							if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Clock")!=-1)
//							{
//								System.out.println("no hit: "+selector+" "+getId()+" "+now);
//							}
						}
					}
				}
				
				if(data!=null)
				{
					ret.setResult((Collection)data);			
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
//									if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Clock")!=-1)
//									{
//										System.out.println("putting: "+getId()+" "+result);
//									}
									// Put result in cache, even if service may not (yet) be valid.
									// May lead to cache hit and still service put in cache again.
									cache.put(key, result, now);
								}
							}
//							if(result==null)
//								System.out.println("found null: "+key);
							ret.setResult((Collection)result);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture addResult(final IntermediateFuture res, final IService service, final Object key)
	{
		final Future ret = new Future();
		((IInternalService)service).isValid().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(!((Boolean)result).booleanValue())
				{
					cache.remove(key);
//					data = null;
				}
				else
				{
					if(!res.getIntermediateResults().contains(service))
					{
//						if(data.getClass().getName().indexOf("Shop")!=-1)
//							System.out.println("cache add: "+data+" to: "+results);

						res.addIntermediateResult(service);
					}
				}
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture getTime()
	{
		final Future ret = new Future();
		if(clock!=null)
		{
			clock.isValid().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					ret.setResult(new Long(clock.getTime()));
				}
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(new Long(-1));
				}
			});
		}
		else
		{
			ret.setResult(new Long(-1));
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
	public IComponentIdentifier getId()
	{
		return container.getId();
	}
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return container.getType();
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
	public IFuture addService(IInternalService service, ProvidedServiceInfo info)
	{
		return container.addService(service, info);
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

//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public IFuture getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
//	{
//		return container.getRequiredService(info, binding, rebind);
//	}
//	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	public IIntermediateFuture getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
//	{
//		return container.getRequiredServices(info, binding, rebind);
//	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CacheServiceContainer(name="+getId()+", container="+container+")";
	}

	// todo: factor out this part of service management?!

	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public IFuture getService(final Class type, final IComponentIdentifier cid)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get provided (declared) service.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public IService getProvidedService(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	public IService[] getProvidedServices(Class clazz)
	{
		throw new UnsupportedOperationException();
	}
	
	public RequiredServiceInfo[] getRequiredServiceInfos()
	{
		throw new UnsupportedOperationException();
	}
	
	public RequiredServiceInfo getRequiredServiceInfo(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
		throw new UnsupportedOperationException();
	}
	
	public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
		throw new UnsupportedOperationException();
	}


	public IFuture getRequiredService(String name)
	{
		throw new UnsupportedOperationException();
	}

	public IIntermediateFuture getRequiredServices(String name)
	{
		throw new UnsupportedOperationException();
	}

	public IFuture getRequiredService(String name, boolean rebind)
	{
		throw new UnsupportedOperationException();
	}

	public IIntermediateFuture getRequiredServices(String name, boolean rebind)
	{
		throw new UnsupportedOperationException();
	}

	public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos)
	{
		throw new UnsupportedOperationException();
	}

	public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service)
	{
		throw new UnsupportedOperationException();
	}
	
	public IServiceInvocationInterceptor[] getInterceptors(Object service)
	{
		throw new UnsupportedOperationException();
	}
	
	public Object getProvidedServiceRawImpl(Class<?> clazz)
	{
		throw new UnsupportedOperationException();
	}
	
	public IService getProvidedService(Class<?> clazz)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type, String scope)
	{
		throw new UnsupportedOperationException();
	}
	
	// todo: remove
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchServiceUpwards(Class type)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type, String scope)
	{
		throw new UnsupportedOperationException();
	}
}
