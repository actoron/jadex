package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.base.service.remote.xml.RMIPostProcessor;
import jadex.base.service.remote.xml.RMIPreProcessor;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.IProxyable;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.AnyResultSelector;
import jadex.commons.service.BasicService;
import jadex.commons.service.IResultSelector;
import jadex.commons.service.ISearchManager;
import jadex.commons.service.IService;
import jadex.commons.service.IVisitDecider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.TypeResultSelector;
import jadex.commons.service.clock.ITimer;
import jadex.commons.service.library.ILibraryService;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *  The remote service management service is responsible for 
 *  handling remote service invocations (similar to RMI).
 */
public class RemoteServiceManagementService extends BasicService implements IRemoteServiceManagementService
{
	//-------- constants --------
	
	/** Excluded remote methods (for all methods)
	 *	Excluded methods throw UnsupportedOperationException. */
	public static String REMOTE_EXCLUDED = "remote_excluded";
	
	/** Uncached remote methods (for methods with no parameters)
	 *	Uncached methods will be invoked on every call. */
	public static String REMOTE_UNCACHED = "remote_uncached";
	
	/** Synchronous remote methods (for methods with void return value). 
     *	If void methods are declared synchronous they will block the caller until
     *	the method has been executed on the remote side (exception thus can arrive). */
	public static String REMOTE_SYNCHRONOUS = "remote_synchronous";

	/** Replacement methods to be executed instead of remote method invocation. */
	public static String REMOTE_METHODREPLACEMENT = "remote_methodreplacement";

	/** The default timeout. */
	public static long DEFAULT_TIMEOUT = 10000;
	
	
	/** The cache of proxy infos. */
	protected static Map proxyinfos = Collections.synchronizedMap(new LRU(200));

	/** The remote interface properties. */
	protected static Map interfaceproperties = Collections.synchronizedMap(new HashMap());
	
	//-------- attributes --------
	
	/** The component. */
	protected IMicroExternalAccess component;
	
	/** The call context. */
	protected CallContext context;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote service management service.
	 */
	public RemoteServiceManagementService(IMicroExternalAccess component)
	{
		super(component.getServiceProvider().getId(), IRemoteServiceManagementService.class, null);

		this.component = component;
		
		QName[] proxyinfo = new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.base.service.remote", "ProxyInfo")};
		
		Set typeinfosread = JavaReader.getTypeInfos();
		TypeInfo ti_proxyinfo = new TypeInfo(new XMLInfo(proxyinfo), 
			new ObjectInfo(ProxyInfo.class, new RMIPostProcessor(component)));
		typeinfosread.add(ti_proxyinfo);
		
		Set typeinfoswrite = JavaWriter.getTypeInfos();
		TypeInfo ti_proxyable = new TypeInfo(new XMLInfo(proxyinfo, null, false, new RMIPreProcessor(component.getComponentIdentifier())), 
			new ObjectInfo(IProxyable.class));
		typeinfoswrite.add(ti_proxyable);
		
		this.context = new CallContext(
			new Reader(new BeanObjectReaderHandler(typeinfosread)),
			new Writer(new BeanObjectWriterHandler(typeinfoswrite, true))
//			new Writer(new RMIObjectWriterHandler(JavaWriter.getTypeInfos(), component.getComponentIdentifier()))
		);
	}
	
	//-------- methods --------
	
