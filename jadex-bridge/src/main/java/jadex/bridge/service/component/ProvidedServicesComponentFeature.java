package jadex.bridge.service.component;

import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Feature for provided services.
 */
public class ProvidedServicesComponentFeature	extends AbstractComponentFeature	implements IProvidedServicesFeature
{
	//-------- attributes --------
	
	/** The map of platform services. */
	protected Map<Class<?>, Collection<IInternalService>> services;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor for type level.
	 */
	public ProvidedServicesComponentFeature()
	{
	}
	
	/**
	 *  Factory method constructor for instance level.
	 */
	protected ProvidedServicesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IComponentFeature interface / type level --------
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return IProvidedServicesFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 *  @param access	The access of the component.
	 *  @param info	The creation info.
	 */
	public IComponentFeature	createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new ProvidedServicesComponentFeature(access, info);
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
			
			// Instantiate service objects
			for(ProvidedServiceInfo info: sermap.values())
			{
				final ProvidedServiceImplementation	impl = info.getImplementation();
				// Virtual service (e.g. promoted)
				if(impl!=null && impl.getBinding()!=null)
				{
					RequiredServiceInfo rsi = new RequiredServiceInfo(BasicService.generateServiceName(info.getType().getType( 
						component.getClassLoader(), component.getModel().getAllImports()))+":virtual", info.getType().getType(component.getClassLoader(), component.getModel().getAllImports()));
					IServiceIdentifier sid = BasicService.createServiceIdentifier(component.getComponentIdentifier(), 
						rsi.getName(), rsi.getType().getType(component.getClassLoader(), component.getModel().getAllImports()),
						BasicServiceInvocationHandler.class, component.getModel().getResourceIdentifier(), info.getScope());
					final IInternalService service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(
						component, sid, rsi, impl.getBinding(), component.getClassLoader(), cinfo.isRealtime());
					
					addService(service, info);
				}
				else
				{
					Object ser = createServiceImplementation(info);
					
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
							component, ser, info.getName(), type, info.getImplementation().getProxytype(), ics, cinfo.isCopy(), 
							cinfo.isRealtime(), moni, info, info.getScope());
						
						addService(proxy, info);
					}
				}
			}
			
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
//					allservices.addAll(it.next());
				}
				initServices(allservices.iterator()).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Execute the main activity of the feature.
	 */
	public IFuture<Void>	body()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Shutdown the feature.
	 */
	public IFuture<Void>	shutdown()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Add a service.
	 *  @param service	The service object.
	 *  @param info	 The service info.
	 */
	protected void	addService(IInternalService service, ProvidedServiceInfo info)
	{
		// Find service types
		Class<?>	type	= info.getType().getType(component.getClassLoader(), component.getModel().getAllImports());
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
		{
			services = Collections.synchronizedMap(new LinkedHashMap<Class<?>, Collection<IInternalService>>());
		}

		for(Class<?> servicetype: types)
		{
			Collection<IInternalService> tmp = services.get(servicetype);
			if(tmp==null)
			{
				tmp = Collections.synchronizedList(new ArrayList<IInternalService>());
				services.put(servicetype, tmp);
			}
			tmp.add(service);
			
			// Make all services available immediately, even before start (hack???).
			((IPlatformComponentAccess)component).getServiceRegistry().addService(new ClassInfo(servicetype), service);
		}
	}
	
	/**
	 *  Create a service implementation from description.
	 */
	protected Object createServiceImplementation(ProvidedServiceInfo info)	throws Exception
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
				ser = SJavaParser.getParsedValue(impl, component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader());
//				System.out.println("added: "+ser+" "+model.getName());
			}
			catch(RuntimeException e)
			{
//				e.printStackTrace();
				throw new RuntimeException("Service creation error: "+info, e);
			}
		}
		else if(impl!=null && impl.getClazz().getType(component.getClassLoader(), component.getModel().getAllImports())!=null)
		{
			ser = impl.getClazz().getType(component.getClassLoader(), component.getModel().getAllImports()).newInstance();
		}
		
		return ser;
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
			component.getLogger().info("Starting service: "+is.getServiceIdentifier());
			is.setComponentAccess(component).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					is.startService().addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							component.getLogger().info("Started service: "+is.getServiceIdentifier());
							
							initServices(services).addResultListener(new DelegationResultListener<Void>(ret));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(exception);
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
			BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
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
}
