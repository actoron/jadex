package jadex.bridge.service.component;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.sensor.service.IMethodInvocationListener;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.commons.IValueFetcher;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;

/**
 *  Feature for provided services.
 */
public class ProvidedServicesComponentFeature	extends AbstractComponentFeature	implements IProvidedServicesFeature
{
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map<Class<?>, Collection<IInternalService>> services;
	
	/** The map of provided service infos. (sid -> method listener) */
	protected Map<IServiceIdentifier, MethodListenerHandler> servicelisteners;
	
	/** The map of provided service infos. (sid -> provided service info) */
	protected Map<IServiceIdentifier, ProvidedServiceInfo> serviceinfos;
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public ProvidedServicesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IComponentFeature interface / instance level --------
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void>	init()
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			// Collect provided services from model (name or type -> provided service info)
			ProvidedServiceInfo[] ps = component.getModel().getProvidedServices();
			Map<Object, ProvidedServiceInfo> sermap = new LinkedHashMap<Object, ProvidedServiceInfo>();
			for(int i=0; i<ps.length; i++)
			{
				Object key = ps[i].getName()!=null? ps[i].getName(): ps[i].getType().getType(component.getClassLoader(), component.getModel().getAllImports());
				if(sermap.put(key, ps[i])!=null)
				{
					throw new RuntimeException("Services with same type must have different name.");  // Is catched and set to ret below
				}
			}
			
			// Adapt services to configuration (if any).
			if(component.getConfiguration()!=null)
			{
				ConfigurationInfo cinfo = component.getModel().getConfiguration(component.getConfiguration());
				ProvidedServiceInfo[] cs = cinfo.getProvidedServices();
				for(int i=0; i<cs.length; i++)
				{
					Object key = cs[i].getName()!=null? cs[i].getName(): cs[i].getType().getType(component.getClassLoader(), component.getModel().getAllImports());
					ProvidedServiceInfo psi = (ProvidedServiceInfo)sermap.get(key);
					ProvidedServiceInfo newpsi= new ProvidedServiceInfo(psi.getName(), psi.getType().getType(component.getClassLoader(), component.getModel().getAllImports()), 
						new ProvidedServiceImplementation(cs[i].getImplementation()), 
						cs[i].getScope()!=null? cs[i].getScope(): psi.getScope(),
						cs[i].getPublish()!=null? cs[i].getPublish(): psi.getPublish(), 
						cs[i].getProperties()!=null? cs[i].getProperties() : psi.getProperties());
					sermap.put(key, newpsi);
				}
			}
			
			// Add custom service infos from outside.
			ProvidedServiceInfo[] pinfos = cinfo.getProvidedServiceInfos();
			for(int i=0; pinfos!=null && i<pinfos.length; i++)
			{
				Object key = pinfos[i].getName()!=null? pinfos[i].getName(): pinfos[i].getType().getType(component.getClassLoader(), component.getModel().getAllImports());
				ProvidedServiceInfo psi = (ProvidedServiceInfo)sermap.get(key);
				ProvidedServiceInfo newpsi= new ProvidedServiceInfo(psi.getName(), psi.getType().getType(component.getClassLoader(), component.getModel().getAllImports()), 
					pinfos[i].getImplementation()!=null? new ProvidedServiceImplementation(pinfos[i].getImplementation()): psi.getImplementation(), 
					pinfos[i].getScope()!=null? pinfos[i].getScope(): psi.getScope(),
					pinfos[i].getPublish()!=null? pinfos[i].getPublish(): psi.getPublish(), 
					pinfos[i].getProperties()!=null? pinfos[i].getProperties() : psi.getProperties());
				sermap.put(key, newpsi);
			}
			
			FutureBarrier<Void> bar = new FutureBarrier<Void>();
			