	/**
	 *  Get a service proxies from a remote platform.
	 *  (called from arbitrary components)
	 *  @param cid Component id that is used to start the search.
	 *  @param manager The search manager.
	 *  @param decider The visit decider.
	 *  @param selector The result selector.
	 *  @return Collection or single result (i.e. service proxies). 
	 */
	public IFuture getServiceProxies(final IComponentIdentifier cid, 
		final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(component.getServiceProvider(), IComponentManagementService.class)
//			.addResultListener(component.createResultListener(new IResultListener()
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				
				// Hack! create remote rms cid with "rms" assumption.
				IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
				final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
				RemoteSearchCommand content = new RemoteSearchCommand(cid, manager, 
					decider, selector, callid);
				
				sendMessage(component, rrms, content, callid, -1, context, ret);
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get a service proxy from a remote platform.
	 *  (called from arbitrary components)
	 *  @param platform The component id of the remote platform.
	 *  @param providerid Optional component id that is used to start the search.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxy(IComponentIdentifier cid, final Class service)
	{
		return getServiceProxies(cid, SServiceProvider.sequentialmanager, SServiceProvider.abortdecider, 
			new TypeResultSelector(service, true));
	}
	
	/**
	 *  Get all service proxies from a remote platform.
	 *  (called from arbitrary components)
	 *  @param platform The component id of the remote platform.
	 *  @param providerid Optional component id that is used to start the search.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxies(IComponentIdentifier cid, final Class service)
	{
		return getServiceProxies(cid, SServiceProvider.sequentialmanager, SServiceProvider.contdecider, 
			new TypeResultSelector(service, true));
	}
	
	/**
	 *  Get all declared service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid The remote provider id.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getDeclaredServiceProxies(IComponentIdentifier cid)
	{
		return getServiceProxies(cid, SServiceProvider.localmanager, SServiceProvider.contdecider, 
			new AnyResultSelector(false, false));
	}
	
	/**
	 *  Get an external access proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid Component target id.
	 *  @return External access of remote component. 
	 */
	public IFuture getExternalAccessProxy(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(component.getServiceProvider(), IComponentManagementService.class)
//			.addResultListener(component.createResultListener(new IResultListener()
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				
				// Hack! create remote rms cid with "rms" assumption.
				IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
				final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
				RemoteGetExternalAccessCommand content = new RemoteGetExternalAccessCommand(cid, callid);
				
				sendMessage(component, rrms, content, callid, -1, context, ret);
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}

	
	/**
	 *  Get the component.
	 *  @return the component.
	 */
	public IExternalAccess getComponent()
	{
		return component;
	}
	
	/**
	 *  Get the waiting calls.
	 *  @return the waiting calls.
	 * /
	public Map getWaitingCalls()
	{
		return waitingcalls;
	}*/
	
	/**
	 *  Get the call context
	 *  @return The call context.
	 */
	public CallContext getCallContext()
	{
		return context;
	}
	
//	protected static Map errors = Collections.synchronizedMap(new LRU(200));
	
	/**
	 *  Send the request message of a remote method invocation.
	 */
	public static void sendMessage(final IMicroExternalAccess component, IComponentIdentifier receiver, final Object content,
		final String callid, final long to, final CallContext context, final Future future)
	{
		final long timeout = to<=0? DEFAULT_TIMEOUT: to;
		
		context.putWaitingCall(callid, future);
//		System.out.println("Waitingcalls: "+waitingcalls.size());
		
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
		msg.put(SFipa.CONVERSATION_ID, callid);
//		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		
		SServiceProvider.getService(component.getServiceProvider(), ILibraryService.class)
			.addResultListener(new IResultListener()
//			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(component.getServiceProvider(), IMessageService.class)
					.addResultListener(new IResultListener()
//					.addResultListener(component.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Hack!!! Manual encoding for using custom class loader at receiver side.
//						msg.put(SFipa.CONTENT, JavaWriter.objectToXML(content, ls.getClassLoader()));
						
						msg.put(SFipa.CONTENT, Writer.objectToXML(context.getWriter(), content, ls.getClassLoader(), context));

						IMessageService ms = (IMessageService)result;
						ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), ls.getClassLoader())
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// ok message could be sent.
								component.scheduleStep(new ICommand() 
								{
									public void execute(Object args) 
									{
//										System.out.println("waitfor");
										MicroAgent pa = (MicroAgent)args;
										pa.waitFor(timeout, new ICommand() 
										{
											public void execute(Object args) 
											{
//												System.out.println("timeout triggered: "+msg);
												context.removeWaitingCall(callid);
//												waitingcalls.remove(callid);
//												System.out.println("Waitingcalls: "+waitingcalls.size());
												future.setExceptionIfUndone(new RuntimeException("No reply received and timeout occurred: "+callid));
											}
										}).addResultListener(new DefaultResultListener()
//										}).addResultListener(component.createResultListener(new DefaultResultListener()
										{
											public void resultAvailable(Object source, Object result)
											{
												// cancel timer when future is finished before. 
												final ITimer timer = (ITimer)result;
												future.addResultListener(new IResultListener()
												{
													public void resultAvailable(Object source, Object result)
													{
														context.removeWaitingCall(callid);
//														waitingcalls.remove(callid);
//														System.out.println("Waitingcalls: "+waitingcalls.size());
//														System.out.println("Cancel timeout (res): "+callid+" "+future);
//														errors.put(callid, new Object[]{"Cancel timeout (res)", result});
														timer.cancel();
													}
													
													public void exceptionOccurred(Object source, Exception exception)
													{
														context.removeWaitingCall(callid);
//														waitingcalls.remove(callid);
//														System.out.println("Waitingcalls: "+waitingcalls.size());
//														System.out.println("Cancel timeout (ex): "+callid+" "+future);
//														errors.put(callid, new Object[]{"Cancel timeout (ex):", exception});
														timer.cancel();
													}
												});
											}
										});
									}
								});
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								// message could not be sent -> fail immediately.
//								System.out.println("Callee could not be reached: "+exception);
//								errors.put(callid, new Object[]{"Callee could not be reached", exception});
								context.removeWaitingCall(callid);
//								waitingcalls.remove(callid);
//								System.out.println("Waitingcalls: "+waitingcalls.size());
								future.setException(exception);
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
//						errors.put(callid, new Object[]{"No msg service", exception});
						context.removeWaitingCall(callid);
//						waitingcalls.remove(callid);
//						System.out.println("Waitingcalls: "+waitingcalls.size());
						future.setException(exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
//				errors.put(callid, new Object[]{"No lib service", exception});
				context.removeWaitingCall(callid);
//				waitingcalls.remove(callid);
//				System.out.println("Waitingcalls: "+waitingcalls.size());
				future.setException(exception);
			}
		});
	}
	
	//-------- management of proxy infos --------
	
	/**
	 *  Get a proxy info for a component. 
	 */
	public static ProxyInfo getProxyInfo(IComponentIdentifier rms, Object target, Class[] remoteinterfaces, CallContext context)
	{
		ProxyInfo ret;
		
		// todo: should all ids of remote objects be saved in table?
		Object tid;
		if(target instanceof IExternalAccess)
		{
			tid = ((IExternalAccess)target).getComponentIdentifier();
		}
		else if(target instanceof IService)
		{
			tid = ((IService)target).getServiceIdentifier();
		}
		else
		{
			tid = context.putTargetObject(target);
		}
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		
		Class targetclass = target.getClass();
		ret = (ProxyInfo)proxyinfos.get(targetclass);
		if(ret==null)
		{
			synchronized(proxyinfos)
			{
				ret = (ProxyInfo)proxyinfos.get(targetclass);
				if(ret==null)
				{
					ret = createProxyInfo(rms, target, tid, remoteinterfaces);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a proxy info for a service. 
	 */
	public static ProxyInfo createProxyInfo(IComponentIdentifier rms, Object target, Object tid, Class[] remoteinterfaces)
	{
		// todo: dgc, i.e. remember that target is a remote object (for which a proxyinfo is sent away).
		
		ProxyInfo ret = new ProxyInfo(rms, tid, remoteinterfaces);
		for(int i=0; i<remoteinterfaces.length; i++)
			fillProxyInfo(ret, target, remoteinterfaces[i]);
		
		// Add default replacement for equals() and hashCode().
		Class targetclass = target.getClass();
		Method	equals	= SReflect.getMethod(Object.class, "equals", new Class[]{Object.class});
		if(ret.getMethodReplacement(equals)==null)
		{
			MethodInfo[] mis = getMethodInfo(equals, targetclass, false);
			for(int i=0; i<mis.length; i++)
			{
				ret.addMethodReplacement(mis[i], new DefaultEqualsMethodReplacement());
			}
		}
		Method	hashcode = SReflect.getMethod(Object.class, "hashCode", new Class[0]);
		if(ret.getMethodReplacement(hashcode)==null)
		{
			MethodInfo[] mis = getMethodInfo(hashcode, targetclass, true);
			for(int i=0; i<mis.length; i++)
			{
				ret.addMethodReplacement(mis[i], new DefaultHashcodeMethodReplacement());
			}
		}
		// Add getClass as excluded. Otherwise the target class must be present on
		// the computer which only uses the proxy.
		Method getclass = SReflect.getMethod(Object.class, "getClass", new Class[0]);
		if(ret.getMethodReplacement(getclass)==null)
		{
			ret.addExcludedMethod(new MethodInfo(getclass));
		}
		
		return ret;
//		System.out.println("Creating proxy for: "+type);
	}	
	
	/**
	 *  Fill a proxy with method information.
	 */
	public static void fillProxyInfo(ProxyInfo pi, final Object target, Class remoteinterface)
	{
		Map properties = (Map)interfaceproperties.get(remoteinterface);
		
		// Hack! as long as registry misses
		if(properties==null && target instanceof IExternalAccess)
			properties = ((IExternalAccess)target).getModel().getProperties();		
		else if(properties==null && target instanceof IService)
			properties = ((IService)target).getPropertyMap();
		
		Class targetclass = target.getClass();
		
		// Check for excluded and synchronous methods.
		if(properties!=null)
		{
			Object ex = properties.get(RemoteServiceManagementService.REMOTE_EXCLUDED);
			if(ex!=null)
			{
				for(Iterator it = SReflect.getIterator(ex); it.hasNext(); )
				{
					MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
					for(int i=0; i<mis.length; i++)
					{
						pi.addExcludedMethod(mis[i]);
					}
				}
			}
			Object syn = properties.get(RemoteServiceManagementService.REMOTE_SYNCHRONOUS);
			if(syn!=null)
			{
				for(Iterator it = SReflect.getIterator(syn); it.hasNext(); )
				{
					MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
					for(int i=0; i<mis.length; i++)
					{
						pi.addSynchronousMethod(mis[i]);
					}
				}
			}
			Object un = properties.get(RemoteServiceManagementService.REMOTE_UNCACHED);
			if(un!=null)
			{
				for(Iterator it = SReflect.getIterator(un); it.hasNext(); )
				{
					MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
					for(int i=0; i<mis.length; i++)
					{
						pi.addUncachedMethod(mis[i]);
					}
				}
			}
			Object mr = properties.get(RemoteServiceManagementService.REMOTE_METHODREPLACEMENT);
			if(mr!=null)
			{
				for(Iterator it = SReflect.getIterator(mr); it.hasNext(); )
				{
					Object[] tmp = (Object[])it.next();
					MethodInfo[] mis = getMethodInfo(tmp[0], targetclass, false);
					for(int i=0; i<mis.length; i++)
					{
						pi.addMethodReplacement(mis[i], (IMethodReplacement)tmp[1]);
					}
				}
			}
			
			// Check methods and possibly cache constant calls.
			Method[] methods = remoteinterface.getMethods();
			methods	= (Method[])SUtil.joinArrays(methods, Object.class.getMethods());
			for(int i=0; i<methods.length; i++)
			{
				// only cache when not excluded, not cached and not replaced
				if(!pi.isUncached(methods[i]) && !pi.isExcluded(methods[i]) && !pi.isReplaced(methods[i])) 
				{
					Class rt = methods[i].getReturnType();
					Class[] ar = methods[i].getParameterTypes();
					
					if(void.class.equals(rt))
					{
	//					System.out.println("Warning, void method call will be executed asynchronously: "+type+" "+methods[i].getName());
					}
					else if(!(rt.isAssignableFrom(IFuture.class)))
					{
						if(ar.length>0)
						{
	//						System.out.println("Warning, service method is blocking: "+type+" "+methods[i].getName());
						}
						else
						{
							// Invoke method to get constant return value.
							try
							{
	//							System.out.println("Calling for caching: "+methods[i]);
								Object val = methods[i].invoke(target, new Object[0]);
								pi.putCache(methods[i].getName(), val);
							}
							catch(Exception e)
							{
								System.out.println("Warning, constant service method threw exception: "+remoteinterface+" "+methods[i]);
		//						e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Get method info.
	 */
	public static MethodInfo[] getMethodInfo(Object iden, Class targetclass, boolean noargs)
	{
		MethodInfo[] ret;
		
		if(iden instanceof String)
		{
			if(noargs)
			{
				Method	method	= SReflect.getMethod(targetclass, (String)iden, new Class[0]);
				if(method==null)
					method	= SReflect.getMethod(Object.class, (String)iden, new Class[0]);
				
				if(method!=null)
				{
					ret = new MethodInfo[]{new MethodInfo(method)};
				}
				else
				{
					throw new RuntimeException("Method not found: "+iden);
				}
			}
			else
			{
				Method[] ms = SReflect.getMethods(targetclass, (String)iden);
				if(ms.length==0)
				{
					ms = SReflect.getMethods(Object.class, (String)iden);
				}
				
				if(ms.length==1)
				{
					ret = new MethodInfo[]{new MethodInfo(ms[0])};
				}
				else if(ms.length>1)
				{
					// Exclude all if more than one fits?!
					ret = new MethodInfo[ms.length];
					for(int i=0; i<ret.length; i++)
						ret[i] = new MethodInfo(ms[i]);
					
					// Check if the methods are equal = same signature (e.g. defined in different interfaces)
//					boolean eq = true;
//					Method m0 = ms[0];
//					for(int i=1; i<ms.length && eq; i++)
//					{
//						if(!hasEqualSignature(m0, ms[i]))
//							eq = false;
//					}
//					if(!eq)
//						throw new RuntimeException("More than one method with the name availble: "+tmp);
//					else
//						ret = new MethodInfo(m0);
				}
				else
				{
					throw new RuntimeException("Method not found: "+iden);
				}
			}
		}
		else
		{
			ret = new MethodInfo[]{new MethodInfo((Method)iden)};
		}
		
		return ret;
	}

	/**
	 *  Create a proxy info for a service. 
	 * /
	public static ProxyInfo createProxyInfoForExternalAccess(IComponentIdentifier rms, IComponentIdentifier cid, IExternalAccess target)
	{
		Class targetclass = null;
		Class[] inter = target.getClass().getInterfaces();
		for(int i=0; i<inter.length && targetclass==null; i++)
		{
			if(SReflect.isSupertype(IExternalAccess.class, inter[0]));
				targetclass = inter[i]; 
		}
		if(targetclass==null)
			targetclass = IExternalAccess.class;
		
		ProxyInfo ret = new ProxyInfo(rms, cid, new Class[]{targetclass});
		
		// todo: Hack!!!
		// Exclude getServiceProvider() from remote external access interface
		Map props = target.getModel().getProperties();		
		fillProxyInfo(ret, target, targetclass, props);
		return ret;
//		System.out.println("Creating proxy for: "+type);
	}*/
	
	/**
	 *  Create a proxy info for a service. 
	 * /
	public static ProxyInfo createProxyInfoForService(IComponentIdentifier rms, IService service)
	{
		ProxyInfo ret = new ProxyInfo(rms, service.getServiceIdentifier(), new Class[]{service.getServiceIdentifier().getServiceType()});
		fillProxyInfo(ret, service, service.getServiceIdentifier().getServiceType(), service.getPropertyMap());
		return ret;
//		System.out.println("Creating proxy for: "+type);
	}*/
	
	/**
	 *  Get a proxy info for a service. 
	 * /
	public static ProxyInfo getProxyInfo(IComponentIdentifier rms, IService service)
	{
		ProxyInfo ret;
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		
		ret = (ProxyInfo)proxyinfos.get(service);
		if(ret==null)
		{
			synchronized(proxyinfos)
			{
				ret = (ProxyInfo)proxyinfos.get(service);
				if(ret==null)
				{
					ret = createProxyInfo(rms, service);
					proxyinfos.put(service, ret);
				}
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Get a proxy info for a component. 
	 * /
	public static ProxyInfo getProxyInfo(IComponentIdentifier rms, IComponentIdentifier cid, IExternalAccess target)
	{
		ProxyInfo ret;
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		
		ret = (ProxyInfo)proxyinfos.get(target);
		if(ret==null)
		{
			synchronized(proxyinfos)
			{
				ret = (ProxyInfo)proxyinfos.get(target);
				if(ret==null)
				{
					ret = createProxyInfo(rms, cid, target);
				}
			}
		}
		
		return ret;
	}*/
	
	//-------- management of proxies --------

	/**
	 *  Get a proxy for a proxy info.
	 */
	public static Object getProxy(IMicroExternalAccess rms, ProxyInfo pi, CallContext context)
	{
		// todo: check if the rms cid of the proxyinfo is the same as this rms
		// then it is the local object scope an the original object can be used
		// todo: table of proxies for remote references
		// return cached proxies
		
		return Proxy.newProxyInstance(rms.getModel().getClassLoader(), pi.getTargetInterfaces(),
			new RemoteMethodInvocationHandler(rms, pi, context));
	}
	
}

