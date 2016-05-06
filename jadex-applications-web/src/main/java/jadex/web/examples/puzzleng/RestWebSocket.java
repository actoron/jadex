package jadex.web.examples.puzzleng;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.actoron.webservice.messages.PullResultMessage;
import com.actoron.webservice.messages.ResultMessage;
import com.actoron.webservice.messages.ServiceInvocationMessage;
import com.actoron.webservice.messages.ServiceSearchMessage;
import com.actoron.webservice.messages.ServiceTerminateInvocationMessage;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
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
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  WebSocket server implementation.
 *  Opens web socket at an address and acts as bridge to Jadex.
 *  Allows for JavaScript usage of Jadex that comes close to Java programming.
 */
@ServerEndpoint(value="/wswebapi")//, configurator=ServletAwareConfig.class)
public class RestWebSocket
{
	/** The service proxies (cache). */
	// todo: release services after some timeout
//	protected Map<String, IService> services;
	
	/** The ongoing future calls. */
	protected Map<String, IFuture<?>> calls = new HashMap<String, IFuture<?>>();
	
//	/** The endpoint configuration. */
//	protected EndpointConfig config;
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig config)
	{
		System.out.println("WebSocket opened: " + session.getId());
//		this.config = config;
	}

	@OnMessage
	public void onMessage(String txt, final Session session) throws IOException
	{
		System.out.println("Message received: " + txt);
//		RemoteEndpoint.Async rea = session.getAsyncRemote();
		
		final Future<String> ret = new Future<String>();
		
		try
		{
			final Object msg = JsonTraverser.objectFromString(txt, null, Object.class);
			if(msg instanceof ServiceSearchMessage)
			{
				getPlatform(session).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
				{
					public void customResultAvailable(IExternalAccess platform)
					{
						final ServiceSearchMessage ssc = (ServiceSearchMessage)msg;
//						IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
						Class<?> type = ssc.getType().getType(RestWebSocket.class.getClassLoader()); // todo: support default loader when using null
						
						if(!ssc.isMultiple())
						{
							IFuture<IService> res = (IFuture<IService>)SServiceProvider.getService(platform, type, RequiredServiceInfo.SCOPE_GLOBAL);
							res.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
							{
								public void customResultAvailable(IService service)
								{
									// Found service and now send back id for generating javascript proxy
//									addService(service);
									sendResult(getServiceInfo(service), ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
								}
							});
						}
						else
						{
							ITerminableIntermediateFuture<IService> res = (ITerminableIntermediateFuture<IService>)SServiceProvider.getServices(platform, type, RequiredServiceInfo.SCOPE_GLOBAL);
							res.addResultListener(new IIntermediateResultListener<IService>()
							{
								public void intermediateResultAvailable(IService service)
								{
									System.out.println("Found: "+service.getServiceIdentifier());
//									addService(service);
									sendResult(getServiceInfo(service), ssc.getCallid(), session, false);//.addResultListener(new DelegationResultListener<String>(ret));
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
					}
				});
			}
			else if(msg instanceof ServiceTerminateInvocationMessage)
			{
				getPlatform(session).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
				{
					public void customResultAvailable(IExternalAccess platform)
					{
						final ServiceTerminateInvocationMessage stim = (ServiceTerminateInvocationMessage)msg;
						
//						System.out.println("Abort: "+stim);
						
						IFuture<?> fut = calls.get(stim.getCallid());
						if(fut instanceof ITerminableFuture)
						{
							((ITerminableFuture<?>)fut).terminate();
							System.out.println("Aborted: "+stim);
						}
						else
						{
							System.out.println("Cannot termiate call: "+fut+" "+stim.getCallid());
						}
					}
				});
			}
			else if(msg instanceof PullResultMessage)
			{
				getPlatform(session).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
				{
					public void customResultAvailable(IExternalAccess platform)
					{
						final PullResultMessage prm = (PullResultMessage)msg;
						
//						System.out.println("Abort: "+stim);
						
						IFuture<?> fut = calls.get(prm.getCallid());
						if(fut instanceof IPullIntermediateFuture)
						{
							((IPullIntermediateFuture<?>)fut).pullIntermediateResult();
//							System.out.println("Pulled intermediate result.");
						}
						else
						{
							System.out.println("Cannot pull result: "+fut+" "+prm.getCallid());
						}
					}
				});
			}
			else if(msg instanceof ServiceInvocationMessage)
			{
				getPlatform(session).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
				{
					public void customResultAvailable(IExternalAccess platform)
					{
						final ServiceInvocationMessage sim = (ServiceInvocationMessage)msg;
						
//						System.out.println("Call: "+sim.getServiceId()+" "+sim.getMethodName()+" "+sim.getParameterValues());
												
						getService(sim.getServiceId(), platform)
							.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
						{
							public void customResultAvailable(IService service) throws Exception
							{
								Class<?> serclazz = service.getServiceIdentifier().getServiceType().getType(null);
								
								Method[] ms = SReflect.getMethods(serclazz, sim.getMethodName());
								
								Method m = null;
								
								if(ms.length==1)
								{
									m = ms[0];
								}
								else if(ms.length>1)
								{
									// Find the 'best' method
									
									// First check the number of arguments
									Set<Method> msok = new HashSet<Method>();
									
									for(Method tmp1: ms)
									{
										if(tmp1.getParameterCount()==sim.getParameterValues().length)
										{
											msok.add(tmp1);
										}
									}
									
									if(msok.size()==1)
									{
										m = msok.iterator().next();
									}
									else if(msok.size()>1)
									{
										// Check the argument types
										
		//								for(Method tmp2: msok)
										for(Iterator<Method> it=msok.iterator(); it.hasNext();)
										{
											Method tmp2 = it.next();
											for(int i=0; i<tmp2.getParameterTypes().length; i++)
											{
												Class<?> ptype = tmp2.getParameterTypes()[i];
												Object pval = sim.getParameterValues()[i];
												
												boolean ok = true;
												if(pval!=null)
												{
													Class<?> wptype = SReflect.getWrappedType(ptype);
													Class<?> wpvtype = SReflect.getWrappedType(pval.getClass());
													
													ok = SReflect.isSupertype(wptype, wpvtype);
													
													if(!ok)
													{
														// Javascript only has float (no integer etc.)
														ok = SReflect.isSupertype(Number.class, wptype) &&
															SReflect.isSupertype(Number.class, wpvtype);
													}
												}
												
												if(!ok)
												{
													it.remove();
												}
											}
										}
										
										if(msok.size()==1)
										{
											m = msok.iterator().next();
										}
										else
										{
											System.out.println("Found more than one method that could be applicable, choosing first: "+msok);
											m = msok.iterator().next();
										}
									}
								}
							
								if(m==null)
								{
									sendException(new RuntimeException("Method not found: "+sim.getMethodName()), sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
								}
								else
								{
									try
									{
										Object[] pvals = new Object[sim.getParameterValues().length];
										for(int i=0; i<sim.getParameterValues().length; i++)
										{
											Class<?> wptype = SReflect.getWrappedType(m.getParameterTypes()[i]);
											Class<?> wpvtype = SReflect.getWrappedType(sim.getParameterValues()[i].getClass());
											
											if(!SReflect.isSupertype(wptype, wpvtype))
											{
		//										System.out.println("type problem: "+wptype+" "+wpvtype+" "+sim.getParameterValues()[i]);
												
												if(sim.getParameterValues()[i] instanceof String)
												{
													if(BasicTypeConverter.isExtendedBuiltInType(wptype))
													{
														IStringObjectConverter conv = BasicTypeConverter.getExtendedStringConverter(wptype);
														Object cval = conv.convertString((String)sim.getParameterValues()[i], null);
														pvals[i] = cval;
													}
												}
												// Javascript only has float (no integer etc.)
												else if(SReflect.isSupertype(Number.class, wptype) && SReflect.isSupertype(Number.class, wpvtype))
												{
													if(Integer.class.equals(wptype))
													{
														pvals[i] = ((Number)sim.getParameterValues()[i]).intValue();
													}
													else if(Long.class.equals(wptype))
													{
														pvals[i] = ((Number)sim.getParameterValues()[i]).longValue();
													}
													else if(Double.class.equals(wptype))
													{
														pvals[i] = ((Number)sim.getParameterValues()[i]).doubleValue();
													}
													else if(Float.class.equals(wptype))
													{
														pvals[i] = ((Number)sim.getParameterValues()[i]).floatValue();
													}
													else if(Short.class.equals(wptype))
													{
														pvals[i] = ((Number)sim.getParameterValues()[i]).shortValue();
													}
													else if(Byte.class.equals(wptype))
													{
														pvals[i] = ((Number)sim.getParameterValues()[i]).byteValue();
													}
												}
											}
											else
											{
												pvals[i] = sim.getParameterValues()[i];
											}
										}
										
										Object res = m.invoke(service, pvals);
									
										if(res instanceof IFuture)
										{
											calls.put(sim.getCallid(), (IFuture<?>)res);
		//									System.out.println("saving: "+sim.getCallid());
											
											if(res instanceof IIntermediateFuture)
											{
												((IIntermediateFuture<Object>)res).addResultListener(new IIntermediateResultListener<Object>()
												{
													public void intermediateResultAvailable(Object result)
													{
														sendResult(result, sim.getCallid(), session, false);//.addResultListener(new DelegationResultListener<String>(ret));
													}
													
													public void exceptionOccurred(Exception e)
													{
														sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
														calls.remove(sim.getCallid());
		//												System.out.println("removed: "+sim.getCallid());
													}
													
													public void finished()
													{
														sendResult(null, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
														calls.remove(sim.getCallid());
		//												System.out.println("removed: "+sim.getCallid());
													}
													
													public void resultAvailable(Collection<Object> result)
													{
														sendResult(result, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
														calls.remove(sim.getCallid());
		//												System.out.println("removed: "+sim.getCallid());
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
														calls.remove(sim.getCallid());
		//												System.out.println("removedb: "+sim.getCallid());
													}
													
													public void exceptionOccurred(Exception e)
													{
														sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
														calls.remove(sim.getCallid());
		//												System.out.println("removedb: "+sim.getCallid());
													}
												});
											}
										}
										else
										{
											sendResult(res, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
										}
									}
									catch(Exception e)
									{
										sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
									}
								}
							}
						});
					}
				});
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
		});
	}
	
	@OnClose
	public void onClose(CloseReason reason, Session session)
	{
		System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
	}
	
	/**
	 *  Get info for a service.
	 */
	protected Object[] getServiceInfo(IService service)
	{
		Class<?> serclazz = service.getServiceIdentifier().getServiceType().getType(null);
		Set<String> ms = new HashSet<String>();
		
		Class<?> clazz = serclazz;
		while(clazz!=null)
		{
			Method[] methods = serclazz.getDeclaredMethods();
		
			for(Method m: methods)
			{
				String name = m.getName();
				ms.add(name);
			}
				
			clazz = clazz.getSuperclass();
		}
		
		Object[] result = new Object[]{service.getServiceIdentifier().toString(), ms};

		return result;
	}
	
	/**
	 *  Send a result message back to the client.
	 */
	protected IFuture<String> sendResult(Object res, String callid, Session session, boolean finished)
	{
		return sendMessage(new ResultMessage(res, callid, finished), session);
	}
	
	/**
	 *  Send an exception message back to the client.
	 */
	protected IFuture<String> sendException(Exception ex, String callid, Session session)
	{
		return sendMessage(new ResultMessage(ex, callid), session);
	}
	
	/**
	 *  Send an message back to the client.
	 */
	protected IFuture<String> sendMessage(ResultMessage rm, Session session)
	{
		Future<String> ret = new Future<String>();
		try
		{
			byte[] data = JsonTraverser.objectToByteArray(rm, null, null, false);
			String d = new String(data);
			ret.setResult(d);
			session.getBasicRemote().sendText(d);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Create a service identifier back from its string representation.
	 */
	protected IFuture<IServiceIdentifier> getServiceIdentifier(String sidname, IExternalAccess platform)
	{
		final Future<IServiceIdentifier> ret = new Future<IServiceIdentifier>();
		
		int idx = sidname.indexOf("@");
		String sername = sidname.substring(0, idx);
		String compname = sidname.substring(idx+1);
		
		ComponentIdentifier cid = new ComponentIdentifier(compname);
		
		// Hack, uses only partially initialized service identifier
		ServiceIdentifier sid = new ServiceIdentifier(cid, Object.class, sername, null, null); 
		
//		SServiceProvider.getService(platform, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, IServiceIdentifier>(ret)
//		{
//			public void customResultAvailable(ITransportAddressService tas)
//			{
//				tas.getPlatformAddresses(new ComponentIdentifier())
//					.addResultListener(new ExceptionDelegationResultListener<>(ret)
//				{
//						
//				});
//			}
//		});
		
		ret.setResult(sid);
		
		return ret;
	}
	
//	/**
//	 *  Add a service to the service cache.
//	 */
//	protected void addService(IService service)
//	{
//		if(services==null)
//			services = new HashMap<>();
//		services.put(service.getServiceIdentifier().toString(), service);
//	}
	
	/**
	 *  Get a service via its sid.
	 */
	protected IFuture<IService> getService(String sid, final IExternalAccess platform)
	{
		final Future<IService> ret = new Future<IService>();
		
//		if(services!=null)
//		{
//			ret.setResult(services.get(sid));
//		}
		
		getServiceIdentifier(sid, platform)
			.addResultListener(new ExceptionDelegationResultListener<IServiceIdentifier, IService>(ret)
		{
			public void customResultAvailable(IServiceIdentifier sid)
			{
				IFuture<IService> fut = SServiceProvider.getService(platform, sid);
				fut.addResultListener(new DelegationResultListener<IService>(ret));
			}
		});
			
		return ret;
	}
	
	/**
	 *  Get or create the platform.
	 *  @return The Jadex platform.
	 */
	protected IFuture<IExternalAccess> getPlatform(Session session)
	{		
//		HttpSession sess = (HttpSession)config.getUserProperties().get("httpSession");
//	    ServletContext appctx = sess.getServletContext();
//	    Object res = appctx.getAttribute("platform");
		Object res = session.getUserProperties().get("platform");
	    if(res instanceof IFuture)
	    {
			Future<IExternalAccess> ret = new Future<IExternalAccess>();

	    	IFuture<IExternalAccess> fut = (IFuture<IExternalAccess>)res;
	    	if(fut.isDone())
	    	{
	    		if(fut.getException()==null)
	    		{
	    			ret.setResult(fut.get());
	    		}
	    		else
	    		{
	    			ret.setException(fut.getException());
	    		}
	    	}
	    	else
	    	{
	    		fut.addResultListener(new DelegationResultListener<IExternalAccess>(ret));
	    	}
	    	
	    	return ret;
	    }
	    else
	    {
	    	PlatformConfiguration pc = PlatformConfiguration.getDefault();
		    pc.getRootConfig().setGui(false);
		    pc.getRootConfig().setLogging(true);
		    pc.getRootConfig().setAwareness(false);
//		    pc.getRootConfig().setRsPublish(true);	
		    IFuture<IExternalAccess> ret = Starter.createPlatform(pc);
//		    appctx.setAttribute("platform", fut);
		    
		    // Hack!!!
		    IExternalAccess platform = ret.get();
			IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
			IComponentIdentifier cid = cms.createComponent(PuzzleAgent.class.getName()+".class", null).getFirstResult();
			System.out.println("Created: "+cid);
		    
//		    ret.addResultListener(new IResultListener<IExternalAccess>()
//			{
//		    	public void resultAvailable(IExternalAccess platform)
//		    	{
//					IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
//					cms.createComponent(HelloWorldAgent.class.getName()+".class", null)
//						.addTuple2ResultListener(new IFunctionalResultListener<IComponentIdentifier>()
//					{
//						public void resultAvailable(IComponentIdentifier cid)
//						{
//							System.out.println("Created: "+cid);
//						}
//					}, null);
//		    	}
//		    	public void exceptionOccurred(Exception e)
//		    	{
//		    		e.printStackTrace();
//		    	}
//			});
		    
		    session.getUserProperties().put("platform", ret);
		    return ret;
	    }
	}
	
//	/**
//	 * 
//	 */
//	public static class ServletAwareConfig extends ServerEndpointConfig.Configurator 
//	{
//	    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) 
//	    {
//	        HttpSession httpSession = (HttpSession)request.getHttpSession();
//	        config.getUserProperties().put("httpSession", httpSession);
//	    }
//	}
}


