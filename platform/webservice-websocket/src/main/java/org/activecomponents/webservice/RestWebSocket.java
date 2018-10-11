package org.activecomponents.webservice;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.activecomponents.webservice.messages.BaseMessage;
import org.activecomponents.webservice.messages.PartialMessage;
import org.activecomponents.webservice.messages.PullResultMessage;
import org.activecomponents.webservice.messages.ResultMessage;
import org.activecomponents.webservice.messages.ServiceInvocationMessage;
import org.activecomponents.webservice.messages.ServiceProvideMessage;
import org.activecomponents.webservice.messages.ServiceSearchMessage;
import org.activecomponents.webservice.messages.ServiceTerminateInvocationMessage;
import org.activecomponents.webservice.messages.ServiceUnprovideMessage;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
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
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.micro.MinimalAgent;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  WebSocket server implementation.
 *  Opens web socket at an address and acts as bridge to Jadex.
 *  Allows for JavaScript usage of Jadex that comes close to Java programming.
 */
//@ServerEndpoint(value="/wswebapi")//, configurator=ServletAwareConfig.class)
public class RestWebSocket extends Endpoint
{
	/** The service proxies (cache). */
	// todo: release services after some timeout
//	protected Map<String, IService> services;
	
	/** The ongoing future calls from client. */
	protected Map<String, IFuture<?>> incalls = new HashMap<String, IFuture<?>>();
	
	/** The ongoing future calls to the client. */
	protected Map<String, Future<?>> outcalls = new HashMap<String, Future<?>>();
	
	/** The endpoint configuration. */
	protected EndpointConfig config;
	
	/** The read processors. */
	protected List<ITraverseProcessor> readprocs;
	
	/** The write processors. */
	protected List<ITraverseProcessor> writeprocs;
	
	/** The partial messages. 
	    todo: cleanup per timeout */
	protected Map<String, Map<Integer, String>> partials;

	/** debug flag (print stacktraces of unsuccessful service calls etc) **/
	protected boolean debug;

	/**
	 *  Create a new rest websocket.
	 */
	public RestWebSocket()
	{
		this.readprocs = JsonTraverser.getDefaultReadProcessorsCopy();
		this.writeprocs = JsonTraverser.getDefaultWriteProcessorsCopy();
		
		writeprocs.add(0, new org.activecomponents.webservice.json.write.JsonServiceProcessor());
		writeprocs.add(0, new org.activecomponents.webservice.json.write.JsonServiceIdentifierProcessor());
		writeprocs.add(0, new org.activecomponents.webservice.json.write.JsonResourceIdentifierProcessor());
		
		readprocs.add(0, new org.activecomponents.webservice.json.read.JsonServiceProcessor());
		readprocs.add(0, new org.activecomponents.webservice.json.read.JsonServiceIdentifierProcessor());
		readprocs.add(0, new org.activecomponents.webservice.json.read.JsonComponentIdentifierProcessor());
		readprocs.add(0, new org.activecomponents.webservice.json.read.JsonResourceIdentifierProcessor());
		
		this.partials = new HashMap<String, Map<Integer,String>>();
	}
	
