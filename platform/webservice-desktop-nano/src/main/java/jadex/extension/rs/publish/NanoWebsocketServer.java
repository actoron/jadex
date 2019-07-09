package jadex.extension.rs.publish;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.activecomponents.webservice.ServiceInfo;
import org.activecomponents.webservice.messages.BaseMessage;
import org.activecomponents.webservice.messages.PartialMessage;
import org.activecomponents.webservice.messages.PullResultMessage;
import org.activecomponents.webservice.messages.ResultMessage;
import org.activecomponents.webservice.messages.ServiceInvocationMessage;
import org.activecomponents.webservice.messages.ServiceProvideMessage;
import org.activecomponents.webservice.messages.ServiceSearchMessage;
import org.activecomponents.webservice.messages.ServiceTerminateInvocationMessage;
import org.activecomponents.webservice.messages.ServiceUnprovideMessage;

import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.FutureReturnType;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.Base64;
import jadex.commons.FileFilter;
import jadex.commons.IFilter;
import jadex.commons.MethodInfo;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SClassReader.ClassFileInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.micro.MinimalAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.serialization.SerializationServices;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  The nano websocket server handles websocket requests from clients like browsers.
 *  
 *  // todo: use the reflection mechanism for method invocation
 *  
 */
public class NanoWebsocketServer extends NanoHttpServer
{
	/** The platform. */
	protected IExternalAccess agent;
	
	/** The ongoing future calls from client. */
	protected Map<String, IFuture<?>> incalls = new HashMap<String, IFuture<?>>();
	
	/** The ongoing future calls to the client. */
	protected Map<String, Future<?>> outcalls = new HashMap<String, Future<?>>();
	
	/** The read processors. */
	//protected List<ITraverseProcessor> readprocs;
	
	/** The write processors. */
	//protected List<ITraverseProcessor> writeprocs;
	
	/** The partial messages. 
	    todo: cleanup per timeout */
	protected Map<String, Map<Integer, String>> partials;

	/** debug flag (print stacktraces of unsuccessful service calls etc) **/
	protected boolean debug;
	
	/** The basic type converters. */
	protected BasicTypeConverter basicconverters;
	
	/** The websockets per session. */
	protected Map<IHTTPSession, MyWebSocket> websockets;
	
	/** The interface -> impl file mappings. */
	protected MultiCollection<String, String> mappings;
	
	/** The methodinfos per service interface. */
	protected Map<jadex.bridge.ClassInfo, MethodInfo[]> serviceinfos;
	
	/** 
	 *  Creates the server.
	 *  @param port Port of the server.
	 */
	public NanoWebsocketServer(int port, IExternalAccess agent, IRequestHandlerService handler)
	{
		super(port, handler);
		//Logger.getLogger(NanoHTTPD.class.getName()).setLevel(Level.OFF);
		//Logger.getLogger(NanoWSD.class.getName()).setLevel(Level.OFF);
		
		this.agent = agent;
		
		this.basicconverters = new BasicTypeConverter();
		
//		this.readprocs = JsonTraverser.getDefaultReadProcessorsCopy();
//		this.writeprocs = JsonTraverser.getDefaultWriteProcessorsCopy();
//		
//		writeprocs.add(0, new jadex.platform.service.serialization.serializers.jsonwrite.JsonServiceProcessor());
//		writeprocs.add(0, new jadex.platform.service.serialization.serializers.jsonwrite.JsonServiceIdentifierProcessor());
//		writeprocs.add(0, new jadex.platform.service.serialization.serializers.jsonwrite.JsonResourceIdentifierProcessor());
//		
//		readprocs.add(0, new jadex.platform.service.serialization.serializers.jsonread.JsonServiceProcessor());
//		readprocs.add(0, new jadex.platform.service.serialization.serializers.jsonread.JsonServiceIdentifierProcessor());
//		readprocs.add(0, new jadex.platform.service.serialization.serializers.jsonread.JsonComponentIdentifierProcessor());
//		readprocs.add(0, new jadex.platform.service.serialization.serializers.jsonread.JsonResourceIdentifierProcessor());
		
		this.partials = new HashMap<String, Map<Integer,String>>();
		this.websockets = Collections.synchronizedMap(new HashMap<>());
		
		this.serviceinfos = new HashMap<>();
	}
	
	@Override
    public void start() throws IOException 
	{
        super.start();
        
        ScheduledExecutorService exes = Executors.newSingleThreadScheduledExecutor();
//        ScheduledExecutorService exes = Executors.newSingleThreadScheduledExecutor(Void -> 
//        {
//        	Thread t = new Thread();
//        	t.setDaemon(true);
//        	return t;
//        });
        
        final ScheduledFuture<?>[] f =new ScheduledFuture[1];
        f[0] = exes.scheduleAtFixedRate(() -> 
        {
            if(isAlive())
            {
            	// Hack! https://www.bountysource.com/issues/44957864-websocket-closes-after-being-open
                MyWebSocket[] wss; 
                // values() delivers internal collection that is not synchronized
                synchronized(websockets)
                {
                	wss = websockets.values().toArray(new MyWebSocket[0]);
                }
                
                for(MyWebSocket ws : wss)
                {
                    try
                    { 
                    	//System.out.println("sending ping: "+ws.getHandshakeRequest().getUri());
                    	ws.ping("ping".getBytes()); 
                    }
                    catch(Exception e)
                    { 
                    	// todo: remove
                    	//websockets.remove
                    }
                }
            }
            else
            {
            	if(f[0]!=null)
            		f[0].cancel(false);
            }
        }, 4, 4, TimeUnit.SECONDS);
    }
	
	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	public IExternalAccess getPlatform()
	{
		return agent;
	}
	
