package org.activecomponents.webservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activecomponents.webservice.messages.BaseMessage;
import org.activecomponents.webservice.messages.PartialMessage;
import org.activecomponents.webservice.messages.PullResultMessage;
import org.activecomponents.webservice.messages.ResultMessage;
import org.activecomponents.webservice.messages.ServiceInvocationMessage;
import org.activecomponents.webservice.messages.ServiceProvideMessage;
import org.activecomponents.webservice.messages.ServiceSearchMessage;
import org.activecomponents.webservice.messages.ServiceTerminateInvocationMessage;
import org.activecomponents.webservice.messages.ServiceUnprovideMessage;

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
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.FileFilter;
import jadex.commons.IFilter;
import jadex.commons.MethodInfo;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SClassReader.ClassFileInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringConverter;
import jadex.micro.MinimalAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedServices;

/**
 *  The abstract websocket server handles websocket requests from clients like browsers.
 *  
 *  Is the base class for specific impls like nano or jetty.
 */
public abstract class AbstractWebSocketServer
{
	/** The platform. */
	protected IExternalAccess agent;
	
	/** The ongoing future calls from client. */
	protected Map<String, IFuture<?>> incalls = new HashMap<String, IFuture<?>>();
	
	/** The ongoing future calls to the client. */
	protected Map<String, Future<?>> outcalls = new HashMap<String, Future<?>>();
	
	/** The partial messages. 
	    todo: cleanup per timeout */
	protected Map<String, Map<Integer, String>> partials;

	/** debug flag (print stacktraces of unsuccessful service calls etc) **/
	//protected boolean debug;
	
	/** The basic type converters. */
	protected BasicTypeConverter basicconverters;
	
	/** The interface -> impl file mappings. */
	protected MultiCollection<String, String> mappings;
	
	/** The methodinfos per service interface. */
	protected Map<jadex.bridge.ClassInfo, MethodInfo[]> serviceinfos;
	
