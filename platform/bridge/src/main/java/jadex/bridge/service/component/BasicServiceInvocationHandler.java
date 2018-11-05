package jadex.bridge.service.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.ServiceCall;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.CheckIndex;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.CheckState;
import jadex.bridge.service.annotation.Raw;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.DecouplingInterceptor;
import jadex.bridge.service.component.interceptors.DecouplingReturnInterceptor;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.component.interceptors.IntelligentProxyInterceptor;
import jadex.bridge.service.component.interceptors.MethodCallListenerInterceptor;
import jadex.bridge.service.component.interceptors.MethodInvocationInterceptor;
import jadex.bridge.service.component.interceptors.PrePostConditionInterceptor;
import jadex.bridge.service.component.interceptors.ResolveInterceptor;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureHelper;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

/**
 *  Basic service invocation interceptor.
 *  It has a multi collection of interceptors per method.
 *  Executes the list of interceptors one by one.
 *  In case no handler can be found a fallback handler is used.
 */
public class BasicServiceInvocationHandler implements InvocationHandler, ISwitchCall
{
	//-------- constants --------
	
	/** The raw proxy type (i.e. no proxy). */
	public static final String	PROXYTYPE_RAW	= "raw";
	
	/** The direct proxy type (supports custom interceptors, but uses caller thread). */
	public static final String	PROXYTYPE_DIRECT	= "direct";
	
	/** The (default) decoupled proxy type (decouples from caller thread to component thread). */
	public static final String	PROXYTYPE_DECOUPLED	= "decoupled";
	
	//-------- attributes --------

	/** The internal access. */
	protected IInternalAccess comp;
	
	// The proxy can be equipped with 
	// a) the IService Object
	// b) a service info object (for pojo services that separate basic service object and pojo service)
	// c) a service identifier that can be used to relay a call to another service
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The service. */
	protected Object service;
		

	/** The logger for errors/warnings. */
	protected Logger logger;

	/** The list of interceptors. */
	protected List<IServiceInvocationInterceptor> interceptors;
	
//	/** The root cause that was given at creation time. */
//	protected Cause cause;
	
//	/** The call id. */
//	protected AtomicLong callid;
	
	/** The flag if the proxy is required (provided otherwise). */
	protected boolean required;
	
	/** The flag if a switchcall should be done. */
	protected boolean switchcall;
	
	
	/** The pojo service map (pojo -> proxy). */
	protected static Map<Object, IService>	pojoproxies;

	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IInternalAccess comp, IServiceIdentifier sid, Logger logger, boolean required)
	{
//		assert cause!=null;
		this.comp = comp;
		this.sid = sid;
		this.logger	= logger;
//		this.cause = cause;
		this.switchcall = true;
		this.required	= required;
//		this.callid = new AtomicLong();
	}
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IInternalAccess comp, IService service, Logger logger, boolean required)
	{
//		assert cause!=null;
		this.comp = comp;
		this.service = service;
//		this.sid = service.getId();
		this.logger	= logger;
//		this.realtime	= realtime;
//		this.cause = cause;
		this.switchcall = false; 
		this.required	= required;
//		this.callid = new AtomicLong();
	}
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IInternalAccess comp, ServiceInfo service, Logger logger)//, Cause cause)
	{
//		assert cause!=null;
		this.comp = comp;
		this.service = service;
//		this.sid = service.getManagementService().getId();
		this.logger	= logger;
//		this.realtime	= realtime;
//		this.cause = cause;
		this.switchcall = false; // called for provided proxy which must not switch (is the object that is asked in the req proxy)
//		this.callid = new AtomicLong();
	}
	
//	/**
//	 *  Create a new invocation handler.
//	 */
//	public BasicServiceInvocationHandler(IInternalAccess comp, IResultCommand<IFuture<Object>, Void> searchcmd, Logger logger, Cause cause)
//	{
//		assert cause!=null;
//		this.comp = comp;
//		this.searchcmd = searchcmd;
////		this.sid = service.getManagementService().getId();
//		this.logger	= logger;
////		this.realtime	= realtime;
//		this.cause = cause;
//		this.switchcall = false; // called for provided proxy which must not switch (is the object that is asked in the req proxy)
////		this.callid = new AtomicLong();
//	}
	
	//-------- methods --------
	
	/**
	 *  A proxy method has been invoked.
	 */
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		Object ret = null;
		
