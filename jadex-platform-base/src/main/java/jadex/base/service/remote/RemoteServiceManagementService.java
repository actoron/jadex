package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.service.AnyResultSelector;
import jadex.service.BasicService;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IVisitDecider;
import jadex.service.SServiceProvider;
import jadex.service.TypeResultSelector;
import jadex.service.clock.ITimer;
import jadex.service.library.ILibraryService;
import jadex.xml.bean.JavaWriter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *  The remote service management service is responsible for 
 *  handling remote service invocations (similar to RMI).
 */
public class RemoteServiceManagementService extends BasicService implements IRemoteServiceManagementService
{
	//-------- constants --------
	
	/** Excluded remote methods (for all methods)
	    Excluded methods throw UnsupportedOperationException. */
	public static String REMOTE_EXCLUDED = "remote_excluded";
	
	/** Uncached remote methods (for methods with no parameters)
	    Uncached methods will be invoked on every call. */
	public static String REMOTE_UNCACHED = "remote_uncached";
	
	/** Synchronous remote methods (for methods with void return value). 
	    If void methods are declared synchronous they will block the caller until
	    the method has been executed on the remote side (exception thus can arrive). */
	public static String REMOTE_SYNCHRONOUS = "remote_synchronous";
	
	/** The default timeout. */
	public static long DEFAULT_TIMEOUT = 10000;
	
	//-------- attributes --------
	
	/** The component. */
	protected IMicroExternalAccess component;
	
	/** The waiting futures. */
	protected Map waitingcalls;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote service management service.
	 */
	public RemoteServiceManagementService(IMicroExternalAccess component)
	{
		super(component.getServiceProvider().getId(), IRemoteServiceManagementService.class, null);

		this.component = component;
		this.waitingcalls = Collections.synchronizedMap(new HashMap());
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
			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				
				// Hack! create remote rms cid with "rms" assumption.
				IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
				final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
				RemoteSearchCommand content = new RemoteSearchCommand(cid, manager, 
					decider, selector, callid);
				
				sendMessage(component, rrms, content, callid, -1, waitingcalls, ret);
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
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
			new AnyResultSelector(false, true));
	}
	
	/**
	 *  Get an external access proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid Component target id.
	 *  @return External access of remote component. 
	 */
	public IFuture getExternalAccessProxy(final IComponentIdentifier cid, final Class targetclass)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				
				// Hack! create remote rms cid with "rms" assumption.
				IComponentIdentifier rrms = cms.createComponentIdentifier("rms@"+cid.getPlatformName(), false, cid.getAddresses());
				final String callid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
				RemoteGetExternalAccessCommand content = new RemoteGetExternalAccessCommand(cid, targetclass, callid);
				
				sendMessage(component, rrms, content, callid, -1, waitingcalls, ret);
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
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
	 */
	public Map getWaitingCalls()
	{
		return waitingcalls;
	}
	
	protected static Map errors = Collections.synchronizedMap(new HashMap());
	
	/**
	 * final IComponentIdentifier sender,
	 */
	public static void sendMessage(final IMicroExternalAccess component, IComponentIdentifier receiver, final Object content,
		final String callid, final long to, final Map waitingcalls, final Future future)
	{
		final long timeout = to<=0? DEFAULT_TIMEOUT: to;
		
		waitingcalls.put(callid, future);
		
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{receiver});
		msg.put(SFipa.CONVERSATION_ID, callid);
//		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		
		SServiceProvider.getService(component.getServiceProvider(), ILibraryService.class)
			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(component.getServiceProvider(), IMessageService.class)
					.addResultListener(component.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Hack!!! Manual encoding for using custom class loader at receiver side.
						msg.put(SFipa.CONTENT, JavaWriter.objectToXML(content, ls.getClassLoader()));

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
												waitingcalls.remove(callid);
												future.setExceptionIfUndone(new RuntimeException("No reply received and timeout occurred: "+callid+" "+msg));
											}
										}).addResultListener(component.createResultListener(new DefaultResultListener()
										{
											public void resultAvailable(Object source, Object result)
											{
												// cancel timer when future is finished before. 
												final ITimer timer = (ITimer)result;
												future.addResultListener(new IResultListener()
												{
													public void resultAvailable(Object source, Object result)
													{
//														System.out.println("Cancel timeout (res): "+callid+" "+future);
														errors.put(callid, new Object[]{"Cancel timeout (res)", result});
														timer.cancel();
													}
													
													public void exceptionOccurred(Object source, Exception exception)
													{
//														System.out.println("Cancel timeout (ex): "+callid+" "+future);
														errors.put(callid, new Object[]{"Cancel timeout (ex):", exception});
														timer.cancel();
													}
												});
											}
										}));
									}
								});
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								// message could not be sent -> fail immediately.
//								System.out.println("Callee could not be reached: "+exception);
								errors.put(callid, new Object[]{"Callee could not be reached", exception});
								waitingcalls.remove(callid);
								future.setException(exception);
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						errors.put(callid, new Object[]{"No msg service", exception});
						waitingcalls.remove(callid);
						future.setException(exception);
					}
				}));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				errors.put(callid, new Object[]{"No lib service", exception});
				waitingcalls.remove(callid);
				future.setException(exception);
			}
		}));
	}
}

