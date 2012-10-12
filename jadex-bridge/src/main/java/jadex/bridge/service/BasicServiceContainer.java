package jadex.bridge.service;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  A service container is a simple infrastructure for a collection of
 *  services. It allows for starting/shutdowning the container and fetching
 *  service by their type/name.
 */
public abstract class BasicServiceContainer implements  IServiceContainer
{	
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map<Class<?>, Collection<IInternalService>> services;
	
	/** The map of provided service infos. (sid -> provided service info) */
	protected Map<IServiceIdentifier, ProvidedServiceInfo> serviceinfos;
	
	/** The container name. */
	protected IComponentIdentifier id;
	
	/** True, if the container is started. */
	protected boolean started;
	
	/** True, if the container is shutdowned. */
	protected boolean shutdowned;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public BasicServiceContainer(IComponentIdentifier id)
	{
		this.id = id;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get all services of a type.
	 *  @param clazz The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture<IService>	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
	{
		if(shutdowned)
		{
//			if(id.getParent()==null)
//			{
//				System.err.println("getS: "+id);
//				Thread.dumpStack();
//			}
			return new IntermediateFuture<IService>(new ComponentTerminatedException(id));
		}
		return manager.searchServices(this, decider, selector, services!=null ? services : Collections.EMPTY_MAP);
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public abstract IFuture<IServiceProvider>	getParent();
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public abstract IFuture<Collection<IServiceProvider>>	getChildren();
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public IComponentIdentifier	getId()
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
	public IFuture<Void>	addService(final IInternalService service, final ProvidedServiceInfo info)
	{
		if(shutdowned)
			return new Future<Void>(new ComponentTerminatedException(id));
		final Future<Void> ret = new Future<Void>();
		
		getServiceType(service.getServiceIdentifier()).addResultListener(new ExceptionDelegationResultListener<Class<?>, Void>(ret)
		{
			public void customResultAvailable(Class<?> servicetype)
			{
//				System.out.println("Adding service: " + info.getName() + " " + service);
				synchronized(this)
				{
					if(services==null)
					{
						services = Collections.synchronizedMap(new LinkedHashMap<Class<?>, Collection<IInternalService>>());
					}
					
					Collection<IInternalService> tmp = services.get(servicetype);
					if(tmp==null)
					{
						tmp = Collections.synchronizedList(new ArrayList<IInternalService>());
						services.put(servicetype, tmp);
					}
					tmp.add(service);
					
					if(serviceinfos==null)
					{
						serviceinfos = Collections.synchronizedMap(new HashMap<IServiceIdentifier, ProvidedServiceInfo>());
					}
					serviceinfos.put(service.getServiceIdentifier(), info);

					
					if(started)
					{
						service.startService().addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								serviceStarted(service).addResultListener(new DelegationResultListener<Void>(ret));
							}
						});
					}
					else
					{
						ret.setResult(null);
					}
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param id The name.
	 *  @param service The service.
	 */
	public IFuture<Void> removeService(final IServiceIdentifier sid)
	{
		if(shutdowned)
			return new Future<Void>(new ComponentTerminatedException(id));
		
		final Future<Void> ret = new Future<Void>();
		
		if(sid==null)
		{
			ret.setException(new IllegalArgumentException("Service identifier nulls."));
			return ret;
		}
		
		getServiceType(sid).addResultListener(new ExceptionDelegationResultListener<Class<?>, Void>(ret)
		{
			public void customResultAvailable(final Class<?> servicetype)
			{
//				System.out.println("Removing service: " + servicetype);
				synchronized(this)
				{
					Collection<IInternalService> tmp = services!=null? services.get(servicetype): null;
					
					IInternalService service = null;
					if(tmp!=null)
					{
						for(Iterator<IInternalService> it=tmp.iterator(); it.hasNext() && service==null; )
						{
							final IInternalService tst = it.next();
							if(tst.getServiceIdentifier().equals(sid))
							{
								service = tst;
								tmp.remove(service);
								// Todo: fix started/terminated!? (i.e. addService() is ignored, when not started!?)
//								if(!terminated)
//								{
//									System.out.println("Terminating service: "+sid);
									getLogger().info("Terminating service: "+sid);
									service.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
									{
										public void customResultAvailable(Void result)
										{
//											if(sid.toString().indexOf("MarshalService")!=-1)
//												System.out.println("Terminated service: "+sid);
											getLogger().info("Terminated service: "+sid);
											serviceShutdowned(tst).addResultListener(new DelegationResultListener<Void>(ret));
										}
									});
//								}
//								else
//								{
//									ret.setResult(null);
//								}
							}
						}
					}
					if(service==null)
					{
						ret.setException(new IllegalArgumentException("Service not found: "+sid));
					}
			
					if(tmp.isEmpty())
						services.remove(servicetype);
				}
			}
		});
		
		return ret;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> start()
	{
		assert	!started;
		started	= true;
		
		final Future<Void> ret = new Future<Void>();
		
		// Start the services.
		if(services!=null && services.size()>0)
		{
			List<IInternalService> allservices = new ArrayList<IInternalService>();
			for(Iterator<Collection<IInternalService>> it=services.values().iterator(); it.hasNext(); )
			{
				allservices.addAll(it.next());
			}
			initServices(allservices.iterator()).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Init the services one by one.
	 */
	protected IFuture<Void> initServices(final Iterator<IInternalService> services)
	{
		if(shutdowned)
			return new Future<Void>(new ComponentTerminatedException(id));
			
		final Future<Void> ret = new Future<Void>();
		if(services.hasNext())
		{
			final IInternalService	is	= services.next();
			getLogger().info("Starting service: "+is.getServiceIdentifier());
			is.startService().addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					getLogger().info("Started service: "+is.getServiceIdentifier());
					serviceStarted(is).addResultListener(new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
							initServices(services).addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
//					getLogger().warning("Exception in service init : "+is.getServiceIdentifier());
//					initServices(services).addResultListener(new DelegationResultListener<Void>(ret));
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
	 *  Shutdown the service.
	 */
	public IFuture<Void> shutdown()
	{
		assert started && !shutdowned;
		
		started	= false;
		
//		Thread.dumpStack();
//		System.out.println("shutdown called: "+getName());
		final Future<Void> ret = new Future<Void>();
		
		// Stop the services.
		if(services!=null && services.size()>0)
		{
			final List<IInternalService> allservices = new ArrayList<IInternalService>();
			for(Iterator<Collection<IInternalService>> it=services.values().iterator(); it.hasNext(); )
			{
				allservices.addAll(it.next());
			}
			
			doShutdown(allservices.iterator()).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					reqservicefetchers	= null;
					requiredserviceinfos	= null;
					shutdowned = true;
					ret.setResult(null);
				}
				public void exceptionOccurred(Exception exception)
				{
					shutdowned = true;
					super.exceptionOccurred(exception);
				}
			});
			
//			final IInternalService[]	service	= new IInternalService[1];	// one element array for final variable.
//			service[0]	= (IInternalService)allservices.remove(allservices.size()-1);
//			getLogger().info("Terminating service: "+service[0].getServiceIdentifier());
////			System.out.println("shutdown start: "+service.getServiceIdentifier());
//			service[0].shutdownService().addResultListener(new DelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					services.remove(service[0].getServiceIdentifier().getServiceType());
//					getLogger().info("Terminated service: "+service[0].getServiceIdentifier());
////					System.out.println("shutdown end: "+result);
//					if(!allservices.isEmpty())
//					{
//						service[0] = (IInternalService)allservices.remove(allservices.size()-1);
//						getLogger().info("Terminating service: "+service[0].getServiceIdentifier());
////						System.out.println("shutdown start: "+service.getServiceIdentifier());
//						service[0].shutdownService().addResultListener(this);
//					}
//					else
//					{
//						reqservicefetchers	= null;
//						requiredserviceinfos	= null;
//						super.customResultAvailable(result);
//					}
//				}
//			});
		}
		else
		{
			shutdowned = true;	
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Do shutdown the services.
	 */
	protected IFuture<Void> doShutdown(final Iterator<IInternalService> services)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(services.hasNext())
		{
			final IInternalService ser = services.next();
			final IServiceIdentifier sid = ser.getServiceIdentifier();

//			removeService(sid).addResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					doShutdown(services).addResultListener(new DelegationResultListener<Void>(ret));
//				}
//			});

//			// Shutdown services in reverse order as later services might depend on earlier ones.
			doShutdown(services).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					removeService(sid).addResultListener(new DelegationResultListener<Void>(ret));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
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
	 *  Called after a service has been started.
	 */
	public IFuture<Void> serviceStarted(IInternalService service)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Called after a service has been shutdowned.
	 */
	public IFuture<Void> serviceShutdowned(IInternalService service)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Get the provided service info for a service.
	 *  @param sid The service identifier.
	 *  @return The provided service info.
	 */
	protected ProvidedServiceInfo getProvidedServiceInfo(IServiceIdentifier sid)
	{
		return serviceinfos.get(sid);
	}
	
	//-------- provided and required service management --------
	
	/** The service fetch method table (name -> fetcher). */
	protected Map<String, IRequiredServiceFetcher>	reqservicefetchers;

	/** The required service infos. */
	protected Map<String, RequiredServiceInfo> requiredserviceinfos;

	/** The service bindings. */
//	protected Map bindings;

	
	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> getService(final Class<T> type, final IComponentIdentifier cid)
	{
		if(shutdowned)
			return new Future<T>(new ComponentTerminatedException(id));

		final Future<T> ret = new Future<T>();
		// Local?
		if(cid.getPlatformName().equals(id.getPlatformName()))
		{
			SServiceProvider.getServiceUpwards(this, IComponentManagementService.class)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
					{
						public void customResultAvailable(IExternalAccess ea)
						{
							SServiceProvider.getDeclaredService(ea.getServiceProvider(), type)
								.addResultListener(new DelegationResultListener<T>(ret));
						}
					});
				}
			});
		}
		else
		{
			SServiceProvider.getService(this, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IRemoteServiceManagementService, T>(ret)
			{
				public void customResultAvailable(IRemoteServiceManagementService rms)
				{
					rms.getServiceProxy(cid, type, RequiredServiceInfo.SCOPE_LOCAL)
						.addResultListener(new DelegationResultListener<T>(ret));
				}
			});
		}
		return ret;
	}

	/**
	 *  Get provided (declared) service.
	 *  @return The service.
	 */
	public IService getProvidedService(String name)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		IService ret = null;
		if(services!=null)
		{
			for(Iterator<Class<?>> it=services.keySet().iterator(); it.hasNext() && ret==null; )
			{
				Collection<IInternalService> sers = services.get(it.next());
				for(Iterator<IInternalService> it2=sers.iterator(); it2.hasNext() && ret==null; )
				{
					IService ser = it2.next();
					if(ser.getServiceIdentifier().getServiceName().equals(name))
					{
						ret = ser;
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the raw implementation of the provided service.
	 *  @param clazz The class.
	 *  @return The raw object.
	 */
	public Object getProvidedServiceRawImpl(Class<?> clazz)
	{
		Object ret = null;
		
		IService service = getProvidedService(clazz);
		if(service!=null)
		{
			BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
			ret = handler.getDomainService();
		}
		
		return ret;
	}

	
	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public IService[] getProvidedServices(Class<?> clazz)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);
		
		Collection<IInternalService> coll = services!=null? services.get(clazz): null;
		return coll==null ? new IService[0] : coll.toArray(new IService[coll.size()]);
	}
	
	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public IService getProvidedService(Class<?> clazz)
	{
		IService[] ret = getProvidedServices(clazz);
		return ret.length>0? ret[0]: null;
	}
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getRequiredServiceInfos()
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		return requiredserviceinfos==null? new RequiredServiceInfo[0]: 
			(RequiredServiceInfo[])requiredserviceinfos.values().toArray(new RequiredServiceInfo[requiredserviceinfos.size()]);
	}
	
	/**
	 *  Set the required services.
	 *  @param required services The required services to set.
	 */
	public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		this.requiredserviceinfos = null;
		addRequiredServiceInfos(requiredservices);
	}
	
	/**
	 *  Add required services for a given prefix.
	 *  @param prefix The name prefix to use.
	 *  @param required services The required services to set.
	 */
	public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		if(requiredservices!=null && requiredservices.length>0)
		{
			if(this.requiredserviceinfos==null)
				this.requiredserviceinfos = new HashMap<String, RequiredServiceInfo>();
			for(int i=0; i<requiredservices.length; i++)
			{
				this.requiredserviceinfos.put(requiredservices[i].getName(), requiredservices[i]);
			}
		}
	}
	
	/**
	 *  Get a required service info.
	 *  @return The required service info.
	 */
	public RequiredServiceInfo getRequiredServiceInfo(String name)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		return requiredserviceinfos==null? null: (RequiredServiceInfo)requiredserviceinfos.get(name);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public IFuture<IService> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		if(shutdowned)
			return new Future<IService>(new ComponentTerminatedException(id));
		return getRequiredService(info, binding, false);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public IIntermediateFuture<IService> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		if(shutdowned)
			return new IntermediateFuture<IService>(new ComponentTerminatedException(id));
		return getRequiredServices(info, binding, false);
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name)
	{
		if(shutdowned)
			return new Future<T>(new ComponentTerminatedException(id));
		return getRequiredService(name, false);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(String name)
	{
		if(shutdowned)
			return new IntermediateFuture<T>(new ComponentTerminatedException(id));
		return getRequiredServices(name, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind)
	{
		if(shutdowned)
			return new Future<T>(new ComponentTerminatedException(id));
		RequiredServiceInfo info = getRequiredServiceInfo(name);
		if(info==null)
		{
			Future<T> ret = new Future<T>();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			RequiredServiceBinding binding = info.getDefaultBinding();//getRequiredServiceBinding(name);
			return getRequiredService(info, binding, rebind);
		}
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(String name, boolean rebind)
	{
		if(shutdowned)
			return new IntermediateFuture<T>(new ComponentTerminatedException(id));

		RequiredServiceInfo info = getRequiredServiceInfo(name);
		if(info==null)
		{
			IntermediateFuture<T> ret = new IntermediateFuture<T>();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			RequiredServiceBinding binding = info.getDefaultBinding();//getRequiredServiceBinding(name);
			return getRequiredServices(info, binding, rebind);
		}
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		if(info==null)
		{
			Future<T> ret = new Future<T>();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		if(shutdowned)
			return new Future<T>(new ComponentTerminatedException(id));
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		return fetcher.getService(info, binding, rebind);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind)
	{
		if(info==null)
		{
			IntermediateFuture<T> ret = new IntermediateFuture<T>();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		if(shutdowned)
			return new IntermediateFuture<T>(new ComponentTerminatedException(id));
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		return fetcher.getServices(info, binding, rebind);
	}
	
	/**
	 *  Get a required service fetcher.
	 *  @param name The required service name.
	 *  @return The service fetcher.
	 */
	protected IRequiredServiceFetcher getRequiredServiceFetcher(String name)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		IRequiredServiceFetcher ret = reqservicefetchers!=null ? reqservicefetchers.get(name) : null;
		if(ret==null)
		{
			ret = createServiceFetcher(name);
			if(reqservicefetchers==null)
				reqservicefetchers = new HashMap<String, IRequiredServiceFetcher>();
			reqservicefetchers.put(name, ret);
		}
		return ret;
	}
		
	/**
	 *  Create a service fetcher.
	 */
	public abstract IRequiredServiceFetcher createServiceFetcher(String name);
	
	/**
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first, -1=last-1, i.e. one before method invocation).
	 */
	public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
		handler.addServiceInterceptor(interceptor, pos);
	}
	
	/**
	 *  Remove a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 */
	public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
		handler.removeServiceInterceptor(interceptor);
	}
	
	/**
	 *  Get the interceptors of a service.
	 *  @param service The service.
	 *  @return The interceptors.
	 */
	public IServiceInvocationInterceptor[] getInterceptors(Object service)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);

		BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
		return handler.getInterceptors();
	}
	
	/**
	 * 
	 */
	public abstract IFuture<Class<?>> getServiceType(final IServiceIdentifier sid);
	
	/**
	 *  Get the logger.
	 *  To be overridden by subclasses.
	 */
	protected abstract Logger	getLogger();
	
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
	
	/**
	 *  Get the default timeout for a method.
	 */
	public static long getMethodTimeout(Class<?>[] interfaces, Method method, boolean remote)
	{
		long ret = Timeout.UNSET;
		
		Class<?>[] allinterfaces = SReflect.getSuperInterfaces(interfaces);
		
		long deftimeout	= Timeout.UNSET;
		for(int i=0; deftimeout==Timeout.UNSET && i<allinterfaces.length; i++)
		{
			// Default timeout for interface (only if method is declared in this interface)
			if(allinterfaces[i].isAnnotationPresent(Timeout.class) && 
				SReflect.getMethod(allinterfaces[i], method.getName(), method.getParameterTypes())!=null)
			{
				Timeout	ta	= (Timeout)allinterfaces[i].getAnnotation(Timeout.class);
				deftimeout = remote? ta.remote(): ta.local();
				if(Timeout.UNSET==deftimeout)
					deftimeout = ta.value();
			}
		}
		
		// Timeout on method overrides global timeout settings
		if(method.isAnnotationPresent(Timeout.class))
		{
			Timeout	ta	= method.getAnnotation(Timeout.class);
			ret = remote? ta.remote(): ta.local();
			if(Timeout.UNSET==ret)
				ret = ta.value();
		}
		
		if(Timeout.UNSET!=deftimeout && Timeout.UNSET==ret)
		{
			ret = deftimeout;
		}
		
		return ret==Timeout.UNSET? remote? BasicService.DEFAULT_REMOTE: BasicService.DEFAULT_LOCAL: ret;
	}
}