//		final long callid = this.callid.getAndIncrement();
//		comp.getServiceContainer().notifyMethodListeners(getServiceIdentifier(), true, proxy, method, args, callid, null);
		
//		if(method.getName().indexOf("start")!=-1 && getServiceIdentifier().getServiceType().getTypeName().indexOf("Peer")!=-1)
//			System.out.println("call method start");
//		if(method.getName().indexOf("updateClientData")!=-1 && args[0]==null)// && getServiceIdentifier().getServiceType().getTypeName().indexOf("Peer")!=-1)
//			System.out.println("call method init");
		
//		ServiceInvocationContext sicon = null;
		
		// IT IS IMPORTANT TO HANDLE getSericeId() HERE. Otherwise random bug behavior might occur
		if((args==null || args.length==0) && "getServiceId".equals(method.getName()))
		{
			ret	= getServiceIdentifier();
		}
		else if(args!=null && args.length==1 && args[0]!=null && "equals".equals(method.getName()) && Object.class.equals(method.getParameterTypes()[0]))
		{
			Object	cmp	= ProxyFactory.isProxyClass(args[0].getClass()) ? ProxyFactory.getInvocationHandler(args[0]) : args[0];
			ret	= equals(cmp);
		}
		else if(method.getAnnotation(Raw.class)!=null)
		{
			Object ser;
			if(service instanceof IInternalService)
			{
				ser = service;
			}
			else if(service instanceof ServiceInfo)
			{
				ServiceInfo si = (ServiceInfo)service;
				if(ResolveInterceptor.SERVICEMETHODS.contains(method))
				{
					ser = si.getManagementService();
				}
				else
				{
					ser = si.getDomainService();
				}
			}
			else if(ProxyFactory.isProxyClass(service.getClass()) && 
				RemoteMethodInvocationHandler.class.equals(ProxyFactory.getInvocationHandler(service).getClass()))
			{
				ser = service;
			}
			else
			{
				throw new RuntimeException("Raw service cannot be invoked on: "+service);
			}
			
			ret = method.invoke(ser, args);
		}
		else if((args==null || args.length==0) && "hashCode".equals(method.getName()))
		{
//			System.out.println("hashcode on proxy: "+getServiceIdentifier().toString());
			ret	= hashCode();
		}
		else if((args==null || args.length==0) && "toString".equals(method.getName()))
		{
//			System.out.println("hashcode on proxy: "+getServiceIdentifier().toString());
			ret	= toString();
		}
		else
		{
			final ServiceInvocationContext sic = new ServiceInvocationContext(proxy, method, getInterceptors(), 
				getServiceIdentifier().getProviderId().getRoot(), getServiceIdentifier());//, cause);
//			sicon = sic;
			
//			if(method.getName().indexOf("getExternalAccess")!=-1 && sic.getLastServiceCall()==null)
//				System.out.println("call method ex");
			
			List<Object> myargs = args!=null? SUtil.arrayToList(args): null;
			
			if(SReflect.isSupertype(IFuture.class, method.getReturnType()))
			{
				final Future<Object> fret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), 
					new FutureFunctionality(logger));
//					new ServiceCallFutureFunctionality(logger, sic.getLastServiceCall(), method.getName()));
				ret	= fret;
//				System.out.println("fret: "+fret+" "+method);
//				fret.addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						System.out.println("fret res: "+result);
//					}
//					public void exceptionOccurred(Exception exception)
//					{
//						System.out.println("fret ex: "+exception);
//					}
//				});
//				if(method.getName().indexOf("addEntry")!=-1)
//					System.out.println("connect: ");
				sic.invoke(service, method, myargs).addResultListener(new ExceptionDelegationResultListener<Void, Object>(fret)
				{
					public void customResultAvailable(Void result)
					{
//						if(sic.getMethod().getName().indexOf("test")!=-1)
//							System.out.println("connect: "+sic.getMethod().getName());
//						if(method.getName().indexOf("start")!=-1 && getServiceIdentifier().getServiceType().getTypeName().indexOf("Peer")!=-1)
//							System.out.println("call method start end");
//						if(method.getName().indexOf("init")!=-1 && getServiceIdentifier().getServiceType().getTypeName().indexOf("Peer")!=-1)
//							System.out.println("call method init");
						try
						{
							// Although normally ret.getResult() is a future there are cases when not
							// because of mapping the method during the call (could be future method and inner one is not)
							if(sic.getResult() instanceof Exception)
							{
								fret.setException((Exception)sic.getResult());
							}
							else if(sic.getResult()!=null && !(sic.getResult() instanceof IFuture))
							{
								fret.setResult(sic.getResult());
							}
							else
							{
								FutureFunctionality.connectDelegationFuture((Future<?>)fret, (IFuture<?>)sic.getResult());
							}
						}
						catch(Exception e)
						{
							fret.setException(e);
						}
					}
				});
			}