			// Instantiate service objects
			for(ProvidedServiceInfo info: sermap.values())
			{
				final ProvidedServiceImplementation	impl = info.getImplementation();
				// Virtual service (e.g. promoted)
				if(impl!=null && impl.getBinding()!=null)
				{
					RequiredServiceInfo rsi = new RequiredServiceInfo(BasicService.generateServiceName(info.getType().getType( 
						component.getClassLoader(), component.getModel().getAllImports()))+":virtual", info.getType().getType(component.getClassLoader(), component.getModel().getAllImports()));
					IServiceIdentifier sid = BasicService.createServiceIdentifier(component, 
						rsi.getName(), rsi.getType().getType(component.getClassLoader(), component.getModel().getAllImports()),
						BasicServiceInvocationHandler.class, component.getModel().getResourceIdentifier(), info.getScope());
					final IInternalService service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(
						component, sid, rsi, impl.getBinding(), component.getClassLoader(), Starter.isRealtimeTimeout(component.getComponentIdentifier()));
					
					bar.addFuture(addService(service, info));
				}
				else
				{
					Object ser = createServiceImplementation(info, getComponent().getFetcher());
					
					// Implementation may null to disable service in some configurations.
					if(ser!=null)
					{
						UnparsedExpression[] ins = info.getImplementation().getInterceptors();
						IServiceInvocationInterceptor[] ics = null;
						if(ins!=null)
						{
							ics = new IServiceInvocationInterceptor[ins.length];
							for(int i=0; i<ins.length; i++)
							{
								if(ins[i].getValue()!=null && ins[i].getValue().length()>0)
								{
									ics[i] = (IServiceInvocationInterceptor)SJavaParser.evaluateExpression(ins[i].getValue(), component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader());
								}
								else
								{
									ics[i] = (IServiceInvocationInterceptor)ins[i].getClazz().getType(component.getClassLoader(), component.getModel().getAllImports()).newInstance();
								}
							}
						}
						
						final Class<?> type = info.getType().getType(component.getClassLoader(), component.getModel().getAllImports());
						PublishEventLevel elm = component.getComponentDescription().getMonitoring()!=null? component.getComponentDescription().getMonitoring(): null;
//						 todo: remove this? currently the level cannot be turned on due to missing interceptor
						boolean moni = elm!=null? !PublishEventLevel.OFF.equals(elm.getLevel()): false; 
						final IInternalService proxy = BasicServiceInvocationHandler.createProvidedServiceProxy(
							component, ser, info.getName(), type, info.getImplementation().getProxytype(), ics, 
							moni, info, info.getScope());
						
						bar.addFuture(addService(proxy, info));
					}
				}
			}
			