	/**
	 *  Opens a web socket.
	 */
	protected WebSocket openWebSocket(IHTTPSession session)
	{
		MyWebSocket ws = new MyWebSocket(session);
		websockets.put(session, ws);
		return ws;
	}
	
	/**
	 *  Handle a search service method.
	 *  @param session The session.
	 */
	protected IFuture<String> handleSearchServiceMessage(final IHTTPSession session, final ServiceSearchMessage ssc)
	{
		final Future<String> ret = new Future<String>();
		
		// Check if search type is set
		if(ssc.getType()==null || ssc.getType().getTypeName()==null || ssc.getType().getTypeName().length()==0)
		{
			Exception e = new RuntimeException("Service type must not be null in service search");
			sendException(e, ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
			return ret;
		}
		
//		final ServiceSearchMessage ssc = (ServiceSearchMessage)msg;
//		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, ServiceScope.PLATFORM).get();
		//final Class<?> type = ssc.getType().getType(NanoWebsocketServer.class.getClassLoader()); // todo: support default loader when using null
//		if(type==null)
//		{
//			Exception e = new RuntimeException("Service class not found: "+ssc.getType());
//			sendException(e, ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
//			return ret;
//		}
		
		// default global or network?
		String scope = ssc.getScope()!=null? ssc.getScope(): ServiceScope.GLOBAL.name();
//		System.out.println("Search service with scope: "+scope);
		
		if(!ssc.isMultiple())
		{
			if("session".equals(scope))
			{
//				IService service = (IService)session.getUserProperties().get(type.getName());
				findModelName(ssc.getType().getTypeName()).thenAccept(filename ->
				{
					if(filename==null)
					{
						sendException(new RuntimeException("Could not create session component for service, no suitable ws_serviceimpl_<class> context parameter defined in web.xml "+ssc.getType()), ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
					}
					else
					{
						getOrCreateSessionComponent(filename, session, filename, true).
							addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
						{
							public void customResultAvailable(IExternalAccess access) throws Exception
							{
								IFuture<IService> res = (IFuture<IService>)access.searchService(new ServiceQuery<IService>(ssc.getType()).setScope(ServiceScope.COMPONENT_ONLY));
								res.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
								{
									public void customResultAvailable(IService service)
									{
										sendResult(service, ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
									}
								});
							}
						});
					}
				}).exceptionally(ret);
				//}).exceptionally(e -> {ret.setException(e); return IFuture.DONE;});
			}
			else
			{
				IFuture<IService> res = (IFuture<IService>)getPlatform().searchService(new ServiceQuery<IService>(ssc.getType()).setScope(ServiceScope.getEnum(scope)));
				res.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
				{
					public void customResultAvailable(IService service)
					{
						// Found service and now send back id for generating javascript proxy
//						addService(service);
//						sendResult(getServiceInfo(service), ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
						sendResult(service, ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
					}
				});
			}
		}
		else
		{
			ITerminableIntermediateFuture<IService> res = (ITerminableIntermediateFuture<IService>)getPlatform().searchServices(new ServiceQuery<IService>(ssc.getType()).setScope(ServiceScope.getEnum(scope)));
			res.addResultListener(new IIntermediateResultListener<IService>()
			{
				public void intermediateResultAvailable(IService service)
				{
					System.out.println("Found service: "+service.getServiceId());
//					addService(service);
					// End of call with finished() thus no addResultListener()
					sendResult(service, ssc.getCallid(), session, false);//.addResultListener(new DelegationResultListener<String>(ret));
//							sendResult(getServiceInfo(service), ssc.getCallid(), session, false);//.addResultListener(new DelegationResultListener<String>(ret));
				}
				
				public void resultAvailable(Collection<IService> services)
				{
					for(IService service: services)
					{
						intermediateResultAvailable(service);
					}
					finished();
				}
				
				public void finished()
				{
					sendResult(null, ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
				}
				
				public void exceptionOccurred(Exception ex)
				{
					sendException(ex, ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
				}
			});
		}
		
		return ret;
	}
	
//	/**
//	 *  Invoke a Jadex service on the managed platform.
//	 */
//	public IFuture<Object> invokeServiceMethod(IComponentIdentifier cid, ClassInfo servicetype, 
//		final String methodname, final Object[] args, final ClassInfo[] argtypes, @FutureReturnType final ClassInfo rettype)
//	{
//		Class<?> rtype = rettype!=null? rettype.getType(agent.getClassLoader(), agent.getModel().getAllImports()): null;
//		
//		final Future<Object> ret = (Future<Object>)SFuture.getNoTimeoutFuture(rtype, agent);
//		
//		System.out.println("invokeServiceMethod: "+servicetype+" "+methodname+" "+Arrays.toString(args)+" "+rettype);
//		
//		// Search service with startpoint of given platform 
//		agent.searchService(new ServiceQuery<IService>(servicetype).setSearchStart(cid.getRoot()).setScope(ServiceScope.PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<IService, Object>(ret)
//		{
//			@Override
//			public void customResultAvailable(IService ser) throws Exception
//			{
//				//System.out.println("Invoking service method: "+ser+" "+methodname);
//				IFuture<Object> fut = ser.invokeMethod(methodname, argtypes, args, rettype);
//				FutureFunctionality.connectDelegationFuture(ret, fut);
//			}
//		});
//		
//		return ret;
//	}

	/**
	 *  Handle a service invocation method.
	 *  @param session The session.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceInvocationMessage(final IHTTPSession session, final ServiceInvocationMessage sim)
	{
		Future<String> ret = new Future<>();
		MethodInfo[] mis = serviceinfos.get(sim.getServiceId().getServiceType());
		
		int psize = sim.getParameterValues().length;
		List<MethodInfo> fit = new ArrayList<>();
		for(MethodInfo mi: mis)
		{
			if(mi.getName().equals(sim.getMethodName()) && psize==mi.getParameterTypeInfos().length)
			{
				fit.add(mi);
			}
		}
		
		if(fit.size()==0)
		{
			sendException(new RuntimeException("Method not found: "+sim.getMethodName()), sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
			return ret;
		}
//		if(fit.size()>1)
//		{
//			sendException(new RuntimeException("Too many methods found: "+sim.getMethodName()), sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
//			return ret;
//		}
		
		MethodInfo mi = fit.get(0);
		//Class<?> rtype = mi.getReturnTypeInfo().getType(this.getClass().getClassLoader());
		
		//final Future<Object> mret = (Future<Object>)SFuture.getNoTimeoutFuture(rtype, agent);

		//System.out.println("invokeServiceMethod: "+servicetype+" "+methodname+" "+Arrays.toString(args)+" "+rettype);

		//final Future<String> ret = new Future<String>();
	
		System.out.println("Searching service: "+sim.getServiceId()+" on platform: "+getPlatform().getId());
		
		IFuture<IService> fut = getPlatform().searchService(new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(sim.getServiceId()));
		
		fut.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
		{
			public void customResultAvailable(IService service) throws Exception
			{
				IFuture<?> res = service.invokeMethod(sim.getMethodName(), mi.getParameterTypeInfos(), sim.getParameterValues(), mi.getReturnTypeInfo());
				//FutureFunctionality.connectDelegationFuture(ret, res);

				incalls.put(sim.getCallid(), (IFuture<?>)res);
//				System.out.println("saving: "+sim.getCallid());

				if(res instanceof IIntermediateFuture)
				{
					((IIntermediateFuture<Object>)res).addResultListener(new IIntermediateResultListener<Object>()
					{
						public void intermediateResultAvailable(Object result)
						{
//							System.out.println("ires: "+result+" "+sim.getCallid());
							sendResult(result, sim.getCallid(), session, false);//.addResultListener(new DelegationResultListener<String>(ret));
						}

						public void exceptionOccurred(Exception e)
						{
							sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
							incalls.remove(sim.getCallid());
//							System.out.println("removed ex: "+sim.getCallid());
						}

						public void finished()
						{
							sendResult(null, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
							incalls.remove(sim.getCallid());
//							System.out.println("removed fin: "+sim.getCallid());
						}

						public void resultAvailable(Collection<Object> result)
						{
							sendResult(result, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
							incalls.remove(sim.getCallid());
//							System.out.println("removed ra: "+sim.getCallid());
						}
					});
				}
				else //if(res instanceof IFuture)
				{
					((IFuture<Object>)res).addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							sendResult(result, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
							incalls.remove(sim.getCallid());
//							System.out.println("remove fut call res: "+sim.getCallid());
						}

						public void exceptionOccurred(Exception e)
						{
							sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
							incalls.remove(sim.getCallid());
//							System.out.println("remove fut call ex: "+sim.getCallid());
						}
					});
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Find the component model name for a type/interface name.
	 *  @param typename The service type name.
	 *  @return The model name.
	 */
	protected IFuture<String> findModelName(String typename)
	{	
		Future<String> ret = new Future<>();
		
		getMappings().thenAccept(c -> 
		{
			Collection<String> filenames = mappings.getCollection(typename);
			
			System.out.println("Mappings for: "+typename+" "+filenames);
			
			if(filenames.size()>0)
				ret.setResult(filenames.iterator().next());
			else
				ret.setException(new RuntimeException("No mapping found for: "+typename));
		}).exceptionally(ret);
		
		return ret;
	}
	
	/**
	 *  Generate a map of service interfaces and components. Helps
	 *  to find a component that implements a service interface.
	 */
	protected IFuture<MultiCollection<String, String>> getMappings()
	{
		final Future<MultiCollection<String, String>> ret = new Future<>();
		
		if(mappings==null)
		{
			mappings = new MultiCollection<>(new HashMap<>(), HashSet.class);
		
			getPlatform().scheduleStep(p ->
			{
				ILibraryService ls = p.getLocalService(ILibraryService.class);
				URL[] urls = ls.getAllURLs().get().toArray(new URL[0]);
				urls = SUtil.removeSystemUrls(urls);
				
				Set<ClassFileInfo> cis = new HashSet<>();
				FileFilter ff = new FileFilter(null, false, ".class");
				
				Set<ClassFileInfo> allcis = SReflect.scanForClassFileInfos(urls, ff, new IFilter<ClassFileInfo>()
				{
					public boolean filter(ClassFileInfo ci)
					{
						AnnotationInfo ai = ci.getClassInfo().getAnnotation(Agent.class.getName());
						
						if(ai!=null)
						{
							// todo: check interfaces for @Service annotation
							
							List<String> ifaces = ci.getClassInfo().getInterfaceNames();
							
							for(String iface: ifaces)
							{
								mappings.add(iface, ci.getFilename());
							}
							
							AnnotationInfo pss = ci.getClassInfo().getAnnotation(ProvidedServices.class.getName());
							if(pss!=null)
							{
								Object[] ps = (Object[])pss.getValue("value");
								for(Object o: ps)
								{
									AnnotationInfo p = (AnnotationInfo)o;
									ClassInfo iface = (ClassInfo)p.getValue("type");
									mappings.add(iface.getClassName(), ci.getFilename());
								}
							}
						}
						
						return true;
						//return ai!=null;
					}
				});
				
				//System.out.println("Found classes: "+allcis);
				
				ret.setResult(mappings);
				
				return IFuture.DONE;
			});
		}
		else
		{
			ret.setResult(mappings);
		}
		
		return ret;
	}
	
	/**
	 *  Find the correct method by its name and parameter values.
	 *  The parameter values are half-evaluated, i.e. if they are
	 *  deserialized as far as possible. Still serialized parameters
	 *  are saved as SerialiedObject. Those are deserialized using
	 *  the parameter class as hint.
	 *
	 *  @param decparams Partially decoded parameters.
	 *  @param serclazz The target class.
	 *  @param methodname The method name
	 *  @return The method and further decoded parameters.
	 * /
	protected Tuple2<java.lang.reflect.Method, Object[]> findMethod(Object[] params, Class<?> serclazz, String methodname)
	{
		java.lang.reflect.Method ret = null;

		java.lang.reflect.Method[] ms = SReflect.getMethods(serclazz, methodname);

		Object[] pvals = null;

		if(ms.length==1)
		{
			ret = ms[0];
		}
		else if(ms.length>1)
		{
			// Find the 'best' method

			// First check the number of arguments
			Set<java.lang.reflect.Method> msok = new HashSet<java.lang.reflect.Method>();
			Set<java.lang.reflect.Method> msmaybeok = new HashSet<java.lang.reflect.Method>();

			for(java.lang.reflect.Method tmp1: ms)
			{
				if(tmp1.getParameterTypes().length==params.length)
				{
					msok.add(tmp1);
				}
			}

			if(msok.size()==1)
			{
				ret = msok.iterator().next();
				if(ret.getParameterTypes().length!=params.length)
					ret = null;
			}
			else if(msok.size()>1)
			{
				// Check the argument types

				for(Iterator<java.lang.reflect.Method> it=msok.iterator(); it.hasNext();)
				{
					java.lang.reflect.Method meth = it.next();
					boolean maybeok = true;
					for(int i=0; i<meth.getParameterTypes().length; i++)
					{
						Class<?> ptype = meth.getParameterTypes()[i];
						Object pval = params[i];

						boolean ok = true;
						if(pval!=null && !SUtil.NULL.equals(pval))
						{
							Class<?> wptype = SReflect.getWrappedType(ptype); // method parameter type
							Class<?> wpvtype = SReflect.getWrappedType(pval.getClass()); // value type

							ok = SReflect.isSupertype(wptype, wpvtype);

							if(!ok)
							{
								// Javascript only has float (no integer etc.)
								ok = SReflect.isSupertype(Number.class, wptype) &&
									SReflect.isSupertype(Number.class, wpvtype);

								// Test if we got String value and have a basic type or wrapper on the
								maybeok &= SReflect.isSupertype(Number.class, wptype) &&
									SReflect.isSupertype(String.class, wpvtype);
							}
						}

						if(!ok)
						{
							it.remove();
							break; // skip other parameters and try next method
						}
					}

					if(maybeok)
					{
						msmaybeok.add(meth);
					}
				}

				if(msok.size()==1)
				{
					ret = msok.iterator().next();
				}
				else if(msok.size()>1)
				{
					System.out.println("Found more than one method that could be applicable, choosing first: "+msok);
					ret = msok.iterator().next();
				}
				else
				{
					if(msmaybeok.size()>0)
					{
						// check if parameter conversion works
						// do this as long as a suitable method was found
						for(Iterator<java.lang.reflect.Method> it=msmaybeok.iterator(); it.hasNext();)
						{
							java.lang.reflect.Method meth = it.next();

							try
							{
								pvals = generateParameters(params, meth);
								ret = meth;
								break;
							}
							catch(Exception e)
							{
							}
						}
					}
				}
			}
		}

		if(ret!=null && pvals==null)
		{
			try
			{
				pvals = generateParameters(params, ret);
			}
			catch(Exception e)
			{
				ret = null;
			}
		}
		
		return new Tuple2<java.lang.reflect.Method, Object[]>(ret, pvals);
	}*/
	
	/**
	 *  Handle a provide service method. 
	 *  Create or get the minimal agent that publishes the service using a proxy. 
	 *  @param session The session of the provider.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceProvideMessage(IHTTPSession session, final ServiceProvideMessage spm)
	{
		final Future<String> ret = new Future<String>();
	
		getOrCreateSessionComponent("minimalagent", session, MinimalAgent.class.getName()+".class", true)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
		{
			public void customResultAvailable(IExternalAccess ma) throws Exception
			{
				ma.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(final IInternalAccess ia)
					{
						final Class<?> sertype = spm.getType().getType(ia.getClassLoader());

//						boolean reuse = false; // spm.isReuse();
//						ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(clazz)

						final IServiceIdentifier[] mysid = new IServiceIdentifier[1];

						Object service = Proxy.newProxyInstance(ia.getClassLoader(), new Class[]{sertype}, new InvocationHandler()
						{
							public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable
							{
								// A service call was received by the service proxy of the client service
								// -> Call the client side and wait for result

//								System.out.println("Received service invocation: "+method.getName());

								Future<?> ret = null;
								
								if(!(SReflect.isSupertype(IFuture.class, method.getReturnType())))
									throw new RuntimeException("Synchronous service methods not supported: "+method.getName());
								
								ret = SFuture.getFuture(method.getReturnType());
								
								String callid = SUtil.createUniqueId(method.getName());
								IServiceIdentifier serviceid = mysid[0];
								ServiceInvocationMessage message = new ServiceInvocationMessage(callid, serviceid, method.getName(), args);
									
								// todo: cleanup old messages
								
								outcalls.put(callid, ret);
									
								sendMessage(message, session);
								
								return ret;
							}
						});
						
						ia.getFeature(IProvidedServicesFeature.class).addService(null, sertype, service)
							.addResultListener(new ExceptionDelegationResultListener<Void, String>(ret)
						{
							public void customResultAvailable(Void result) throws Exception
							{
								IService ser = (IService)ia.getFeature(IProvidedServicesFeature.class).getProvidedService(sertype);
								final IServiceIdentifier sid = ser.getServiceId();
								mysid[0] = sid;

								String[] tags = spm.getTags();
								if(tags!=null && tags.length>0)
								{
									Map<String, Object> params = new HashMap<String, Object>();
									params.put(TagProperty.NAME, tags);
									TagProperty tag = new TagProperty(ia, null, null, params);
									ia.addNFProperty(sid, tag).addResultListener(new ExceptionDelegationResultListener<Void, String>(ret)
									{
										public void customResultAvailable(Void result) throws Exception
										{
											sendResult(sid, spm.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
										}

										public void exceptionOccurred(Exception exception)
										{
											exception.printStackTrace();
											super.exceptionOccurred(exception);
										}
									});
								}
								else
								{
									System.out.println("no tags");
									sendResult(sid, spm.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
								}
							}
						});
						return IFuture.DONE;
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				sendException(exception, spm.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
				super.exceptionOccurred(exception);
			}
		});
		
		return ret;
	}

	/**
	 *  Handle a provide service method.
	 *  Create or get the minimal agent that publishes the service using a proxy.
	 *  @param session The session of the provider.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceUnprovideMessage(final IHTTPSession session, final ServiceUnprovideMessage sum)
	{
		final Future<String> ret = new Future<String>();

		getOrCreateSessionComponent("minimalagent", session, MinimalAgent.class.getName()+".class", false)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
		{
			public void customResultAvailable(IExternalAccess ma) throws Exception
			{
				ma.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(final IInternalAccess ia)
					{
						ia.getFeature(IProvidedServicesFeature.class).removeService(sum.getServiceId())
							.addResultListener(new ExceptionDelegationResultListener<Void, String>(ret)
						{
							public void customResultAvailable(Void result) throws Exception
							{
								sendResult(sum.getServiceId(), sum.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
							}

							public void exceptionOccurred(Exception exception)
							{
								sendException(exception, sum.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
								super.exceptionOccurred(exception);
							}
						});
						return IFuture.DONE;
					}
				});
			}

			public void exceptionOccurred(Exception exception)
			{
				sendException(exception, sum.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
				super.exceptionOccurred(exception);
			}
		});

		return ret;
	}

	/**
	 *  Handle a terminate invocation method.
	 *  @param session The session.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleTerminateInvocationMessage(final IHTTPSession session, final ServiceTerminateInvocationMessage stim)
	{
		final Future<String> ret = new Future<String>();
	
//		final ServiceTerminateInvocationMessage stim = (ServiceTerminateInvocationMessage)msg;
		
//		System.out.println("Abort: "+stim);
		
		IFuture<?> fut = incalls.get(stim.getCallid());
		if(fut instanceof ITerminableFuture)
		{
			((ITerminableFuture<?>)fut).terminate();
			System.out.println("Aborted: "+stim);
		}
		else
		{
			System.out.println("Cannot termiate call: "+fut+" "+stim.getCallid());
		}
		
		return ret;
	}
	
	/**
	 *  Handle a result message from a call to the client. 
	 *  @param session The session.
	 *  @param rm The message.
	 */
	protected IFuture<String> handleResultMessage(final IHTTPSession session, final ResultMessage rm)
	{
		Future<Object> fut = (Future)outcalls.get(rm.getCallid());
		
		if(rm.getException()!=null)
		{
			fut.setException(rm.getException());
		}
		else if(fut instanceof IntermediateFuture)
		{
			if(!rm.isFinished())
			{
				((IntermediateFuture)fut).addIntermediateResult(rm.getResult());
			}
			else if(rm.getResult()!=null)
			{
				((IntermediateFuture)fut).setFinished();
			}
			else
			{
				fut.setResult(rm.getResult());
			}
		}
		else if(rm.isFinished())
		{
			fut.setResult(rm.getResult());
		}
		
		if(rm.isFinished())
			outcalls.remove(rm.getCallid());
		
		return new Future<String>((String)null);
	}
	
	/**
	 *  Handle a pull result method.
	 *  @param session The session.
	 *  @param prm The message.
	 */
	protected IFuture<String> handlePullResultMessage(final IHTTPSession session, final PullResultMessage prm)
	{
		final Future<String> ret = new Future<String>();
		
//		final PullResultMessage prm = (PullResultMessage)msg;
		
//		System.out.println("Abort: "+stim);
		
		IFuture<?> fut = incalls.get(prm.getCallid());
		if(fut instanceof IPullIntermediateFuture)
		{
			((IPullIntermediateFuture<?>)fut).pullIntermediateResult();
//			System.out.println("Pulled intermediate result.");
		}
		else
		{
			System.out.println("Cannot pull result: "+fut+" "+prm.getCallid());
		}
		
		return ret;
	}
	
	/**
	 *  Generate call parameters.
	 *  @param vals The current parameters.
	 *  @return The adapted method call parameters.
	 * /
	protected Object[] generateParameters(Object[] vals, java.lang.reflect.Method m) throws Exception
	{
		Object[] ret = new Object[vals.length];
		
		for(int i=0; i<ret.length; i++)
		{
			if(vals[i]==null)
			{
				ret[i] = null;
				continue;
			}
			
//			Class<?> wptype = SReflect.getWrappedType(m.getParameterTypes()[i]);
			Type wptype = m.getGenericParameterTypes()[i];
			
			ret[i] = convertParameter(vals[i], wptype);
		}
		
		return ret;
	}*/
	
	/**
	 *  Convert a parameter to a target type.
	 * /
	protected Object convertParameter(Object value, Type targettype) throws Exception
	{
		Class<?> targetclass = SReflect.getClass(targettype);
		Class<?> targetclasswrapped = SReflect.getWrappedType(targetclass);
		
		if(value instanceof SerializedValue)
		{
			String text = ((SerializedValue)value).getValue();
			try
			{
//				value = JsonTraverser.objectFromString(text, this.getClass().getClassLoader(), null, targetclass, readprocs);
				value = convertToObject(text, targetclass);
			}
			catch(Exception e)
			{
//				value = JsonTraverser.objectFromString(text, this.getClass().getClassLoader(), null, targetclasswrapped, readprocs);
				value = convertToObject(text, targetclasswrapped);
			}
		}

		Object ret = value;

		Class<?> valuewrapped = SReflect.getWrappedType(value.getClass());

		if(!SReflect.isSupertype(targetclass, valuewrapped))
		{
//			System.out.println("type problem: "+targetType+" "+actualValueWrapped+" "+sim.getParameterValues()[i]);
			
			if(value instanceof String)
			{
				if(basicconverters.isSupportedType(targetclass))
				{
					IStringObjectConverter conv = basicconverters.getStringConverter(targetclass);
					Object cval = conv.convertString((String)value, null);
					ret = cval;
				}
				// base 64 case (problem could be normal string?!)
				else if(SReflect.isSupertype(byte[].class, targetclass))
				{
					ret = Base64.decode(((String) value).toCharArray());
				}
			}
			// Javascript only has float (no integer etc.)
			else if(SReflect.isSupertype(Number.class, targetclass) && SReflect.isSupertype(Number.class, valuewrapped))
			{
				if(Integer.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).intValue();
				}
				else if(Long.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).longValue();
				}
				else if(Double.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).doubleValue();
				}
				else if(Float.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).floatValue();
				}
				else if(Short.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).shortValue();
				}
				else if(Byte.class.equals(targetclasswrapped))
				{
					ret = ((Number)value).byteValue();
				}
			}
			else if(valuewrapped.isArray())
			{
				Type itype;
				if(SReflect.isSupertype(List.class, targetclass))
				{
					ret = new ArrayList<Object>();
					itype = SReflect.getInnerGenericType(targettype);
				}
				else if(SReflect.isSupertype(Set.class, targetclass))
				{
					ret = new HashSet<Object>();
					itype = SReflect.getInnerGenericType(targettype);
				}
				else if(targetclass.isArray())
				{
					ret = Array.newInstance(targetclass.getComponentType(), Array.getLength(value));
					itype = targetclass.getComponentType();
				}
				else
				{
					throw new RuntimeException("Parameter conversion not possible: "+value+" "+targettype);
				}
					
				if(Array.getLength(value)>0)
				{
					for(int i=0; i<Array.getLength(value); i++)
					{
						Object v = convertParameter(Array.get(value, i), itype);
						if(ret instanceof Collection)
						{
							((Collection)ret).add(v);
						}
						else
						{
							Array.set(ret, i, v);
						}
					}
				}
			}
			else
			{
				throw new RuntimeException("Parameter conversion not possible: "+value+" "+targettype);
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Send a result message back to the client.
	 */
	protected IFuture<String> sendResult(Object res, String callid, IHTTPSession session, boolean finished)
	{
//		if(finished && res!=null && res.getClass().toString().indexOf("ChatEvent")!=-1)
//			System.out.println("removing call: "+callid);
	
		Future<String> ret = new Future<>();
		
		// todo: sideeffect: move out of here!
		
		if(res instanceof IService)
		{
			IService ser = (IService)res;
			jadex.bridge.ClassInfo iface = ser.getServiceId().getServiceType();
			if(!serviceinfos.containsKey(iface))
			{
				ser.getMethodInfos().thenAccept(mis -> 
				{
					serviceinfos.put(iface, mis);
					ServiceInfo si = new ServiceInfo(((IService)res).getServiceId(), getMethodNames(mis));
					sendMessage(new ResultMessage(si, callid, finished), session).delegate(ret);
				}).exceptionally(ret);
			}
			else
			{
				MethodInfo[] mis = serviceinfos.get(iface);
				ServiceInfo si = new ServiceInfo(((IService)res).getServiceId(), getMethodNames(mis));
				sendMessage(new ResultMessage(si, callid, finished), session).delegate(ret);
			}
		}
		else
		{
			sendMessage(new ResultMessage(res, callid, finished), session).delegate(ret);
		}
		
		return ret;
	}
	
	/**
	 *  Get the method names.
	 *  @param mis The method infos.
	 *  @return The method names.
	 */
	protected Set<String> getMethodNames(MethodInfo[] mis)
	{
		Set<String> ret = new HashSet<>();
		for(MethodInfo mi: mis)
			ret.add(mi.getName());
		return ret;
	}
	
	/**
	 *  Send an exception message back to the client.
	 */
	protected IFuture<String> sendException(Exception ex, String callid, IHTTPSession session)
	{
//		System.out.println("removing call ex: "+callid);
		if(debug) 
			ex.printStackTrace();
		return sendMessage(new ResultMessage(ex, callid), session);
	}
	
	/**
	 *  Send an message back to the client.
	 */
	protected IFuture<String> sendMessage(BaseMessage message, IHTTPSession session)
	{
		Future<String> ret = new Future<String>();

		try
		{
			// todo: problem classloader

			// TODO: unwrap types here instead frontend-side?

			// ensure single threaded access to socket (how to do without lock)?
			//String data = JsonTraverser.objectToString(message, this.getClass().getClassLoader(), true, null, writeprocs);
			String data = convertToString(message);
			
			synchronized(this)
			{
				getWebSocket(session).send(data);
				//session.getBasicRemote().sendText(data);
			}
			ret.setResult(data);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Convert an object to the json string representation.
	 *  @param val The value.
	 *  @return The string representation.
	 */
	protected String convertToString(Object val)
	{
		ISerializationServices ser = (ISerializationServices)Starter.getPlatformValue(getPlatform().getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_JSON);
		String data = conv.convertObject(val, null, this.getClass().getClassLoader(), null);
		return data;
	}
	
	/**
	 *  Convert json to object representation.
	 *  @param val The json value.
	 *  @return The object.
	 */
	protected Object convertToObject(String val, Class<?> type)
	{
		ISerializationServices ser = (ISerializationServices)Starter.getPlatformValue(getPlatform().getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_JSON);
		Object data = conv.convertString(val, type, this.getClass().getClassLoader(), null);
		return data;
	}
	
	/**
	 *  Get or create a session component under the given key.
	 *  
	 *  // todo: create session component on other than gateway platform?!
	 */
	protected IFuture<IExternalAccess> getOrCreateSessionComponent(final String key, final IHTTPSession session, final String filename, final boolean create)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		Object ma = getWebSocket(session).getProperties().get(key);
		
		if(ma instanceof IFuture)
		{
			((IFuture<IExternalAccess>)ma).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
		}
		else if(ma==null)
		{
			if(create)
			{
				getWebSocket(session).getProperties().put(key, ret);

				getPlatform().createComponent(new CreationInfo().setFilename(filename)).addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess exta)
					{
						exta.waitForTermination().addResultListener(new IResultListener<Map<String,Object>>()
						{
							public void resultAvailable(java.util.Map<String,Object> result)
							{
								try
								{
									getWebSocket(session).getProperties().remove(key);
								}
								catch(IllegalStateException e)
								{
									// nop when session is already closed
								}
								catch(Exception e)
								{
									System.out.println("Could not remove component from session: "+key);
									e.printStackTrace();
								}
							}
							public void exceptionOccurred(Exception exception)
							{
								if(!ret.setExceptionIfUndone(exception))
								{
									System.out.println("Exception in session component");
									exception.printStackTrace();
								}
							}
						});
						ret.setResult(exta);
					}

					public void exceptionOccurred(Exception exception)
					{
						if(!ret.setExceptionIfUndone(exception))
						{
							System.out.println("Exception in session component");
							exception.printStackTrace();
						}
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Handle a partial mesage.
	 */
	protected IFuture<String> handlePartialMessage(final IHTTPSession session, final PartialMessage msg)
	{
		final Future<String> ret = new Future<String>();
		
		Map<Integer, String> mypartials = partials.get(msg.getCallid());
		
		if(mypartials==null)
		{
			mypartials = new HashMap<Integer, String>();
			partials.put(msg.getCallid(), mypartials);
		}
		
		mypartials.put(Integer.valueOf(msg.getNumber()), msg.getData());

//		System.out.println("Received: "+msg.getNumber()+" "+mypartials.size()+"/"+msg.getCount());
		
		// When all parts have arrived
		if(mypartials.size()==msg.getCount())
		{
			StringBuffer buf = new StringBuffer();
			for(int i=0; i<msg.getCount(); i++)
			{
				buf.append(mypartials.remove(i));
			}
			partials.remove(msg.getCallid());
			
			try
			{
//				System.out.println("Recieved sliced message: "+msg.getCallid());
				//onMessage(buf.toString(), session);
				getWebSocket(session).onMessage(buf.toString());
				ret.setResult("Recieved sliced message: "+msg.getCallid());
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		
		return ret;
	}

	/**
	 *  Struct for serialized value.
	 */
	public static class SerializedValue
	{
		/** The serialized value. */
		protected String value;

		/**
		 *  Create a new serialized value.
		 */
		public SerializedValue()
		{
		}

		/**
		 *  Create a new serialized value.
		 */
		public SerializedValue(String value)
		{
			this.value = value;
		}

		/**
		 *  Get the value.
		 *  @return The value
		 */
		public String getValue()
		{
			return value;
		}

		/**
		 *  Set the value.
		 *  @param value The value to set
		 */
		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	/**
	 *  Get websocket per session.
	 *  @param session The session.
	 *  @return The socket.
	 */
	protected MyWebSocket getWebSocket(IHTTPSession session)
	{
		MyWebSocket ws = websockets.get(session);
		if(ws!=null)
			return ws;
		else
			throw new RuntimeException("No websocket found for: "+session);		
	}
	
	/**
	 * 
	 */
	class MyWebSocket extends WebSocket
	{
		protected IHTTPSession session;
		
		protected Map<String, Object> properties;
		
		public MyWebSocket(IHTTPSession session)
		{
			super(session);
			this.session = session;
			this.properties = new HashMap<>();
		}
		
		public Map<String, Object> getProperties()
		{
			return properties;
		}

		@Override
		protected void onOpen()
		{
			System.out.println("WebSocket opened: "+", jadexsocket@"+this.hashCode());
		}
		
		@Override
		protected void onMessage(WebSocketFrame message)
		{
			String txt = message.getTextPayload();
			onMessage(txt);
		}
		
		public void onMessage(String txt)
		{
			System.out.println("Message received: " + txt);
//			RemoteEndpoint.Async rea = session.getAsyncRemote();
			
			// The result here ist the (last) meassage sent back
			final Future<String> ret = new Future<String>();
			
			try
			{
				// todo: problem classloader
				// JSonServiceProcessor: service.getServiceIdentifier().getServiceType().getType(targetcl);
				// problem getType(null) does not use default classloader but returns null :-(?!
				//final Object msg = JsonTraverser.objectFromString(txt, this.getClass().getClassLoader(), null, Object.class, readprocs);
				final Object msg = convertToObject(txt, null);
				
				if(msg instanceof PartialMessage)
				{
					handlePartialMessage(session, (PartialMessage)msg);
				}
				else if(msg instanceof ServiceSearchMessage)
				{
					handleSearchServiceMessage(session, (ServiceSearchMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else if(msg instanceof ServiceInvocationMessage)
				{
					handleServiceInvocationMessage(session, (ServiceInvocationMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else if(msg instanceof ServiceTerminateInvocationMessage)
				{
					handleTerminateInvocationMessage(session, (ServiceTerminateInvocationMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else if(msg instanceof PullResultMessage)
				{
					handlePullResultMessage(session, (PullResultMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else if(msg instanceof ServiceProvideMessage)
				{
					handleServiceProvideMessage(session, (ServiceProvideMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else if(msg instanceof ServiceUnprovideMessage)
				{
					handleServiceUnprovideMessage(session, (ServiceUnprovideMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else if(msg instanceof ResultMessage)
				{
					handleResultMessage(session, (ResultMessage)msg).addResultListener(new DelegationResultListener<String>(ret));
				}
				else
				{
					System.out.println("Message type not understood: "+msg);
					ret.setResult("not understood");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
//			String res = fut.get();
//			session.getBasicRemote().sendText(res);
			
			ret.addResultListener(new DefaultResultListener<String>()
			{
				public void resultAvailable(String arg0)
				{
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			});
		}
		
		@Override
		protected void onClose(CloseCode code, String reason, boolean initiatedByRemote)
		{
			System.out.println("Closing a WebSocket due to " + reason + ", jadexsocket@"+this.hashCode());
			
			// Kill session components
			for(Object val: getProperties().values())
			{
				IComponentIdentifier cid = null;
				
				if(val instanceof IService)
				{
					cid = ((IService)val).getServiceId().getProviderId();
				}				
				else if(val instanceof IExternalAccess)
				{
					cid = ((IExternalAccess)val).getId();
				}
				else if(val instanceof IComponentIdentifier)
				{
					cid = (IComponentIdentifier)val;
				}
				else
				{
					System.out.println("found session value: "+val);
				}

				if(cid!=null)
				{
//					IComponentManagementService cms = platform.searchService(new ServiceQuery<>(IComponentManagementService.class)).get();
					getPlatform().getExternalAccess(cid).killComponent();
					System.out.println("Killing session component: "+((IService)val).getServiceId().getProviderId());
				}
			}
			
			websockets.remove(session);
		}
		
		@Override
		protected void onException(IOException exception)
		{
			System.out.println("onExeption: "+exception);
		}
		
		@Override
		protected void onPong(WebSocketFrame pong)
		{
			//System.out.println("onPong: "+pong.getTextPayload());
		}
	};
}