//			else if(method.getReturnType().equals(void.class))
//			{
//				IFuture<Void> myvoid = sic.invoke(service, method, myargs);
//				
//				// Wait for the call to return to be able to throw exceptions
//				myvoid.get();
//				ret = sic.getResult();
//				
//				// Check result and propagate exception, if any.
//				// Do not throw exception as user code should not differentiate between local and remote case.
//	//			if(myvoid.isDone())
//	//			{
//	//				myvoid.get(null);	// throws exception, if any.
//	//			}
//	//			else
//				{
//					myvoid.addResultListener(new IResultListener<Void>()
//					{
//						public void resultAvailable(Void result)
//						{
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							logger.warning("Exception in void method call: "+method+" "+getServiceIdentifier()+" "+exception);
//						}
//					});
//				}
//			}
			else
			{
//				if(method.getName().indexOf("Void")!=-1)
//					System.out.println("sdfdf");
				IFuture<Void> fut = sic.invoke(service, method, myargs);
				if(fut.isDone())
				{
					//fut.get();	
					ret = sic.getResult();
				}
				else
				{
					// Try again after triggering delayed notifications.
					FutureHelper.notifyStackedListeners();
					if(fut.isDone())
					{
//						System.out.println("stacked method: "+method);
						ret = sic.getResult();
					}
					else
					{
//						logger.warning("Warning, blocking call: "+method.getName()+" "+getServiceIdentifier());
						// Waiting for the call is ok because of component suspendable
						fut.get();
						ret = sic.getResult();
					}
				}
				if(ret instanceof Throwable)
					SUtil.rethrowAsUnchecked((Throwable)ret);
			}
		}
		