	@Override
//	@OnOpen
	public void onOpen(final Session session, EndpointConfig config)
	{
		System.out.println("WebSocket opened: " + session.getId() + ", jadexsocket@"+this.hashCode());
		this.config = config;

		this.debug = getServletContext().getInitParameter("servicecall_debug")!=null?
				Boolean.parseBoolean(getServletContext().getInitParameter("servicecall_debug")): false;

		session.setMaxTextMessageBufferSize(8*1024);
		
		session.addMessageHandler(new MessageHandler.Whole<String>() 
		{
			@Override
 			public void onMessage(String text) 
			{
				try 
				{
					RestWebSocket.this.onMessage(text, session);
				}
				catch(IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

//	@OnMessage
	public void onMessage(final String txt, final Session session) throws IOException
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
			final Object msg = JsonTraverser.objectFromString(txt, this.getClass().getClassLoader(), null, Object.class, readprocs);
			
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
	
	@Override
//	@OnClose
	public void onClose(final Session session, CloseReason reason)
	{
		System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase() + ", jadexsocket@"+this.hashCode());
		
		SWebSocket.getPlatform(getServletContext()).addResultListener(new IResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess platform)
			{
				// Kill session components
				for(Object val: session.getUserProperties().values())
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
//						IComponentManagementService cms = platform.searchService(new ServiceQuery<>(IComponentManagementService.class)).get();
						platform.getExternalAccess(cid).killComponent();
						System.out.println("Killing session component: "+((IService)val).getServiceId().getProviderId());
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Could not cleanup session components: "+exception);
			}
		});
	}

	/**
	 *  Handle a partial mesage.
	 */
	protected IFuture<String> handlePartialMessage(final Session session, final PartialMessage msg)
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
				onMessage(buf.toString(), session);
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
	 *  Handle a search service method.
	 *  @param session The session.
	 */
	protected IFuture<String> handleSearchServiceMessage(final Session session, final ServiceSearchMessage ssc)
	{
		final Future<String> ret = new Future<String>();
		
		SWebSocket.getPlatform(getServletContext()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
				// Check if search type is set
				if(ssc.getType()==null || ssc.getType().getTypeName()==null || ssc.getType().getTypeName().length()==0)
				{
					sendException(new RuntimeException("Service type must not be null in service search"), ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
					return;
				}
				
//				final ServiceSearchMessage ssc = (ServiceSearchMessage)msg;
//				IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
				final Class<?> type = ssc.getType().getType(RestWebSocket.class.getClassLoader()); // todo: support default loader when using null
				
				if(type==null)
				{
					sendException(new RuntimeException("Service class not found: "+ssc.getType()), ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
					return;
				}
				
				String scope = ssc.getScope()!=null? ssc.getScope(): RequiredServiceInfo.SCOPE_GLOBAL;
//				System.out.println("Search service with scope: "+scope);
				
				if(!ssc.isMultiple())
				{
					if("session".equals(scope))
					{
//						IService service = (IService)session.getUserProperties().get(type.getName());
						String filename = findModelName(ssc.getType().getTypeName());
						if(filename==null)
						{
							sendException(new RuntimeException("Could not create session component for service, no suitable ws_serviceimpl_<class> context parameter defined in web.xml "+type.getName()), ssc.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
						}
						else
						{
							getOrCreateSessionComponent(filename, session, filename, true).
								addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
							{
								public void customResultAvailable(IExternalAccess access) throws Exception
								{
									IFuture<IService> res = (IFuture<IService>)access.searchService(new ServiceQuery<>((Class<IService>)type, RequiredServiceInfo.SCOPE_COMPONENT_ONLY));
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
					}
					else
					{
						IFuture<IService> res = (IFuture<IService>)platform.searchService(new ServiceQuery<>(type, scope));
						res.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
						{
							public void customResultAvailable(IService service)
							{
								// Found service and now send back id for generating javascript proxy
//								addService(service);
//								sendResult(getServiceInfo(service), ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
								sendResult(service, ssc.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
							}
						});
					}
				}
				else
				{
					ITerminableIntermediateFuture<IService> res = (ITerminableIntermediateFuture<IService>)platform.searchService(new ServiceQuery<>( type, scope));
					res.addResultListener(new IIntermediateResultListener<IService>()
					{
						public void intermediateResultAvailable(IService service)
						{
							System.out.println("Found service: "+service.getServiceId());
//							addService(service);
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
			}
		});
		
		return ret;
	}
	
	/**
	 *  Find the component model name for a type name.
	 *  @param typename The service type name.
	 *  @return The model name.
	 */
	protected String findModelName(String typename)
	{	
		String ret = null;
		
		Enumeration<String> pnames = getServletContext().getInitParameterNames();
		if(pnames!=null)
		{
			while(ret==null && pnames.hasMoreElements())
			{
				String pname = pnames.nextElement();
				if(pname!=null && pname.startsWith("ws_serviceimpl"))
				{
					String clname = pname.substring(pname.lastIndexOf("_")+1);
					if(clname.equals(typename))
					{
						ret = getServletContext().getInitParameter(pname);
					}
				}
			}
		}	
		
		return ret;
	}
	
	/**
	 *  Handle a service invocation method.
	 *  @param session The session.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceInvocationMessage(final Session session, final ServiceInvocationMessage sim)
	{
		final Future<String> ret = new Future<String>();
	
		SWebSocket.getPlatform(getServletContext()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
				IFuture<IService> fut = platform.searchService(new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(sim.getServiceId()));
				fut.addResultListener(new ExceptionDelegationResultListener<IService, String>(ret)
				{
					public void customResultAvailable(IService service) throws Exception
					{
						// todo: fundamental problem class loader.
						// Only the target component should need to know the service classes
						Class<?> serclazz = service.getServiceId().getServiceType().getType(RestWebSocket.class.getClassLoader());

						// decoded parameters
						Object[] decparams = new Object[sim.getParameterValues().length];
						int cnt=0;
						for(Object encp: sim.getParameterValues())
						{
							try
							{
								decparams[cnt] = JsonTraverser.objectFromString((String)encp, this.getClass().getClassLoader(), null, Object.class, readprocs);
								cnt++;
							}
							catch(Exception e)
							{
								decparams[cnt++] = new SerializedValue((String)encp);
							}
						}
						
						Tuple2<Method, Object[]> tup = findMethod(decparams, serclazz, sim.getMethodName());

						Method m = tup.getFirstEntity();
						Object[] pvals = tup.getSecondEntity();

						if(m==null)
						{
							sendException(new RuntimeException("Method not found: "+sim.getMethodName()), sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
						}
//						else if(m.getParameterTypes().length!=sim.getParameterValues().length)
//						{
//							sendException(new RuntimeException("Method signature differs: "+sim.getMethodName()), sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
//						}
						else
						{
							try
							{
								Object res = m.invoke(service, pvals);

								if(res instanceof IFuture)
								{
									incalls.put(sim.getCallid(), (IFuture<?>)res);
//									System.out.println("saving: "+sim.getCallid());

									if(res instanceof IIntermediateFuture)
									{
										((IIntermediateFuture<Object>)res).addResultListener(new IIntermediateResultListener<Object>()
										{
											public void intermediateResultAvailable(Object result)
											{
//												System.out.println("ires: "+result+" "+sim.getCallid());
												sendResult(result, sim.getCallid(), session, false);//.addResultListener(new DelegationResultListener<String>(ret));
											}

											public void exceptionOccurred(Exception e)
											{
												sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
												incalls.remove(sim.getCallid());
//												System.out.println("removed ex: "+sim.getCallid());
											}

											public void finished()
											{
												sendResult(null, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
												incalls.remove(sim.getCallid());
//												System.out.println("removed fin: "+sim.getCallid());
											}

											public void resultAvailable(Collection<Object> result)
											{
												sendResult(result, sim.getCallid(), session, true).addResultListener(new DelegationResultListener<String>(ret));
												incalls.remove(sim.getCallid());
//												System.out.println("removed ra: "+sim.getCallid());
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
//												System.out.println("remove fut call res: "+sim.getCallid());
											}

											public void exceptionOccurred(Exception e)
											{
												sendException(e, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
												incalls.remove(sim.getCallid());
//												System.out.println("remove fut call ex: "+sim.getCallid());
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
                                IllegalArgumentException descriptiveException = new IllegalArgumentException("Trying to call: " + m + "\n\t with parameters: " + Arrays.toString(pvals), e);
								sendException(descriptiveException, sim.getCallid(), session).addResultListener(new DelegationResultListener<String>(ret));
							}
						}
					}
				});
			}
		});

		return ret;
	}

	/**
	 *  Fin the correct method by its name and parameter values.
	 *  The parameter values are half-evaluated, i.e. if they are
	 *  deserialized as far as possible. Still serialized parameters
	 *  are saved as SerialiedObject. Those are deserialized using
	 *  the parameter class as hint.
	 *
	 *  @param decparams Partially decoded parameters.
	 *  @param serclazz The target class.
	 *  @param methodname The method name
	 *  @return The method and further decoded parameters.
	 */
	protected Tuple2<Method, Object[]> findMethod(Object[] params, Class<?> serclazz, String methodname)
	{
		Method ret = null;

		Method[] ms = SReflect.getMethods(serclazz, methodname);

		Object[] pvals = null;

		if(ms.length==1)
		{
			ret = ms[0];
		}
		else if(ms.length>1)
		{
			// Find the 'best' method

			// First check the number of arguments
			Set<Method> msok = new HashSet<Method>();
			Set<Method> msmaybeok = new HashSet<Method>();

			for(Method tmp1: ms)
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

				for(Iterator<Method> it=msok.iterator(); it.hasNext();)
				{
					Method meth = it.next();
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
						for(Iterator<Method> it=msmaybeok.iterator(); it.hasNext();)
						{
							Method meth = it.next();

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
		
		return new Tuple2<Method, Object[]>(ret, pvals);
	}
	
	/**
	 *  Handle a provide service method. 
	 *  Create or get the minimal agent that publishes the service using a proxy. 
	 *  @param session The session of the provider.
	 *  @param sim The message.
	 */
	protected IFuture<String> handleServiceProvideMessage(final Session session, final ServiceProvideMessage spm)
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
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
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
	protected IFuture<String> handleServiceUnprovideMessage(final Session session, final ServiceUnprovideMessage sum)
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
	protected IFuture<String> handleTerminateInvocationMessage(final Session session, final ServiceTerminateInvocationMessage stim)
	{
		final Future<String> ret = new Future<String>();
	
		SWebSocket.getPlatform(getServletContext()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
//				final ServiceTerminateInvocationMessage stim = (ServiceTerminateInvocationMessage)msg;
				
//				System.out.println("Abort: "+stim);
				
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
			}
		});
		
		return ret;
	}
	
	/**
	 *  Handle a result message from a call to the client. 
	 *  @param session The session.
	 *  @param rm The message.
	 */
	protected IFuture<String> handleResultMessage(final Session session, final ResultMessage rm)
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
	protected IFuture<String> handlePullResultMessage(final Session session, final PullResultMessage prm)
	{
		final Future<String> ret = new Future<String>();
		
		SWebSocket.getPlatform(getServletContext()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, String>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
//				final PullResultMessage prm = (PullResultMessage)msg;
				
//				System.out.println("Abort: "+stim);
				
				IFuture<?> fut = incalls.get(prm.getCallid());
				if(fut instanceof IPullIntermediateFuture)
				{
					((IPullIntermediateFuture<?>)fut).pullIntermediateResult();
//					System.out.println("Pulled intermediate result.");
				}
				else
				{
					System.out.println("Cannot pull result: "+fut+" "+prm.getCallid());
				}
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Get the http session.
//	 *  @return The http session.
//	 */
//	protected HttpSession getHttpSession()
//	{
//		return (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
//	}
	
	/**
	 *  Get the servlet context.
	 *  @return The servlet context.
	 */
	protected ServletContext getServletContext()
	{
		return (ServletContext)config.getUserProperties().get("servletcontext");
//		return (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
	}
	
	/**
	 *  Generate call parameters.
	 *  @param vals The current parameters.
	 *  @return The adapted method call parameters.
	 */
	protected Object[] generateParameters(Object[] vals, Method m) throws Exception
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
	}
	
	/**
	 *  Convert a parameter to a target type.
	 */
	protected Object convertParameter(Object value, Type targettype) throws Exception
	{
		Class<?> targetclass = SReflect.getClass(targettype);
		Class<?> targetclasswrapped = SReflect.getWrappedType(targetclass);
		
		if(value instanceof SerializedValue)
		{
			String text = ((SerializedValue)value).getValue();
			try
			{
				value = JsonTraverser.objectFromString(text, this.getClass().getClassLoader(), null, targetclass, readprocs);
			}
			catch(Exception e)
			{
				value = JsonTraverser.objectFromString(text, this.getClass().getClassLoader(), null, targetclasswrapped, readprocs);
			}
		}

		Object ret = value;

		Class<?> valuewrapped = SReflect.getWrappedType(value.getClass());

		if(!SReflect.isSupertype(targetclass, valuewrapped))
		{
//			System.out.println("type problem: "+targetType+" "+actualValueWrapped+" "+sim.getParameterValues()[i]);
			
			if(value instanceof String)
			{
				if(BasicTypeConverter.isExtendedBuiltInType(targetclass))
				{
					IStringObjectConverter conv = BasicTypeConverter.getExtendedStringConverter(targetclass);
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
	}
	
	/**
	 *  Send a result message back to the client.
	 */
	protected IFuture<String> sendResult(Object res, String callid, Session session, boolean finished)
	{
//		if(finished && res!=null && res.getClass().toString().indexOf("ChatEvent")!=-1)
//			System.out.println("removing call: "+callid);
		return sendMessage(new ResultMessage(res, callid, finished), session);
	}
	
	/**
	 *  Send an exception message back to the client.
	 */
	protected IFuture<String> sendException(Exception ex, String callid, Session session)
	{
//		System.out.println("removing call ex: "+callid);
		if (debug) ex.printStackTrace();
		return sendMessage(new ResultMessage(ex, callid), session);
	}
	
	/**
	 *  Send an message back to the client.
	 */
	protected IFuture<String> sendMessage(BaseMessage message, Session session)
	{
		Future<String> ret = new Future<String>();

		try
		{
			// todo: problem classloader

			// TODO: unwrap types here instead frontend-side?

			// ensure single threaded access to socket (how to do without lock)?
			String data = JsonTraverser.objectToString(message, this.getClass().getClassLoader(), true, null, writeprocs);
			synchronized(this)
			{
				session.getBasicRemote().sendText(data);
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
	 *  Get or create a session component under the given key.
	 */
	protected IFuture<IExternalAccess> getOrCreateSessionComponent(final String key, final Session session, final String filename, final boolean create)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		SWebSocket.getPlatform(getServletContext()).addResultListener(new DelegationResultListener<IExternalAccess>(ret)
		{
			public void customResultAvailable(IExternalAccess platform)
			{
				Object ma = session.getUserProperties().get(key);
				
				if(ma instanceof IFuture)
				{
					((IFuture<IExternalAccess>)ma).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
				}
				else if(ma==null)
				{
					if(create)
					{
						session.getUserProperties().put(key, ret);

						platform.createComponent(new CreationInfo().setFilename(filename)).addResultListener(new IResultListener<IExternalAccess>()
						{
							public void resultAvailable(IExternalAccess exta)
							{
								exta.waitForTermination().addResultListener(new IResultListener<Map<String,Object>>()
								{
									public void resultAvailable(java.util.Map<String,Object> result)
									{
										try
										{
											session.getUserProperties().remove(key);
										}
										catch(IllegalStateException e)
										{
											// nop when session is already closed
										}
										catch(Exception e)
										{
//												System.out.println("Could not remove component from session: "+key);
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
			}
		});
		
		return ret;
	}


	/**
	 *
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
}