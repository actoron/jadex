package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.base.service.remote.commands.AbstractRemoteCommand;
import jadex.base.service.remote.commands.RemoteGetExternalAccessCommand;
import jadex.base.service.remote.commands.RemoteResultCommand;
import jadex.base.service.remote.commands.RemoteSearchCommand;
import jadex.base.service.remote.xml.RMIPostProcessor;
import jadex.base.service.remote.xml.RMIPreProcessor;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.AnyResultSelector;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.TypeResultSelector;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IRemotable;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.Reader;
import jadex.xml.writer.WriteContext;
import jadex.xml.writer.Writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/* $if !android $ */
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
/* $else $
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLStreamException;
$endif $ */

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

	/** Timeout for remote method invocation. */
	public static String REMOTE_TIMEOUT = "remote_timeout";

	/** The default timeout. */
	public static long DEFAULT_TIMEOUT = 10000;
		
	//-------- attributes --------
	
	/** The component. */
	protected IMicroExternalAccess component;
	
	/** The map of waiting calls (callid -> future). */
	protected Map waitingcalls;

	/** The remote reference module. */
	protected RemoteReferenceModule rrm;
	
	/** The rmi object to xml writer. */
	protected Writer writer;
	
	/** The rmi xml to object reader. */
	protected Reader reader;
	
	/** The timer. */
	protected Timer	timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote service management service.
	 */
	public RemoteServiceManagementService(IMicroExternalAccess component,ILibraryService libservice)
	{
		super(component.getServiceProvider().getId(), IRemoteServiceManagementService.class, null);

		this.component = component;
		this.rrm = new RemoteReferenceModule(this, libservice);
		this.waitingcalls = new HashMap();
		this.timer	= new Timer(true);
		
		QName[] pr = new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.base.service.remote", "ProxyReference")};
		
		Set typeinfosread = JavaReader.getTypeInfos();
		TypeInfo ti_rr = new TypeInfo(new XMLInfo(pr), 
			new ObjectInfo(ProxyReference.class, new RMIPostProcessor(rrm)));
		typeinfosread.add(ti_rr);
		
		Set typeinfoswrite = JavaWriter.getTypeInfos();
		final RMIPreProcessor preproc = new RMIPreProcessor(rrm);
		TypeInfo ti_proxyable = new TypeInfo(new XMLInfo(pr, null, false, preproc), 
			new ObjectInfo(IRemotable.class));
		typeinfoswrite.add(ti_proxyable);
		
		this.reader = new Reader(new TypeInfoPathManager(typeinfosread), false, false, false, new XMLReporter()
		{
			public void report(String message, String error, Object info, Location location)
				throws XMLStreamException
			{
				List	errors	= (List)((ReadContext)Reader.READ_CONTEXT.get()).getUserContext();
				errors.add(new Tuple(new Object[]{message, error, info, location}));
			}
		}, new BeanObjectReaderHandler(typeinfosread));
		this.writer = new Writer(new BeanObjectWriterHandler(typeinfoswrite, true))
		{
			public void writeObject(WriteContext wc, Object object, QName tag) throws Exception 
			{
//				System.out.println("object: "+object);
//				if(object instanceof RemoteResultCommand)
//					System.out.println("huhuhu");
				
				if(SServiceProvider.isRemoteReference(object))
				{
//					System.out.println("changed: "+object.getClass()+" "+object);
					object = preproc.preProcess(wc, object);
				}
//				else
//				{
//					System.out.println("kept: "+object.getClass()+" "+object);
//				}
				
				// Perform pojo service replacement (for local and remote calls).
				// Test if it is pojo service impl.
				// Has to be mapped to new proxy then
				
				if(object!=null && !(object instanceof BasicService) && object.getClass().isAnnotationPresent(Service.class))
				{
					System.out.println("test");
					// Check if the argument type refers to the pojo service
//					Service ser = object.getClass().getAnnotation(Service.class);
//					if(SReflect.isSupertype(ser.value(), sic.getMethod().getParameterTypes()[i]))
					{
						object = BasicServiceInvocationHandler.getPojoServiceProxy(object);
						System.out.println("proxy: "+object);
					}
				}
				
				super.writeObject(wc, object, tag);
			};
		};
	}
	
	//-------- methods --------
	
	/**
	 *  Get service proxies from a remote platform.
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
		Future ret = new Future();
		
		component.scheduleStep(new IComponentStep<Object>()
		{
			@XMLClassname("getServiceProxies")
			public IFuture<Object> execute(IInternalAccess ia)
			{
				RemoteServiceManagementAgent agent = (RemoteServiceManagementAgent)ia;
				final Future fut = new Future();
				SServiceProvider.getService(component.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(agent.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						
						// Hack! create remote rms cid with "rms" assumption.
						IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
						final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
						RemoteSearchCommand content = new RemoteSearchCommand(cid, manager, 
							decider, selector, callid);
						
						sendMessage(rrms, content, callid, -1, fut);
					}
					public void exceptionOccurred(Exception exception)
					{
						fut.setException(exception);
					}
				}));
				
				return fut;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get a service proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid	The remote provider id.
	 *  @param service	The service type.
	 *  @param scope	The search scope. 
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxy(final IComponentIdentifier cid, final Class service, String scope)
	{
		Future	ret	= new Future();
		getServiceProxies(cid, SServiceProvider.getSearchManager(false, scope), SServiceProvider.getVisitDecider(true, scope), 
			new TypeResultSelector(service, true)).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(result!=null && !((Collection)result).isEmpty())
					super.customResultAvailable(((Collection)result).iterator().next());
				else
					super.exceptionOccurred(new ServiceNotFoundException("No proxy for service found: "+cid+", "+service.getName()));
			}
		});
		return ret;
	}
	
	/**
	 *  Get all service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid	The remote provider id.
	 *  @param service	The service type.
	 *  @param scope	The search scope. 
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxies(IComponentIdentifier cid, Class service, String scope)
	{
		return getServiceProxies(cid, SServiceProvider.getSearchManager(true, scope), SServiceProvider.getVisitDecider(false, scope), 
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
		
		component.scheduleStep(new IComponentStep<Object>()
		{
			@XMLClassname("getExternalAccessProxy")
			public IFuture<Object> execute(IInternalAccess ia)
			{
				final Future fut = new Future();
				RemoteServiceManagementAgent agent = (RemoteServiceManagementAgent)ia;
				SServiceProvider.getService(component.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(agent.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						
						// Hack! create remote rms cid with "rms" assumption.
						IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
						final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
						RemoteGetExternalAccessCommand content = new RemoteGetExternalAccessCommand(cid, callid);
						
						sendMessage(rrms, content, callid, -1, fut);
					}
					public void exceptionOccurred(Exception exception)
					{
						fut.setException(exception);
					}
				}));
				
				return fut;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}

	/**
	 *  Get the component.
	 *  @return the component.
	 */
	public IMicroExternalAccess getComponent()
	{
		return component;
	}
	
	/**
	 *  Get the rms component identifier.
	 *  @return The rms component identifier.
	 */
	public IComponentIdentifier getRMSComponentIdentifier()
	{
		return component.getComponentIdentifier();
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
	 *  Get the reader.
	 *  @return The reader.
	 */
	public Reader getReader()
	{
		return reader;
	}

	/**
	 *  Get the writer.
	 *  @return the writer.
	 */
	public Writer getWriter()
	{
		return writer;
	}
	
	/**
	 *  Add a new waiting call.
	 *  @param callid The callid.
	 *  @param future The future.
	 */
	public void putWaitingCall(String callid, Future future)
	{
		getRemoteReferenceModule().checkThread();
		waitingcalls.put(callid, future);
	}
	
	/**
	 *  Get a waiting call future.
	 *  @param callid The callid.
	 *  @return The future.
	 */
	public Future getWaitingCall(String callid)
	{
		getRemoteReferenceModule().checkThread();
		return (Future)waitingcalls.get(callid);
	}
	
	/**
	 *  Remove a waiting call.
	 *  @param callid The callid.
	 *  @return The future.
	 */
	public Future removeWaitingCall(String callid)
	{
		getRemoteReferenceModule().checkThread();
		return (Future)waitingcalls.remove(callid);
	}
	
	/**
	 *  Get the remote reference module.
	 *  @return the rrm.
	 */
	public RemoteReferenceModule getRemoteReferenceModule()
	{
		return rrm;
	}
	
//	protected static Map errors = Collections.synchronizedMap(new LRU(200));
//	public Map interestingcalls = new HashMap();
	/**
	 *  Send the request message of a remote method invocation.
	 *  (Can savely be called from any thread).
	 */
	public void sendMessage(final IComponentIdentifier receiver, final Object content,
		final String callid, final long to, final Future future)
	{
		component.scheduleStep(new IComponentStep<Void>()
		{
			@XMLClassname("sendMessage")
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				if(content instanceof AbstractRemoteCommand)
				{
					((AbstractRemoteCommand)content).preprocessCommand(rrm, receiver);
				}
				
				final long timeout = to<=0? DEFAULT_TIMEOUT: to;
				
				putWaitingCall(callid, future);
//				System.out.println("Waitingcalls: "+waitingcalls.size());
				
				final Map msg = new HashMap();
				msg.put(SFipa.SENDER, component.getComponentIdentifier());
				msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
				msg.put(SFipa.CONVERSATION_ID, callid);
//				msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
				
				SServiceProvider.getService(component.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(component.getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(ia.createResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								ls.getClassLoader(component.getModel().getResourceIdentifier()).addResultListener(new IResultListener()
								{
									public void resultAvailable(Object result)
									{
										ClassLoader cl = (ClassLoader)result;
										
										// todo: use rid of sender?! (not important as writer does not use classloader, only nuggets)
//										ClassLoader cl = ls.getClassLoader(component.getModel().getResourceIdentifier());
										
										// Hack!!! Manual encoding for using custom class loader at receiver side.
//										msg.put(SFipa.CONTENT, JavaWriter.objectToXML(content, ls.getClassLoader()));
										
//										System.out.println("sent: "+callid);
//										System.out.println("RMS sending to: "+receiver+", "+(content!=null?SReflect.getClassName(content.getClass()):null));
										
										
										String cont = Writer.objectToXML(getWriter(), content, cl, receiver);
										msg.put(SFipa.CONTENT, cont);
										
//										if(cont.indexOf("getServices")!=-1)
//										{
//											interestingcalls.put(callid, cont);
//										}
										
										IMessageService ms = (IMessageService)result;
										ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), component.getModel().getResourceIdentifier(), null)
											.addResultListener(ia.createResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
												// ok message could be sent.
												timer.schedule(new TimerTask()
												{
													public void run()
													{
														ia.getExternalAccess().scheduleStep(new IComponentStep<Void>()
														{
															public IFuture<Void> execute(IInternalAccess ia)
															{
																if(!future.isDone())
																{
																	removeWaitingCall(callid);
																	future.setExceptionIfUndone(new RuntimeException("No reply received and timeout occurred: "+callid)
																	{
																		public void printStackTrace()
																		{
																			Thread.dumpStack();
																			super.printStackTrace();
																		}
																	}
																	);
																}
																return IFuture.DONE;
															}
														});
//														System.out.println("timeout triggered: "+msg);
													}
												}, timeout);
												

												future.addResultListener(ia.createResultListener(new IResultListener()
												{
													public void resultAvailable(Object result)
													{
														removeWaitingCall(callid);
//														System.out.println("Waitingcalls: "+waitingcalls.size());
//														System.out.println("Cancel timeout (res): "+callid+" "+future);
//														errors.put(callid, new Object[]{"Cancel timeout (res)", result});
													}
																	
													public void exceptionOccurred(Exception exception)
													{
//														exception.printStackTrace();
														removeWaitingCall(callid);
//														System.out.println("Waitingcalls: "+waitingcalls.size());
//														System.out.println("Cancel timeout (ex): "+callid+" "+future);
//														errors.put(callid, new Object[]{"Cancel timeout (ex):", exception});
//														ia.getLogger().warning("Remote request failed: "+content);
														ia.getLogger().info("Remote exception occurred: "+exception.toString());
													}
												}));
											}
											
											public void exceptionOccurred(Exception exception)
											{
												// message could not be sent -> fail immediately.
//												System.out.println("Callee could not be reached: "+exception);
//												errors.put(callid, new Object[]{"Callee could not be reached", exception});
												future.setException(exception);
												removeWaitingCall(callid);
//												waitingcalls.remove(callid);
//												System.out.println("Waitingcalls: "+waitingcalls.size());
											}
										}));
									}
									public void exceptionOccurred(Exception exception)
									{
//										System.out.println("Classlooder not found: "+exception);
//										errors.put(callid, new Object[]{"No msg service", exception});
										removeWaitingCall(callid);
//										waitingcalls.remove(callid);
//										System.out.println("Waitingcalls: "+waitingcalls.size());
										future.setException(exception);
									}
								});
							}
							
							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("Message service not found: "+exception);
//								errors.put(callid, new Object[]{"No msg service", exception});
								removeWaitingCall(callid);
//								waitingcalls.remove(callid);
//								System.out.println("Waitingcalls: "+waitingcalls.size());
								future.setException(exception);
							}
						}));
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("Library service not found: "+exception);
//						errors.put(callid, new Object[]{"No lib service", exception});
						removeWaitingCall(callid);
//						waitingcalls.remove(callid);
//						System.out.println("Waitingcalls: "+waitingcalls.size());
						future.setException(exception);
					}
				}));
				
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Called when the service is shut down.
	 */
	public IFuture<Void> shutdownService()
	{
		timer.cancel();
		return super.shutdownService();
	}
}