			bar.waitFor().addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					// Start the services.
					Collection<IInternalService>	allservices	= getAllServices();
					if(!allservices.isEmpty())
					{
						initServices(allservices.iterator()).addResultListener(new DelegationResultListener<Void>(ret));
					}
					else
					{
						ret.setResult(null);
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Called when the feature is shutdowned.
	 */
	public IFuture<Void> shutdown()
	{
		Future<Void>	ret	= new Future<Void>();
		
		// Shutdown the services.
		Collection<IInternalService>	allservices	= getAllServices();
		if(!allservices.isEmpty())
		{
			LinkedList<IInternalService>	list	= new LinkedList<IInternalService>(allservices);
			shutdownServices(list.descendingIterator()).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Add a service.
	 *  @param service	The service object.
	 *  @param info	 The service info.
	 */
	protected IFuture<Void> addService(IInternalService service, ProvidedServiceInfo info)
	{
		if(serviceinfos==null)
			serviceinfos = new HashMap<IServiceIdentifier, ProvidedServiceInfo>();
		serviceinfos.put(service.getServiceIdentifier(), info);
		
		// Find service types
//		Class<?>	type	= info.getType().getType(component.getClassLoader(), component.getModel().getAllImports());
		Class<?>	type	= service.getServiceIdentifier().getServiceType().getType(component.getClassLoader(), component.getModel().getAllImports());
		Set<Class<?>> types = new LinkedHashSet<Class<?>>();
		types.add(type);
		for(Class<?> sin: SReflect.getSuperInterfaces(new Class[]{type}))
		{
			if(sin.isAnnotationPresent(Service.class))
			{
				types.add(sin);
			}
		}

		if(services==null)
			services = Collections.synchronizedMap(new LinkedHashMap<Class<?>, Collection<IInternalService>>());
		
//		return ServiceRegistry.getRegistry(component.getComponentIdentifier()).addService(service);
		
//		FutureBarrier<Void> bar = new FutureBarrier<Void>();
		
		for(Class<?> servicetype: types)
		{
			Collection<IInternalService> tmp = services.get(servicetype);
			if(tmp==null)
			{
				tmp = Collections.synchronizedList(new ArrayList<IInternalService>());
				services.put(servicetype, tmp);
			}
			tmp.add(service);
			
			// Make service available immediately, even before start (hack???).
//			bar.addFuture(SynchronizedServiceRegistry.getRegistry(component.getComponentIdentifier()).addService(new ClassInfo(servicetype), service));
		}
		
		return ServiceRegistry.getRegistry(component.getComponentIdentifier()).addService(service);
//		return bar.waitFor();
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
	
	/**
	 *  Remove a service.
	 *  @param service	The service object.
	 *  @param info	 The service info.
	 */
	protected void	removeService(IInternalService service)
	{
		IServiceRegistry	registry	= ServiceRegistry.getRegistry(component.getComponentIdentifier());
		
		if(registry!=null) // Maybe null on rescue thread (todo: why remove() on rescue thread?)
		{
			registry.removeService(service);
		}
	}
	
	/**
	 *  Create a service implementation from description.
	 */
	protected Object createServiceImplementation(ProvidedServiceInfo info, IValueFetcher fetcher) throws Exception
	{
		Object	ser	= null;
		ProvidedServiceImplementation impl = info.getImplementation();
		if(impl!=null && impl.getValue()!=null)
		{
			// todo: other Class imports, how can be found out?
			try
			{
//				SimpleValueFetcher fetcher = new SimpleValueFetcher(component.getFetcher());
//				fetcher.setValue("$servicename", info.getName());
//				fetcher.setValue("$servicetype", info.getType().getType(component.getClassLoader(), component.getModel().getAllImports()));
//				System.out.println("sertype: "+fetcher.fetchValue("$servicetype")+" "+info.getName());
				ser = SJavaParser.getParsedValue(impl, component.getModel().getAllImports(), fetcher, component.getClassLoader());
//				System.out.println("added: "+ser+" "+model.getName());
			}
			catch(RuntimeException e)
			{
//				e.printStackTrace();
				throw new RuntimeException("Service creation error: "+info, e);
			}
		}
		else if(impl!=null && impl.getClazz()!=null)
		{
			if(impl.getClazz().getType(component.getClassLoader(), component.getModel().getAllImports())!=null)
			{
				ser = impl.getClazz().getType(component.getClassLoader(), component.getModel().getAllImports()).newInstance();
			}
			else
			{
				throw new RuntimeException("Could not load service implementation class: "+impl.getClazz());
			}
		}
		
		return ser;
	}
	
	/**
	 *  Get all services in a single collection.
	 */
	protected Collection<IInternalService>	getAllServices()
	{
		Collection<IInternalService> allservices;
		if(services!=null && services.size()>0)
		{
			allservices = new LinkedHashSet<IInternalService>();
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
			}
		}
		else
		{
			allservices	= Collections.emptySet();
		}
		
		return allservices;
	}
	
	/**
	 *  Init the services one by one.
	 */
	protected IFuture<Void> initServices(final Iterator<IInternalService> services)
	{
		final Future<Void> ret = new Future<Void>();
		if(services.hasNext())
		{
			final IInternalService	is	= services.next();
			initService(is).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					initServices(services).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
//			component.getLogger().info("Starting service: "+is.getServiceIdentifier());
//			is.setComponentAccess(component).addResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					is.startService().addResultListener(new IResultListener<Void>()
//					{
//						public void resultAvailable(Void result)
//						{
//							component.getLogger().info("Started service: "+is.getServiceIdentifier());
//							
//							
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							ret.setException(exception);
//						}
//					});
//				}
//			});
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Init a service, i.e. set the component (internal access) and call startService.
	 */
	protected IFuture<Void> initService(final IInternalService is)
	{
		final Future<Void> ret = new Future<Void>();
//		component.getLogger().info("Starting service: "+is.getServiceIdentifier()+" "+component.getComponentFeature(IExecutionFeature.class).isComponentThread());
		is.setComponentAccess(component).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				System.out.println("Starting service: "+is.getServiceIdentifier()+" "+component.getComponentFeature(IExecutionFeature.class).isComponentThread());
				is.startService().addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
//						component.getLogger().info("Started service: "+is.getServiceIdentifier());
						serviceStarted(is).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Called after a service has been started.
	 */
	public IFuture<Void> serviceStarted(final IInternalService service)
	{
		final Future<Void> ret = new Future<Void>();
		ProvidedServiceInfo info = getProvidedServiceInfo(service.getServiceIdentifier());
		PublishInfo pit = info==null? null: info.getPublish();
		if(pit!=null)
		{
			// Hack?! evaluate the publish id string 
			// Must clone info to not change the model
			final PublishInfo pi = new PublishInfo(pit);
			try
			{
				String pid = (String)SJavaParser.evaluateExpression(pi.getPublishId(), getComponent().getModel().getAllImports(), getComponent().getFetcher(), getComponent().getClassLoader());
				pi.setPublishId(pid);
//				System.out.println("pid is now: "+pid);
			}
			catch(Exception e)
			{
//				e.printStackTrace();
			}
			
			if (pi.isMulti())
			{
				SServiceProvider.getServices(getComponent(), IPublishService.class, pi.getPublishScope()).addResultListener(new IIntermediateResultListener<IPublishService>()
				{
					/** Flag if published at least once. */
					protected boolean published = false;
					
					/** Flag if finished. */
					protected boolean finished = false;
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}

					public void resultAvailable(
							Collection<IPublishService> result)
					{
					}
					
					public void intermediateResultAvailable(final IPublishService result)
					{
						result.publishService(service.getServiceIdentifier(), pi).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void vresult)
							{
								if (!published)
								{
									ret.setResult(null);
									published = true;
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if (finished && !published)
								{
									getComponent().getLogger().severe("Could not publish: "+service.getServiceIdentifier());
									ret.setException(exception);
								}
							}
						});
					}

					public void finished()
					{
						finished = true;
					}
				});
//				SServiceProvider.getServices(getComponent(), IPublishService.class, pi.getPublishScope()).addResultListener(new IResultListener<Collection<IPublishService>>()
//				{
//					public void exceptionOccurred(Exception exception)
//					{
//						getComponent().getLogger().severe("Could not publish: "+service.getServiceIdentifier()+" "+exception.getMessage());
//						ret.setResult(null);
//					}
//					
//					public void resultAvailable(Collection<IPublishService> result)
//					{
//						for (final IPublishService pubserv : result)
//						{
//							pubserv.publishService(service.getServiceIdentifier(), pi).addResultListener(new IResultListener<Void>()
//							{
//								public void resultAvailable(Void result)
//								{
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									getComponent().getLogger().severe("Could not publish to " + pubserv + ": "+service.getServiceIdentifier()+" "+exception.getMessage());
//								}
//							});
//						}
//					}
//				});
			}
			else
			{
				getPublishService(getComponent(), pi.getPublishType(), pi.getPublishScope(), (Iterator<IPublishService>)null)
					.addResultListener(getComponent().getComponentFeature(IExecutionFeature.class)
					.createResultListener(new ExceptionDelegationResultListener<IPublishService, Void>(ret)
				{
					public void customResultAvailable(IPublishService ps)
					{
						//System.out.println("Got publish service " + ps);
						ps.publishService(service.getServiceIdentifier(), pi)
							.addResultListener(getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
					}
					public void exceptionOccurred(Exception exception)
					{
	//					exception.printStackTrace();
						getComponent().getLogger().severe("Could not publish: "+service.getServiceIdentifier()+" "+exception.getMessage());
						ret.setResult(null);
					}
				}));
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Called after a service has been shutdowned.
	 */
	public IFuture<Void> serviceShutdowned(final IInternalService service)
	{
		final Future<Void> ret = new Future<Void>();
//		adapter.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				ProvidedServiceInfo info = getProvidedServiceInfo(service.getServiceIdentifier());
				final PublishInfo pi = info==null? null: info.getPublish();
//				System.out.println("shutdown ser: "+service.getServiceIdentifier());
				if(pi!=null)
				{
					final IServiceIdentifier sid = service.getServiceIdentifier();
//					getPublishService(instance, pi.getPublishType(), null).addResultListener(instance.createResultListener(new IResultListener<IPublishService>()
					getPublishService(getComponent(), pi.getPublishType(), pi.getPublishScope(), null).addResultListener(new IResultListener<IPublishService>()
					{
						public void resultAvailable(IPublishService ps)
						{
							ps.unpublishService(sid).addResultListener(new DelegationResultListener<Void>(ret));
						}
						
						public void exceptionOccurred(Exception exception)
						{
			//				instance.getLogger().severe("Could not unpublish: "+sid+" "+exception.getMessage());
							
							// ignore, if no publish info
							ret.setResult(null);
							// todo: what if publish info but no publish service?
						}
					});
				}
				else
				{
					ret.setResult(null);
				}				
//			}
//		});
		return ret;
	}
	
	/**
	 *  Get the publish service for a publish type (e.g. web service).
	 *  @param type The type.
	 *  @param services The iterator of publish services (can be null).
	 *  @return The publish service.
	 */
	public static IFuture<IPublishService> getPublishService(final IInternalAccess instance, final String type, final String scope, final Iterator<IPublishService> services)
	{
		final Future<IPublishService> ret = new Future<IPublishService>();
		
		if(services==null)
		{
			IFuture<Collection<IPublishService>> fut = SServiceProvider.getServices(instance, IPublishService.class, scope, null);
			fut.addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IPublishService>, IPublishService>(ret)
			{
				@Override
				public void exceptionOccurred(Exception exception) {
					// TODO Auto-generated method stub
					super.exceptionOccurred(exception);
					exception.printStackTrace();
				}
				public void customResultAvailable(Collection<IPublishService> result)
				{
					getPublishService(instance, type, scope, result.iterator()).addResultListener(new DelegationResultListener<IPublishService>(ret));
				}
			}));
		}
		else
		{
			if(services.hasNext())
			{
				final IPublishService ps = (IPublishService)services.next();
				ps.isSupported(type).addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Boolean, IPublishService>(ret)
				{
					public void customResultAvailable(Boolean supported)
					{
						if(supported.booleanValue())
						{
							ret.setResult(ps);
						}
						else
						{
							getPublishService(instance, type, scope, services).addResultListener(new DelegationResultListener<IPublishService>(ret));
						}
					}
				}));
			}
			else
			{
//				ret.setResult(null);
				ret.setException(new ServiceNotFoundException("IPublishService not found."));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the services one by one.
	 */
	protected IFuture<Void> shutdownServices(final Iterator<IInternalService> services)
	{
		final Future<Void> ret = new Future<Void>();
		if(services.hasNext())
		{
			final IInternalService	is	= services.next();
			// Remove service from registry before shutdown.
			removeService(is);
			
			component.getLogger().info("Stopping service: "+is.getServiceIdentifier());
			is.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					component.getLogger().info("Stopped service: "+is.getServiceIdentifier());
					serviceShutdowned(is).addResultListener(new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
							shutdownServices(services).addResultListener(new DelegationResultListener<Void>(ret));
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
	
	//-------- IProvidedServicesFeature interface --------

	/**
	 *  Get provided (declared) service.
	 *  @return The service.
	 */
	public IService getProvidedService(String name)
	{
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
			BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(service);
			ret = clazz.cast(handler.getDomainService());
		}
		
		return ret;
	}

	/**
	 *  Get the provided service implementation object by name.
	 *  
	 *  @param name The service name.
	 *  @return The service.
	 */
	public Object getProvidedServiceRawImpl(String name)
	{
		Object ret = null;
		
		Object service = getProvidedService(name);
		if(service!=null)
		{
			BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(service);
			ret = handler.getDomainService();
		}
		
		return ret;	
	}
	
	/**
	 *  Get the provided service implementation object by id.
	 *  
	 *  @param name The service identifier.
	 *  @return The service.
	 */
	public Object getProvidedService(IServiceIdentifier sid)
	{
		Object ret = null;
		
		Object[] services = getProvidedServices(sid.getServiceType().getType(getComponent().getClassLoader()));
		if(services!=null)
		{
			for(Object ser: services)
			{
				// Special case for fake proxies, i.e. creating a service proxy for a known component (without knowing cid)
				if(sid.getServiceName().equals("NULL"))
				{
					((IService)ser).getServiceIdentifier().getServiceType().equals(sid.getServiceType());
					ret = (IService)ser;
					break;
				}
				else if(((IService)ser).getServiceIdentifier().equals(sid))
				{
					ret = (IService)ser;
					break;
				}
			}
		}
		
		return ret;	
	}
	
	/**
	 *  Get the provided service implementation object by id.
	 *  
	 *  @param name The service identifier.
	 *  @return The service.
	 */
	public Object getProvidedServiceRawImpl(IServiceIdentifier sid)
	{
		Object ret = null;
		
		Object[] services = getProvidedServices(sid.getServiceType().getType(getComponent().getClassLoader()));
		if(services!=null)
		{
			IService service = null;
			for(Object ser: services)
			{
				if(((IService)ser).getServiceIdentifier().equals(sid))
				{
					service = (IService)ser;
					break;
				}
			}
			if(service!=null)
			{
				if(ProxyFactory.isProxyClass(service.getClass()))
				{
					BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(service);
					ret = handler.getDomainService();
				}
				else
				{
					ret = service;
				}
			}
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
		Collection<IInternalService> coll	= null;
		if(services!=null)
		{
			if(clazz!=null)
			{
				coll = services.get(clazz);
			}
			else
			{
				coll = new HashSet<IInternalService>();
				for(Class<?> cl: services.keySet())
				{
					Collection<IInternalService> sers = services.get(cl);
					coll.addAll(sers);
				}
			}			
		}
		
		T[] ret	= (T[])Array.newInstance(clazz==null? Object.class: clazz, coll!=null ? coll.size(): 0);
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
	 *  Get the services.
	 *  @return The services.
	 */
	public Map<Class<?>, Collection<IInternalService>> getServices() 
	{
		return services;
	}

	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service)
	{
		return addService(name, type, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, service, null, null);
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param type The proxy type (@see{BasicServiceInvocationHandler}).
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service, String proxytype)
	{
		return addService(name, type, proxytype, null, service, null, null);
	}
	
	// todo:
//	/**
//	 *  Add a service to the platform. 
//	 *  If under the same name and type a service was contained,
//	 *  the old one is removed and shutdowned.
//	 *  @param type The public service interface.
//	 *  @param service The service.
//	 */
//	public void addService(String name, Class<?> type, Object service, PublishInfo pi)
//	{
//		addService(name, type, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, service, pi);
//	}
	
	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param scope	The service scope.
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service, PublishInfo pi, String scope)
	{
		ProvidedServiceInfo psi = pi!=null? new ProvidedServiceInfo(null, type, null, null, pi, null): null;
		return addService(name, type, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, service, psi, scope);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param service The service.
	 */
	public IFuture<Void> removeService(final IServiceIdentifier sid)
	{
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
//									System.out.println("Terminating service: "+sid);
							getComponent().getLogger().info("Terminating service: "+sid);
							
							// Dispose nonfunc properties
							
							// todo: how to shutdown?
							
							ret.setResult(null);
							
//							service.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret)
//							{
//								public void customResultAvailable(Void result)
//								{
////									if(fservice.getServiceIdentifier().toString().indexOf("ContextSer")!=-1)
////										System.out.println("hierda");
//									
//									fservice.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
//									{
//										public void customResultAvailable(Void result)
//										{
////											if(id.getParent()==null)// && sid.toString().indexOf("Async")!=-1)
////												System.out.println("Terminated service: "+sid);
//											getLogger().info("Terminated service: "+sid);
//											
//											for(Class<?> key: servicetypes)
//											{
//												getServiceRegistry().removeService(new ClassInfo(key), fservice);
//											}
//											
//											serviceShutdowned(fservice).addResultListener(new DelegationResultListener<Void>(ret));
//										}
//										
//										public void exceptionOccurred(Exception exception)
//										{
//											exception.printStackTrace();
//											super.exceptionOccurred(exception);
//										}
//									});
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									exception.printStackTrace();
//									super.exceptionOccurred(exception);
//								}
//							});							
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

	/**
	 * 
	 */
	public IFuture<Collection<Class<?>>> getServiceTypes(final IServiceIdentifier sid)
	{
		final Future<Collection<Class<?>>> ret = new Future<Collection<Class<?>>>();
		getServiceType(sid).addResultListener(new ExceptionDelegationResultListener<Class<?>, Collection<Class<?>>>(ret)
		{
			public void customResultAvailable(Class<?> result)
			{
				// todo: cache results
				Set<Class<?>> res = new LinkedHashSet<Class<?>>();
				res.add(result);
				
				Class<?>[] sins = SReflect.getSuperInterfaces(new Class[]{result});
				for(Class<?> sin: sins)
				{
					if(sin.isAnnotationPresent(Service.class))
					{
						res.add(sin);
					}
				}
				
				ret.setResult(res);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<Class<?>> getServiceType(final IServiceIdentifier sid)
	{
		final Future<Class<?>> ret = new Future<Class<?>>();
		if(sid.getServiceType().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports())!=null)
		{
			ret.setResult(sid.getServiceType().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports())); // todo: only local? remote would cause nullpointer
		}
		else
		{
			ILibraryService ls = SServiceProvider.getLocalService(getComponent(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			ls.getClassLoader(sid.getResourceIdentifier())
				.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Class<?>>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					ret.setResult(sid.getServiceType().getType(cl));
				}
			});
		}
		return ret;
	}
	
	
	/**
	 *  Add a service to the component. 
	 *  @param type The service interface.
	 *  @param service The service.
	 *  @param proxytype	The proxy type (@see{BasicServiceInvocationHandler}).
	 */
	public IFuture<Void> addService(final String name, final Class<?> type, final String proxytype, 
		final IServiceInvocationInterceptor[] ics, final Object service, final ProvidedServiceInfo info, String scope)
	{
		final Future<Void> ret = new Future<Void>();
		
//		System.out.println("addS:"+service);

		PublishEventLevel elm = getComponent().getComponentDescription().getMonitoring()!=null? getComponent().getComponentDescription().getMonitoring(): null;
		// todo: remove this? currently the level cannot be turned on due to missing interceptor
//		boolean moni = elm!=null? !PublishEventLevel.OFF.equals(elm.getLevel()): false; 
		
		boolean moni = elm!=null && !PublishEventLevel.OFF.equals(elm); 
		final IInternalService proxy = BasicServiceInvocationHandler.createProvidedServiceProxy(
			getComponent(), service, name, type, proxytype, ics, moni, 
			info, scope!=null ? scope : info!=null? info.getScope(): null);
		
		addService(proxy, info).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				initService(proxy).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
				
		return ret;
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
	 *  Add a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 *  @param pos The position (0=first, -1=last-1, i.e. one before method invocation).
	 */
	public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos)
	{
		BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(service);
		handler.addServiceInterceptor(interceptor, pos);
	}
	
	/**
	 *  Remove a service interceptor.
	 *  @param interceptor The interceptor.
	 *  @param service The service.
	 */
	public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service)
	{
		BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(service);
		handler.removeServiceInterceptor(interceptor);
	}
	
	/**
	 *  Get the interceptors of a service.
	 *  @param service The service.
	 *  @return The interceptors.
	 */
	public IServiceInvocationInterceptor[] getInterceptors(Object service)
	{
		BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)ProxyFactory.getInvocationHandler(service);
		return handler.getInterceptors();
	}
}
