package jadex.bridge.service;

import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.sensor.service.IMethodInvocationListener;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.MethodListenerHandler;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.commons.IAsyncFilter;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

import java.lang.reflect.Array;
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
// old! todo: remove
//public abstract class BasicServiceContainer implements  IServiceContainer, IServiceProvider
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
	
	/** The map of provided service infos. (sid -> method listener) */
	protected Map<IServiceIdentifier, MethodListenerHandler> servicelisteners;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public BasicServiceContainer(IComponentIdentifier id)
	{
		this.id = id;
	}
	
	//-------- interface methods --------
	
//	protected static Set<String>	SEARCHES
//		= Collections.synchronizedSet(new HashSet<String>());
	
//	/**
//	 *  Get all services of a type.
//	 *  @param clazz The class.
//	 *  @return The corresponding services.
//	 */
//	public ITerminableIntermediateFuture<IService> getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector)
//	{
//		if(shutdowned)
//		{
////			if(id.getParent()==null)
////			{
////				System.err.println("getS: "+id);
////				Thread.dumpStack();
////			}
//			return new TerminableIntermediateFuture<IService>(new ComponentTerminatedException(id));
//		}
//		
//		ITerminableIntermediateFuture<IService>	ret	= manager.searchServices(this, decider, selector, services!=null ? services : Collections.EMPTY_MAP);
////		final String	search	= "search: "+manager+", "+decider+", "+selector;
////		if(search.indexOf("IMonitoring")!=-1)
////		{
////			System.out.println(search);
////		}
////		SEARCHES.add(search);
////		ret.addResultListener(new IResultListener<Collection<IService>>()
////		{
////			public void resultAvailable(Collection<IService> result)
////			{
////				SEARCHES.remove(search);
////			}
////			
////			public void exceptionOccurred(Exception exception)
////			{
////				SEARCHES.remove(search);
////			}
////		});
//		return ret;
//	}
	
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
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 *  @param info The provided service info.
	 *  @param componentfetcher	 Helper to fetch corrent object for component injection based on field type.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	addService(final IInternalService service, final ProvidedServiceInfo info)
	{
		if(shutdowned)
			return new Future<Void>(new ComponentTerminatedException(id));
		final Future<Void> ret = new Future<Void>();
		
		getServiceTypes(service.getServiceIdentifier()).addResultListener(new ExceptionDelegationResultListener<Collection<Class<?>>, Void>(ret)
		{
			public void customResultAvailable(final Collection<Class<?>> servicetypes)
			{
				// Services are available per default for subcomponents even if not inited
//				// Hack!!! Must make cms available before init for bootstrapping of service container of platform
//				if(started && (service.getServiceIdentifier().getServiceType().getTypeName().indexOf("IComponentManagementService")!=-1
//					|| service.getServiceIdentifier().getServiceType().getTypeName().indexOf("IMessageService")!=-1))
//				{
//					for(Class<?> key: servicetypes)
//					{
//						getServiceRegistry().addService(new ClassInfo(key), service);
//					}
//				}
				
				synchronized(this)
				{
					if(services==null)
					{
						services = Collections.synchronizedMap(new LinkedHashMap<Class<?>, Collection<IInternalService>>());
					}
					
					for(Class<?> servicetype: servicetypes)
					{
						Collection<IInternalService> tmp = services.get(servicetype);
						if(tmp==null)
						{
							tmp = Collections.synchronizedList(new ArrayList<IInternalService>());
							services.put(servicetype, tmp);
						}
						tmp.add(service);
					}
					
					if(serviceinfos==null)
					{
						serviceinfos = Collections.synchronizedMap(new HashMap<IServiceIdentifier, ProvidedServiceInfo>());
					}
					serviceinfos.put(service.getServiceIdentifier(), info);
				}
					
				if(started)
				{
					service.setComponentAccess(getComponent()).addResultListener(new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
							service.startService().addResultListener(new DelegationResultListener<Void>(ret)
							{
								public void customResultAvailable(Void result)
								{
									for(Class<?> key: servicetypes)
									{
										getServiceRegistry().addService(new ClassInfo(key), service);
									}
									serviceStarted(service).addResultListener(new DelegationResultListener<Void>(ret));
								}
							});
						};
					});
				}
				else
				{
					// Make services available for init of subcomponents, but not visible to outside until component is running.
					for(Class<?> key: servicetypes)
					{
						getServiceRegistry().addService(new ClassInfo(key), service);
					}
					ret.setResult(null);
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param chainid The name.
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
		
		getServiceTypes(sid).addResultListener(new ExceptionDelegationResultListener<Collection<Class<?>>, Void>(ret)
		{
			public void customResultAvailable(final Collection<Class<?>> servicetypes)
			{
//				System.out.println("Removing service: " + servicetype);
				synchronized(this)
				{
					IInternalService service = null;
					
					for(Class<?> servicetype: servicetypes)
					{
						Collection<IInternalService> tmp = services!=null? services.get(servicetype): null;
						
						service = null;
						
						if(tmp!=null)
						{
							for(Iterator<IInternalService> it=tmp.iterator(); it.hasNext() && service==null; )
							{
								final IInternalService tst = it.next();
								if(tst.getServiceIdentifier().equals(sid))
								{
									service = tst;
									tmp.remove(service);
								}
							}
							
							// Remove collection if last service
							if(tmp.isEmpty())
							{
								services.remove(servicetype);
							}
						}
						
						if(service==null)
						{
							ret.setException(new IllegalArgumentException("Service not found: "+sid));
							break;
						}
					}
					
					if(service!=null)
					{
						final IInternalService fservice = service;
						// Todo: fix started/terminated!? (i.e. addService() is ignored, when not started!?)
	//					if(!terminated)
	//					{
//							if(sid.toString().indexOf("Context")!=-1)
//								System.out.println("Terminating service: "+sid);
							getLogger().info("Terminating service: "+sid);
							
							// Dispose nonfunc properties
							service.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret)
							{
								public void customResultAvailable(Void result)
								{
//									if(fservice.getServiceIdentifier().toString().indexOf("ContextSer")!=-1)
//										System.out.println("hierda");
									
									fservice.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
									{
										public void customResultAvailable(Void result)
										{
//											if(id.getParent()==null)// && sid.toString().indexOf("Async")!=-1)
//												System.out.println("Terminated service: "+sid);
											getLogger().info("Terminated service: "+sid);
											
											for(Class<?> key: servicetypes)
											{
												getServiceRegistry().removeService(new ClassInfo(key), fservice);
											}
											
											serviceShutdowned(fservice).addResultListener(new DelegationResultListener<Void>(ret));
										}
										
										public void exceptionOccurred(Exception exception)
										{
											exception.printStackTrace();
											super.exceptionOccurred(exception);
										}
									});
								}
								
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
									super.exceptionOccurred(exception);
								}
							});							
	//					}
	//					else
	//					{
	//						ret.setResult(null);
	//					}
					}
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
				// Service may occur at different positions if added with more than one interface
				Collection<IInternalService> col = it.next();
				for(IInternalService ser: col)
				{
					if(!allservices.contains(ser))
					{
						allservices.add(ser);
					}
				}
//				allservices.addAll(it.next());
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
			is.setComponentAccess(getComponent()).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
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
//									getLogger().warning("Exception in service init : "+is.getServiceIdentifier());
//									initServices(services).addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
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
				// Service may occur at different positions if added with more than one interface
				Collection<IInternalService> col = it.next();
				for(IInternalService ser: col)
				{
					if(!allservices.contains(ser))
					{
						allservices.add(ser);
					}
				}
//				allservices.addAll(it.next());
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
	 *  Get the current state for snapshot or persistence.
	 */
	public ServiceContainerPersistInfo	getPersistInfo()
	{
		return new ServiceContainerPersistInfo(this);
	}
	
	/**
	 *  Restore a container from a persited state,
	 */
	public void	restore(ServiceContainerPersistInfo info)
	{
		// Todo...
		this.started	= true;
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

	
//	/**
//	 *  Get one service of a type from a specific component.
//	 *  @param type The class.
//	 *  @param cid The component identifier of the target component.
//	 *  @return The corresponding service.
//	 */
//	public <T> IFuture<T> getService(final Class<T> type, final IComponentIdentifier cid)
//	{
//		if(shutdowned)
//			return new Future<T>(new ComponentTerminatedException(id));
//
//		final Future<T> ret = new Future<T>();
//		// Local?
//		if(cid.getPlatformName().equals(id.getPlatformName()))
//		{
//			SServiceProvider.getServiceUpwards(this, IComponentManagementService.class)
//				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService cms)
//				{
//					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
//					{
//						public void customResultAvailable(IExternalAccess ea)
//						{
//							SServiceProvider.getDeclaredService(ea.getServiceProvider(), type)
//								.addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//			});
//		}
//		else
//		{
//			SServiceProvider.getService(this, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new ExceptionDelegationResultListener<IRemoteServiceManagementService, T>(ret)
//			{
//				public void customResultAvailable(IRemoteServiceManagementService rms)
//				{
//					rms.getServiceProxy(cid, type, RequiredServiceInfo.SCOPE_LOCAL)
//						.addResultListener(new DelegationResultListener<T>(ret));
//				}
//			});
//		}
//		return ret;
//	}

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
	public <T> T getProvidedServiceRawImpl(Class<T> clazz)
	{
		T ret = null;
		
		T service = getProvidedService(clazz);
		if(service!=null)
		{
			BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
			ret = (T)handler.getDomainService();
		}
		
		return ret;
	}

	
	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public <T> T[] getProvidedServices(Class<T> clazz)
	{
		if(shutdowned)
			throw new ComponentTerminatedException(id);
		
		Collection<IInternalService> coll = services!=null? services.get(clazz): null;
		T[] ret	= (T[])Array.newInstance(clazz, coll!=null ? coll.size(): 0);
		return coll==null ? ret : coll.toArray(ret);
	}
	
	/**
	 *  Get provided (declared) service.
	 *  @param clazz The interface.
	 *  @return The service.
	 */
	public <T> T getProvidedService(Class<T> clazz)
	{
		T[] ret = getProvidedServices(clazz);
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
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name)
	{
		return getRequiredService(name, false);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name)
	{
		return getRequiredServices(name, false);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		return getRequiredService(info, binding, false, null);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding)
	{
		return getRequiredServices(info, binding, false, null);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, IAsyncFilter<T> filter)
	{
		IFuture<T> ret = getRequiredService(info, binding, false, filter);
		return ret;
//		return getRequiredService(info, binding, false, filter);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> IIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, IAsyncFilter<T> filter)
	{
		return getRequiredServices(info, binding, false, filter);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind)
	{
		return getRequiredService(name, rebind, null);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IAsyncFilter<T> filter)
	{
		if(shutdowned)
			return new Future<T>(new ComponentTerminatedException(id));
		
		RequiredServiceInfo info = getRequiredServiceInfo(name);
		if(info==null)
		{
			Future<T> ret = new Future<T>();
			ret.setException(new ServiceNotFoundException(name+" in: "+id));
			return ret;
		}
		else
		{
			RequiredServiceBinding binding = info.getDefaultBinding();//getRequiredServiceBinding(name);
			return getRequiredService(info, binding, rebind, filter);
		}
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind)
	{
		return getRequiredServices(name, rebind, null);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IAsyncFilter<T> filter)
	{
		if(shutdowned)
			return new TerminableIntermediateFuture<T>(new ComponentTerminatedException(id));

		RequiredServiceInfo info = getRequiredServiceInfo(name);
		if(info==null)
		{
			TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
			ret.setException(new ServiceNotFoundException(name));
			return ret;
		}
		else
		{
			RequiredServiceBinding binding = info.getDefaultBinding();//getRequiredServiceBinding(name);
			return getRequiredServices(info, binding, rebind, filter);
		}
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
	{
		if(info==null)
		{
			Future<T> ret = new Future<T>();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		return fetcher.getService(info, binding, rebind, filter);
	}
	
	/**
	 *  Get required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
	{
		if(info==null)
		{
			TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
			ret.setException(new IllegalArgumentException("Info must not null."));
			return ret;
		}
		
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(info.getName());
		return fetcher.getServices(info, binding, rebind, filter);
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required service name.
	 *  @return The last result.
	 */
	public <T> T getLastRequiredService(String name)
	{
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(name);
		return fetcher.getLastService();
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required services name.
	 *  @return The last result.
	 */
	public <T> Collection<T> getLastRequiredServices(String name)
	{
		IRequiredServiceFetcher fetcher = getRequiredServiceFetcher(name);
		return fetcher.getLastServices();
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
	 *  Add a method invocation handler.
	 */
	public void addMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
	{
//		System.out.println("added lis: "+sid+" "+mi+" "+hashCode());
		
		if(servicelisteners==null)
			servicelisteners = new HashMap<IServiceIdentifier, MethodListenerHandler>();
		MethodListenerHandler handler = servicelisteners.get(sid);
		if(handler==null)
		{
			handler = new MethodListenerHandler();
			servicelisteners.put(sid, handler);
		}
		handler.addMethodListener(mi, listener);
	}
	
	/**
	 *  Remove a method invocation handler.
	 */
	public void removeMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
	{
		if(servicelisteners!=null)
		{
			MethodListenerHandler handler = servicelisteners.get(sid);
			if(handler!=null)
			{
				handler.removeMethodListener(mi, listener);
			}
		}
	}
	
	/**
	 *  Notify listeners that a service method has been called.
	 */
	public void notifyMethodListeners(IServiceIdentifier sid, boolean start, Object proxy, final Method method, final Object[] args, Object callid, ServiceInvocationContext context)
	{
		if(servicelisteners!=null)
		{
			MethodListenerHandler handler = servicelisteners.get(sid);
			if(handler!=null)
			{
//				MethodInfo mi = new MethodInfo(method);
				handler.notifyMethodListeners(start, proxy, method, args, callid, context);
			}
		}
	}
	
	/**
	 *  Test if service and method has listeners.
	 */
	public boolean hasMethodListeners(IServiceIdentifier sid, MethodInfo mi)
	{
		boolean ret = false;
		if(servicelisteners!=null)
		{
			MethodListenerHandler handler = servicelisteners.get(sid);
			if(handler!=null)
			{
				ret = handler.hasMethodListeners(sid, mi);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public abstract IInternalAccess getComponent();
	
	/**
	 * 
	 */
	public abstract IFuture<Class<?>> getServiceType(final IServiceIdentifier sid);
	
	/**
	 * 
	 */
	public abstract IFuture<Collection<Class<?>>> getServiceTypes(final IServiceIdentifier sid);
	
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
		return obj instanceof IServiceContainer && ((IServiceProvider)obj).getId().equals(getId());
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
				
//		return ret==Timeout.UNSET? remote? BasicService.getRemoteDefaultTimeout(): BasicService.getLocalDefaultTimeout(): ret;
		return ret;
	}
	
}