//		final ServiceInvocationContext fsicon = sicon;
//		if(ret instanceof IFuture)
//		{
//			((IFuture<Object>)ret).addResultListener(new IResultListener<Object>()
//			{
//				public void resultAvailable(Object result)
//				{
//					comp.getServiceContainer().notifyMethodListeners(getServiceIdentifier(), false, proxy, method, args, callid, fsicon);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					comp.getServiceContainer().notifyMethodListeners(getServiceIdentifier(), false, proxy, method, args, callid, fsicon);
//				}
//			});
//		}
//		else
//		{
//			comp.getServiceContainer().notifyMethodListeners(getServiceIdentifier(), false, proxy, method, args, callid, fsicon);
//		}
		
		return ret;
	}
	
	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		if(sid==null)
		{
			// Hack!!! Preserve call context after getServiceIdentifier()
			ServiceCall	sc	= CallAccess.getNextInvocation();
			CallAccess.resetNextInvocation();
			
			sid = service instanceof ServiceInfo? ((ServiceInfo)service).getManagementService().getServiceId():
				((IService)service).getServiceId();
			
			CallAccess.setNextInvocation(sc);
		}
		return sid;
	}
	
	/**
	 *  Get the service.
	 *  @return The service.
	 */
	public Object getService()
	{
		return service;
	}
	
	/**
	 *  Get the domain service.
	 *  @return The domain service.
	 */
	public Object getDomainService()
	{
		return service instanceof ServiceInfo? ((ServiceInfo)service).getDomainService(): service;
	}

	/**
	 *  Add an interceptor.
	 *  
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void addFirstServiceInterceptor(IServiceInvocationInterceptor interceptor)
	{
		if(interceptors==null)
			interceptors = new ArrayList<IServiceInvocationInterceptor>();
		interceptors.add(0, interceptor);
	}
	
	/**
	 *  Add an interceptor.
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void addServiceInterceptor(IServiceInvocationInterceptor interceptor, int pos)
	{
		if(interceptors==null)
			interceptors = new ArrayList<IServiceInvocationInterceptor>();
		// Hack? -1 for default position one before method invocation interceptor
		interceptors.add(pos>-1? pos: interceptors.size()-1, interceptor);
	}
	
	/**
	 *  Add an interceptor.
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void addServiceInterceptor(IServiceInvocationInterceptor interceptor)
	{
		addServiceInterceptor(interceptor, -1);
	}
	
	/**
	 *  Remove an interceptor.
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized void removeServiceInterceptor(IServiceInvocationInterceptor interceptor)
	{
		if(interceptors!=null)
			interceptors.remove(interceptor);
	}
	
	/**
	 *  Get interceptors.
	 *  Must be synchronized as invoke() is called from arbitrary threads.
	 */
	public synchronized IServiceInvocationInterceptor[] getInterceptors()
	{
		return interceptors==null || interceptors.size()==0? null://new IServiceInvocationInterceptor[]{fallback}: 
			(IServiceInvocationInterceptor[])interceptors.toArray(new IServiceInvocationInterceptor[interceptors.size()]);
	}
	
	//-------- replacement methods for service proxies --------
	
	/**
	 *  Return the hash code.
	 */
	public int hashCode()
	{
		return 31+getServiceIdentifier().hashCode();
	}
	
	/**
	 *  Test if two objects are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof BasicServiceInvocationHandler && ((BasicServiceInvocationHandler)obj).getServiceIdentifier().equals(getServiceIdentifier());
	}
	
	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return getServiceIdentifier().toString();
	}
	
	//-------- static methods --------
	
	/**
	 *  Static method for creating a standard service proxy for a provided service.
	 */
	public static IInternalService createProvidedServiceProxy(IInternalAccess ia, Object service, 
		String name, Class<?> type, String proxytype, IServiceInvocationInterceptor[] ics, 
		boolean monitoring, ProvidedServiceInfo info, ServiceScope scope)
	{
		IServiceIdentifier sid = null;
		
		if(isProvidedServiceProxy(service))
		{
			System.out.println("Already provided service proxy: "+service);
			return (IInternalService)service;
		}
		
		IInternalService ret;
		
		if(!SReflect.isSupertype(type, service.getClass()))
			throw new RuntimeException("Service implementation '"+service.getClass().getName()+"' does not implement service interface: "+type.getName());
		
		if(service instanceof IInternalService)
		{
			sid = BasicService.createServiceIdentifier(ia, name, type, service.getClass(), ia.getModel().getResourceIdentifier(), scope);
			((IInternalService)service).setServiceIdentifier(sid);
		}
			
		
//		if(type.getName().indexOf("IServiceCallService")!=-1)
//			System.out.println("hijijij");
		
		if(!PROXYTYPE_RAW.equals(proxytype) || (ics!=null && ics.length>0))
		{
			BasicServiceInvocationHandler handler = createProvidedHandler(name, ia, type, service, info, scope);
			if(sid==null)
			{
				Object ser = handler.getService();
				if(ser instanceof ServiceInfo)
					sid = ((ServiceInfo)ser).getManagementService().getServiceId();
			}
			ret	= (IInternalService)ProxyFactory.newProxyInstance(ia.getClassLoader(), new Class[]{IInternalService.class, type}, handler);
//			try
//			{
//				((IService)service).getServiceIdentifier();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
			
			BasicServiceInvocationHandler.addProvidedInterceptors(handler, service, ics, ia, proxytype, monitoring, sid!=null? sid: ret.getServiceId());
//			ret	= (IInternalService)Proxy.newProxyInstance(ia.getExternalAccess()
//				.getModel().getClassLoader(), new Class[]{IInternalService.class, type}, handler);
			if(!(service instanceof IService))
			{
				if(!service.getClass().isAnnotationPresent(Service.class)
					// Hack!!! BPMN uses a proxy as service implementation.
					&& !(ProxyFactory.isProxyClass(service.getClass())
					&& ProxyFactory.getInvocationHandler(service).getClass().isAnnotationPresent(Service.class)))
				{
					//throw new RuntimeException("Pojo service must declare @Service annotation: "+service.getClass());
					ia.getLogger().warning("Pojo service should declare @Service annotation: "+service.getClass());
//					throw new RuntimeException("Pojo service must declare @Service annotation: "+service.getClass());
				}
				addPojoServiceProxy(service, ret);
			}
		}
		else
		{
			if(service instanceof IInternalService)
			{
				ret	= (IInternalService)service;
			}
			else
			{
				throw new RuntimeException("Raw services must implement IInternalService (e.g. by extending BasicService): " + service.getClass().getCanonicalName());
			}
		}
		return ret;
	}
	
	/**
	 *  Create a basic invocation handler.
	 */
	protected static BasicServiceInvocationHandler createProvidedHandler(String name, IInternalAccess ia, Class<?> type, Object service, ProvidedServiceInfo info, ServiceScope scope)
	{
//		if(type.getName().indexOf("ITestService")!=-1 && ia.getComponentIdentifier().getName().startsWith("Global"))
//			System.out.println("gaga");
		
		Map<String, Object> serprops = new HashMap<String, Object>();
		if(info != null && info.getProperties() != null)
		{
			for(UnparsedExpression exp : info.getProperties())
			{
				Object val = SJavaParser.parseExpression(exp, ia.getModel().getAllImports(), ia.getClassLoader()).getValue(null);
				serprops.put(exp.getName(), val);
			}
		}
		
		BasicServiceInvocationHandler handler;
		if(service instanceof IService)
		{
			IService ser = (IService)service;
			
			if(service instanceof BasicService)
			{
				serprops.putAll(((BasicService)service).getPropertyMap());
				((BasicService)service).setPropertyMap(serprops);
			}
			
			handler = new BasicServiceInvocationHandler(ia, ser, ia.getLogger(), false);
			
//			if(type==null)
//			{
//				type = ser.getServiceIdentifier().getServiceType();
//			}
//			else if(!type.equals(ser.getServiceIdentifier().getServiceType()))
//			{
//				throw new RuntimeException("Service does not match its type: "+type+", "+ser.getServiceIdentifier().getServiceType());
//			}
		}
		else
		{
			if(type==null)
			{
				// Try to find service interface via annotation
				if(service.getClass().isAnnotationPresent(Service.class))
				{
					Service si = (Service)service.getClass().getAnnotation(Service.class);
					if(!si.value().equals(Object.class))
					{
						type = si.value();
					}
				}
				// Otherwise take interface if there is only one
				else
				{
					Class<?>[] types = service.getClass().getInterfaces();
					if(types.length!=1)
						throw new RuntimeException("Unknown service interface: "+SUtil.arrayToString(types));
					type = types[0];
				}
			}
			Class<?> serclass = service.getClass();

			BasicService mgmntservice = new BasicService(ia.getId(), type, serclass, null);
			mgmntservice.setServiceIdentifier(BasicService.createServiceIdentifier(ia, name, type, service.getClass(), ia.getModel().getResourceIdentifier(), scope));
			serprops.putAll(mgmntservice.getPropertyMap());
			mgmntservice.setPropertyMap(serprops);
			
			// Do not try to call isAnnotationPresent for Proxy on Android
			// see http://code.google.com/p/android/issues/detail?id=24846
			if(!(SReflect.isAndroid() && ProxyFactory.isProxyClass(serclass)))
			{
				while(!Object.class.equals(serclass))
				{
					Field[] fields = serclass.getDeclaredFields();
					for(int i=0; i<fields.length; i++)
					{
						if(fields[i].isAnnotationPresent(ServiceIdentifier.class))
						{
							ServiceIdentifier si = (ServiceIdentifier)fields[i].getAnnotation(ServiceIdentifier.class);
							if(si.value().equals(Object.class) || si.value().equals(type))
							{
								if(SReflect.isSupertype(IServiceIdentifier.class, fields[i].getType()))
								{
									try
									{
										fields[i].setAccessible(true);
										fields[i].set(service, mgmntservice.getServiceId());
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
								else
								{
									throw new RuntimeException("Field cannot store IServiceIdentifer: "+fields[i]);
								}
							}
						}
						
						if(fields[i].isAnnotationPresent(ServiceComponent.class))
						{
							Object val	= ia.getParameterGuesser().guessParameter(fields[i].getType(), false);
							try
							{
								fields[i].setAccessible(true);
								fields[i].set(service, val);
							}
							catch(Exception e)
							{
//								e.printStackTrace();
								throw new RuntimeException(e);
							}
						}
					}
					serclass = serclass.getSuperclass();
				}
			}
			
			ServiceInfo si = new ServiceInfo(service, mgmntservice);
			handler = new BasicServiceInvocationHandler(ia, si, ia.getLogger());//, ia.getDescription().getCause());
			
//			addPojoServiceIdentifier(service, mgmntservice.getServiceIdentifier());
		}
		
		return handler;
	}
	
	/**
	 *  Add the standard and custom interceptors.
	 */
	protected static void addProvidedInterceptors(BasicServiceInvocationHandler handler, Object service, 
		IServiceInvocationInterceptor[] ics, IInternalAccess ia, String proxytype, 
		boolean monitoring, IServiceIdentifier sid)
	{
//		System.out.println("addI:"+service);

		// Only add standard interceptors if not raw.
		if(!PROXYTYPE_RAW.equals(proxytype))
		{
			handler.addFirstServiceInterceptor(new MethodInvocationInterceptor());
//			if(monitoring)
//				handler.addFirstServiceInterceptor(new MonitoringInterceptor(ia));
//			handler.addFirstServiceInterceptor(new AuthenticationInterceptor(ia, false));
			
			
			try
			{
				Class<?> clazz = sid.getServiceType().getType(ia.getClassLoader());
				boolean addhandler = false;
				Method[] ms = SReflect.getAllMethods(clazz);
				
				formethod:
				for (Method m : ms)
				{
					Annotation[] as = m.getAnnotations();
					for (Annotation anno : as)
						if (anno instanceof CheckNotNull 
							|| anno instanceof CheckState
							|| anno instanceof CheckIndex)
						{
							addhandler = true;
							break formethod;
						}
				}
				if (addhandler)
					handler.addFirstServiceInterceptor(new PrePostConditionInterceptor(ia));
			}
			catch (Exception e)
			{
			}
			
			if(!(service instanceof IService))
				handler.addFirstServiceInterceptor(new ResolveInterceptor(ia));
			
			handler.addFirstServiceInterceptor(new MethodCallListenerInterceptor(ia, sid));
//			handler.addFirstServiceInterceptor(new ValidationInterceptor(ia));
			if(!PROXYTYPE_DIRECT.equals(proxytype))
				handler.addFirstServiceInterceptor(new DecouplingInterceptor(ia, Starter.isParameterCopy(sid.getProviderId()), false));
			handler.addFirstServiceInterceptor(new DecouplingReturnInterceptor());
			
			// used only by global service pool, todo add contionally
			handler.addFirstServiceInterceptor(new IntelligentProxyInterceptor(ia.getExternalAccess(), sid));
		}
		
		if(ics!=null)
		{
			for(int i=0; i<ics.length; i++)
			{
				handler.addServiceInterceptor(ics[i], -1);
			}
		}
	}
	
	/**
	 *  Static method for creating a delegation service proxy for 
	 *  provided service that is not offered by the component itself.
	 */
	// TODO: not currently used in apps???
	public static IInternalService createDelegationProvidedServiceProxy(IInternalAccess ia, IServiceIdentifier sid, 
		RequiredServiceInfo info, RequiredServiceBinding binding, ClassLoader classloader, boolean realtime)
	{
		BasicServiceInvocationHandler handler = new BasicServiceInvocationHandler(ia, sid, ia.getLogger(), false);//, ia.getDescription().getCause(), false);
		handler.addFirstServiceInterceptor(new MethodInvocationInterceptor());
//		handler.addFirstServiceInterceptor(new DelegationInterceptor(ia, info, binding, null, sid, realtime));	// TODO
		handler.addFirstServiceInterceptor(new DecouplingReturnInterceptor(/*ea, null,*/));
		return (IInternalService)ProxyFactory.newProxyInstance(classloader, new Class[]{IInternalService.class, info.getType().getType(classloader, ia.getModel().getAllImports())}, handler); //sid.getServiceType()
	}

	/**
	 *  Static method for creating a standard service proxy for a required service.
	 */
	public static IService createRequiredServiceProxy(IInternalAccess ia, IService service, 
		IRequiredServiceFetcher fetcher, RequiredServiceInfo info, RequiredServiceBinding binding, boolean realtime)
	{
		if(isRequiredServiceProxy(service))
		{
			System.out.println("Already required service proxy: "+service);
			return service;
		}
		
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("IServiceCallService")!=-1)
//			System.out.println("hijijij");

//		System.out.println("cRSP:"+service.getServiceIdentifier());
		IService ret = service;
		
		if(binding==null || !PROXYTYPE_RAW.equals(binding.getProxytype()))
		{
	//		System.out.println("create: "+service.getServiceIdentifier().getServiceType());
			BasicServiceInvocationHandler handler = new BasicServiceInvocationHandler(ia, service, ia.getLogger(), true); // ia.getDescription().getCause()
			handler.addFirstServiceInterceptor(new MethodInvocationInterceptor());
//			handler.addFirstServiceInterceptor(new AuthenticationInterceptor(ia, true));
			// Dropped for v4???
//			if(binding!=null && binding.isRecover())
//				handler.addFirstServiceInterceptor(new RecoveryInterceptor(ia.getExternalAccess(), info, binding, fetcher));
			if(binding==null || PROXYTYPE_DECOUPLED.equals(binding.getProxytype())) // done on provided side
				handler.addFirstServiceInterceptor(new DecouplingReturnInterceptor());
			handler.addFirstServiceInterceptor(new MethodCallListenerInterceptor(ia, service.getServiceId()));
//			handler.addFirstServiceInterceptor(new NFRequiredServicePropertyProviderInterceptor(ia, service.getId()));
			UnparsedExpression[] interceptors = binding!=null ? binding.getInterceptors() : null;
			if(interceptors!=null && interceptors.length>0)
			{
				for(int i=0; i<interceptors.length; i++)
				{
					IServiceInvocationInterceptor interceptor = (IServiceInvocationInterceptor)SJavaParser.evaluateExpression(
//						interceptors[i].getValue(), ea.getModel().getAllImports(), ia.getFetcher(), ea.getModel().getClassLoader());
						interceptors[i].getValue(), ia.getModel().getAllImports(), ia.getFetcher(), ia.getClassLoader());
					handler.addServiceInterceptor(interceptor);
				}
			}
			// Decoupling interceptor on required chains ensures that wrong incoming calls e.g. from gui thread
			// are automatically pushed to the req component thread
			if(binding==null || PROXYTYPE_DECOUPLED.equals(binding.getProxytype())) // done on provided side
				handler.addFirstServiceInterceptor(new DecouplingInterceptor(ia, false, true));
//			ret = (IService)Proxy.newProxyInstance(ea.getModel().getClassLoader(), new Class[]{IService.class, 
			// service.getServiceIdentifier().getServiceType()
//			ret = (IService)Proxy.newProxyInstance(ia.getClassLoader(), new Class[]{IService.class, INFRPropertyProvider.class, info.getType().getType(ia.getClassLoader(), ia.getModel().getAllImports())}, handler); 
			Class<?> ty = info.getType()!=null? info.getType().getType(ia.getClassLoader(), ia.getModel().getAllImports()): null;
			if(ty==null)
			{
//				throw new IllegalArgumentException("Type must not null: "+ty);
				ret = (IService)ProxyFactory.newProxyInstance(ia.getClassLoader(), new Class[]{IService.class}, handler); 
			}
			else
			{
				ret = (IService)ProxyFactory.newProxyInstance(ia.getClassLoader(), new Class[]{IService.class, ty}, handler); 	
			}
			// todo: think about orders of decouping interceptors
			// if we want the decoupling return interceptor to schedule back on an external caller actual order must be reversed
			// now it can only schedule back on the hosting component of the required proxy
		}
		
		return ret;
	}
	
	/**
	 *  Add a service proxy.
	 *  @param pojo The pojo.
	 *  @param proxy The proxy.
	 */
	public static void addPojoServiceProxy(Object pojo, IService proxy)
	{
//		System.out.println("add pojoproxy: "+proxy.getServiceIdentifier());
		
		synchronized(BasicServiceInvocationHandler.class)
		{
			if(pojoproxies==null)
				pojoproxies = new IdentityHashMap<Object, IService>();
			pojoproxies.put(pojo, proxy);
		}
	}
	
	/**
	 *  Remove a pojo - proxy pair.
	 *  @param sid The service identifier.
	 */
	public static void removePojoServiceProxy(IServiceIdentifier sid)
	{
		synchronized(BasicServiceInvocationHandler.class)
		{
			for(Iterator<IService> it=pojoproxies.values().iterator(); it.hasNext(); )
			{
				IService proxy = it.next();
				
				if(sid.equals(proxy.getServiceId()))
				{
					it.remove();
					break;
//					System.out.println("rem: "+pojosids.size());	
				}
			}
		}
	}
	
	/**
	 *  Get the proxy of a pojo service.
	 *  @param pojo The pojo service.
	 *  @return The proxy of the service.
	 */
	public static IService getPojoServiceProxy(Object pojo)
	{
		synchronized(BasicServiceInvocationHandler.class)
		{
			return pojoproxies.get(pojo);
		}
	}
	
	/**
	 *  Check if a switch call should be done.
	 *  @return True, if switch should be done.
	 */
	public boolean isSwitchCall()
	{
		return switchcall;
	}
	
	/**
	 *  Check if the handler is for a required service proxy.
	 */
	public boolean isRequired()
	{
		return required;
	}
	
//	/**
//	 *  Add a method listener.
//	 */
//	public void addMethodListener(MethodInfo m, IMethodInvocationListener listener)
//	{
//		if(methodlisteners==null)
//			methodlisteners = new HashMap<MethodInfo, List<IMethodInvocationListener>>();
//		List<IMethodInvocationListener> lis = methodlisteners.get(m);
//		if(lis==null)
//		{
//			lis = new ArrayList<IMethodInvocationListener>();
//			methodlisteners.put(m, lis);
//		}
//		lis.add(listener);
//	}
//	
//	/**
//	 *  Add a method listener.
//	 */
//	public void removeMethodListener(MethodInfo m, IMethodInvocationListener listener)
//	{
//		if(methodlisteners!=null)
//		{
//			List<IMethodInvocationListener> lis = methodlisteners.get(m);
//			if(lis!=null)
//			{
//				lis.remove(listener);
//			}
//		}
//	}
//	
//	/**
//	 *  Notify registered listeners in case a method is called.
//	 */
//	protected void notifyMethodListeners(boolean start, Object proxy, final Method method, final Object[] args, long callid)
//	{
//		if(methodlisteners!=null)
//		{
//			doNotifyListeners(start, proxy, method, args, callid, methodlisteners.get(null));
//			doNotifyListeners(start, proxy, method, args, callid, methodlisteners.get((new MethodInfo(method))));
//		}
//	}
//	
//	/**
//	 *  Do notify the listeners.
//	 */
//	protected void doNotifyListeners(boolean start, Object proxy, final Method method, final Object[] args, long callid, List<IMethodInvocationListener> lis)
//	{
//		if(lis!=null)
//		{
//			for(IMethodInvocationListener ml: lis)
//			{
//				if(start)
//				{
//					ml.methodCallStarted(proxy, method, args, callid);
//				}
//				else
//				{
//					ml.methodCallFinished(proxy, method, args, callid);
//				}
//			}
//		}
//	}
	
//	/**
//	 * 
//	 */
//	public static void addPojoServiceIdentifier(Object pojo, IServiceIdentifier sid)
//	{
//		if(pojosids==null)
//		{
//			synchronized(BasicServiceInvocationHandler.class)
//			{
//				if(pojosids==null)
//				{
//					pojosids = Collections.synchronizedMap(new HashMap());
//				}
//			}
//		}
//		pojosids.put(pojo, sid);
////		System.out.println("add: "+pojosids.size());
//	}
//	
//	/**
//	 * 
//	 */
//	public static void removePojoServiceIdentifier(IServiceIdentifier sid)
//	{
//		if(pojosids!=null)
//		{
//			pojosids.values().remove(sid);
////			System.out.println("rem: "+pojosids.size());
//		}
//	}
//	
//	/**
//	 * 
//	 */
//	public static IServiceIdentifier getPojoServiceIdentifier(Object pojo)
//	{
//		return (IServiceIdentifier)pojosids.get(pojo);
//	}
	
	/**
	 *  Test if a service is a required service proxy.
	 *  @param service The service.
	 *  @return True, if is required service proxy.
	 */
	public static boolean isRequiredServiceProxy(Object service)
	{
		boolean ret = false;
		if(ProxyFactory.isProxyClass(service.getClass()))
		{
			Object tmp = ProxyFactory.getInvocationHandler(service);
			if(tmp instanceof BasicServiceInvocationHandler)
			{
				BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)tmp;
				ret = handler.isRequired();
			}
		}
		return ret;
	}
	
	/**
	 *  Test if a service is a provided service proxy.
	 *  @param service The service.
	 *  @return True, if is provided service proxy.
	 */
	public static boolean isProvidedServiceProxy(Object service)
	{
		boolean ret = false;
		if(ProxyFactory.isProxyClass(service.getClass()))
		{
			Object tmp = ProxyFactory.getInvocationHandler(service);
			if(tmp instanceof BasicServiceInvocationHandler)
			{
				BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)tmp;
				ret = !handler.isRequired();
			}
		}
		return ret;
	}
}