	/** 
	 *  Creates the server.
	 */
	public AbstractWebSocketServer(IExternalAccess agent)
	{
		this.agent = agent;
		this.basicconverters = new BasicTypeConverter();
		
		this.partials = new HashMap<String, Map<Integer,String>>();
		//this.websockets = Collections.synchronizedMap(new HashMap<>());
		
		this.serviceinfos = new HashMap<>();
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
	 *  Handle a search service method.
	 *  @param session The session.
	 */
	protected IFuture<String> handleSearchServiceMessage(final Object session, final ServiceSearchMessage ssc)
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
				findModelName(ssc.getType().getTypeName()).then(filename ->
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
				}).catchErr(ret);
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
			res.addResultListener(new IntermediateEmptyResultListener<IService>()
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
	
	/**
	 *  Handle a service invocation method.
	 *  @param session The session.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceInvocationMessage(final Object session, final ServiceInvocationMessage sim)
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
		
		//System.out.println("invokeServiceMethod: "+servicetype+" "+methodname+" "+Arrays.toString(args)+" "+rettype);
		//System.out.println("Searching service: "+sim.getServiceId()+" on platform: "+getPlatform().getId());
		
		IFuture<IService> fut = getPlatform().searchService(new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(sim.getServiceId()));
		
		fut.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
		{
			public void customResultAvailable(IService service) throws Exception
			{
				// Do not pass argtypes when multiple methods match
				IFuture<?> res = service.invokeMethod(sim.getMethodName(), fit.size()==1? mi.getParameterTypeInfos(): null, sim.getParameterValues(), mi.getReturnTypeInfo());

				incalls.put(sim.getCallid(), (IFuture<?>)res);
//				System.out.println("saving: "+sim.getCallid());

				if(res instanceof IIntermediateFuture)
				{
					((IIntermediateFuture<Object>)res).addResultListener(new IntermediateEmptyResultListener<Object>()
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
		
		getMappings().then(c -> 
		{
			Collection<String> filenames = mappings.getCollection(typename);
			
			System.out.println("Mappings for: "+typename+" "+filenames);
			
			if(filenames.size()>0)
				ret.setResult(filenames.iterator().next());
			else
				ret.setException(new RuntimeException("No mapping found for: "+typename));
		}).catchErr(ret);
		
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
	 *  Handle a provide service method. 
	 *  Create or get the minimal agent that publishes the service using a proxy. 
	 *  @param session The session of the provider.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceProvideMessage(Object session, final ServiceProvideMessage spm)
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
	protected IFuture<String> handleServiceUnprovideMessage(final Object session, final ServiceUnprovideMessage sum)
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
	protected IFuture<String> handleTerminateInvocationMessage(final Object session, final ServiceTerminateInvocationMessage stim)
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
	protected IFuture<String> handleResultMessage(final Object session, final ResultMessage rm)
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
	protected IFuture<String> handlePullResultMessage(final Object session, final PullResultMessage prm)
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
	 *  Send a result message back to the client.
	 */
	protected IFuture<String> sendResult(Object res, String callid, Object session, boolean finished)
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
				ser.getMethodInfos().then(mis -> 
				{
					serviceinfos.put(iface, mis);
					ServiceInfo si = new ServiceInfo(((IService)res).getServiceId(), getMethodNames(mis));
					sendMessage(new ResultMessage(si, callid, finished), session).delegate(ret);
				}).catchErr(ret);
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
	protected IFuture<String> sendException(Exception ex, String callid, Object session)
	{
//		System.out.println("removing call ex: "+callid);
		//if(debug) 
			ex.printStackTrace();
		return sendMessage(new ResultMessage(ex, callid), session);
	}
	
	/**
	 *  Send an message back to the client.
	 */
	protected IFuture<String> sendMessage(BaseMessage message, Object session)
	{
		Future<String> ret = new Future<String>();

		try
		{
			// todo: problem classloader

			// TODO: unwrap types here instead frontend-side?

			// ensure single threaded access to socket (how to do without lock)?
			//String data = JsonTraverser.objectToString(message, this.getClass().getClassLoader(), true, null, writeprocs);
			String data = convertToString(message);

			sendWebSocketData(session, data);
			
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
	protected IFuture<IExternalAccess> getOrCreateSessionComponent(final String key, final Object session, final String filename, final boolean create)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		Object ma = getSessionProperties(session).get(key);
		
		if(ma instanceof IFuture)
		{
			((IFuture<IExternalAccess>)ma).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
		}
		else if(ma==null)
		{
			if(create)
			{
				getSessionProperties(session).put(key, ret);

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
									getSessionProperties(session).remove(key);
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
	protected IFuture<String> handlePartialMessage(final Object session, final PartialMessage msg)
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
				onMessage(session, buf.toString());
//				getWebSocket(session).onMessage(buf.toString());
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
	 *  Cleanup the (created) session platform components.
	 *  @param session The session.
	 */
	public void cleanSessionComponents(Object session)
	{
		// Kill session components
		// todo: support remote creation/cleanup
		for(Object val: getSessionProperties(session).values())
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
//				IComponentManagementService cms = platform.searchService(new ServiceQuery<>(IComponentManagementService.class)).get();
				getPlatform().getExternalAccess(cid).killComponent();
				System.out.println("Killing session component: "+((IService)val).getServiceId().getProviderId());
			}
		}
	}
	
	/**
	 * 
	 * @param session
	 * @param txt
	 */
	public void onMessage(Object session, String txt)
	{
		System.out.println("Message received: " + txt);
//		RemoteEndpoint.Async rea = session.getAsyncRemote();
		
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
		
//		String res = fut.get();
//		session.getBasicRemote().sendText(res);
		
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
	
	/**
	 *  Called on close of a session
	 *  @param session The session.
	 */
	public void onClose(Object session)
	{
		cleanSessionComponents(session);
	}
	
	/**
	 *  Abstract send message method.
	 *  Must be implemented by concrete web socket impl.
	 *  @param session The web socket session.
	 *  @param data The data to send.
	 */
	public abstract void sendWebSocketData(Object session, String data);
	
	/**
	 *  Get the properties belonging to a web socket session.
	 *  @param session The session.
	 *  @return The properties.
	 */
	public abstract Map<String, Object> getSessionProperties(Object ws);
	
	
}
