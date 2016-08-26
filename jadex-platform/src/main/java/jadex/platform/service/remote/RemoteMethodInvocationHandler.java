package jadex.platform.service.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.ISwitchCall;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.IntelligentProxyInterceptor;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.PullIntermediateDelegationFuture;
import jadex.commons.future.PullSubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.Tuple2Future;
import jadex.commons.transformation.annotations.Classname;
import jadex.platform.service.remote.commands.RemoteFutureBackwardCommand;
import jadex.platform.service.remote.commands.RemoteFuturePullCommand;
import jadex.platform.service.remote.commands.RemoteFutureTerminationCommand;
import jadex.platform.service.remote.commands.RemoteMethodInvocationCommand;

/**
 *  Class that implements the Java proxy InvocationHandler, which
 *  is called when a method on a proxy is called.
 */
public class RemoteMethodInvocationHandler implements InvocationHandler, ISwitchCall //extends MethodListenerHandler
{
	protected static final Method schedulestep;
	
	protected static final Method finalize;
	
	static
	{
		try
		{
			finalize = IFinalize.class.getMethod("finalize", new Class[0]);
			schedulestep = IExternalAccess.class.getMethod("scheduleStep", new Class[]{IComponentStep.class});
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	//-------- attributes --------
	
	/** The remote service management service. */
	protected RemoteServiceManagementService rsms;

	/** The proxy reference. */
	protected ProxyReference pr;
	
	/** The target resolver. */
	protected ITargetResolver tr;
		
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public RemoteMethodInvocationHandler(RemoteServiceManagementService rsms, ProxyReference pr)
	{
		this.rsms = rsms;
		this.pr = pr;
//		System.out.println("handler: "+pi.getServiceIdentifier().getServiceType()+" "+pi.getCache());
	}
	
	//-------- methods --------
	
//	public static Object debugcallid	= null;	
	
	/**
	 *  Invoke a method.
	 */
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{		
		final IComponentIdentifier compid = rsms.getRMSComponentIdentifier();
		final String callid = SUtil.createUniqueId(compid.getName()+".0."+method.toString());
	
//		notifyMethodListeners(true, proxy, method, args, callid);
		
		final ProxyInfo pi = pr.getProxyInfo();
		
		if(pi.isExcluded(method)) 
			throw new UnsupportedOperationException("The method is excluded for remote: " + method);
		
		// Determine if call goes to
		// a) cached method
		// b) method replacement
		// c) finalize method
		// d) real method invocation
		
		if(pr.getCache()!=null && !pi.isUncached(method) && !pi.isReplaced(method))
		{
			Class<?> rt = method.getReturnType();
			Class<?>[] ar = method.getParameterTypes();
			if(!rt.equals(void.class) && !(SReflect.isSupertype(IFuture.class, rt)) && ar.length==0)
			{
				Object res = pr.getCache().get(method.getName());
				if(res instanceof Throwable && SReflect.isSupertype(Throwable.class, rt))
				{
					throw (Throwable)res;
				}
				else
				{
					return res;
				}
			}
		}
		
		// Test if method has a replacement command.
		IMethodReplacement	replacement	= pi.getMethodReplacement(method);
		if(replacement!=null)
		{
			// Todo: super pointer for around-advice-like replacements.
			return replacement.invoke(proxy, args);
		}
		
		// Test if finalize is called.
		if(finalize.equals(method))
		{
//			System.out.println("Finalize called on: "+proxy);
			rsms.component.scheduleStep(new IComponentStep<Void>()
			{
				@Classname("fin")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					rsms.getRemoteReferenceModule().decProxyCount(pr.getRemoteReference());
					return IFuture.DONE;
				}
			});
			return null;
		}
		
		final ServiceInvocationContext sic = ServiceInvocationContext.SICS.get();
		ServiceInvocationContext.SICS.remove();
		
		// Get the current service invocation 
//		ServiceCall invoc = ServiceCall.getCurrentInvocation();
		Map<String, Object>	props	= new HashMap<String, Object>();
//		props.put("method2", method.getName());
		// Must use call from ServiceCall because could not have required chain
		ServiceCall invoc = ServiceCall.getOrCreateNextInvocation(props);
//		ServiceCall invoc = sic.getServiceCall();
		
		// The reatime property is not necessary, as currently message are sent with realtime timeouts always  
		
//		if(method.getName().indexOf("schedule")!=-1)
//		System.out.println("step: "+method.getName()+", "+invoc);
		
		Map<String, Object> nf = invoc!=null? invoc.getProperties(): new HashMap<String, Object>();
		boolean sec = pi.isSecure(method);
		if(sec)
			nf.put(SecureTransmission.SECURE_TRANSMISSION, sec? Boolean.TRUE: Boolean.FALSE);
		final long to = invoc!=null && invoc.hasUserTimeout()? invoc.getTimeout(): pi.getMethodTimeout(rsms.getComponent().getComponentIdentifier(), method);
		nf.put(Timeout.TIMEOUT, Long.valueOf(to));
		final Map<String, Object> nonfunc = nf; 
		
		Class<?> type = determineReturnType(proxy, method, args);
		Future<Object> future = createReturnFuture(compid, callid, method, type, to, nonfunc, sic);

		// determine the call target
		// can be redirected by intelligent proxies
		ITargetResolver tr = getTargetResolver();
		if(tr!=null)
		{
			IServiceIdentifier sid = (IServiceIdentifier)pr.getRemoteReference().getTargetIdentifier();
			
			IntelligentProxyInterceptor.invoke(null, sic, sid, rsms.getComponent(), tr, 3, 0).addResultListener(new DelegationResultListener<Object>(future)); 

//			final Future<Object> ffuture = future;
//			tr.determineTarget((IServiceIdentifier)pr.getRemoteReference().getTargetIdentifier(), rsms.getComponent())
//				.addResultListener(new ExceptionDelegationResultListener<IService, Object>(future) 
//			{
//				public void customResultAvailable(IService ser) 
//				{
//					IServiceIdentifier sid = ser.getServiceIdentifier();
//					IComponentIdentifier rrms = new ComponentIdentifier("rms@system."+sid.getProviderId().getPlatformName(), sid.getProviderId().getAddresses());
//					RemoteReference rr = new RemoteReference(rrms, sid);
//					// non-func is in command to let stream handlers access the properties in RMI processing
//					final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
//						rr, method, args, callid, IComponentIdentifier.LOCAL.get(), nonfunc);
//					
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("invoke: "+method.getName());
////					if(method.getName().equals("getResult"))
////						System.out.println("sending invoke");
//					rsms.sendMessage(rr.getRemoteManagementServiceIdentifier(), 
//						null, content, callid, to, ffuture, nonfunc, sic);
//					
////					// Provide alternative immediate future result, if method is asynchronous.
////					if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
////					{
//////						System.out.println("Warning, void method call will be executed asynchronously: "
//////							+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
////						future	= new Future(null);
////					}
//				}
//			});
		}
		else
		{
			// non-func is in command to let stream handlers access the properties in RMI processing
			final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
				pr.getRemoteReference(), method, args, callid, IComponentIdentifier.LOCAL.get(), nonfunc);
			
			// Can be invoked directly, because internally redirects to agent thread.
//			System.out.println("invoke: "+method.getName());
//			if(method.getName().equals("getResult"))
//				System.out.println("sending invoke");
			IComponentIdentifier servicecid = null;
			if (pr.getRemoteReference().getTargetIdentifier() instanceof IComponentIdentifier)
				servicecid = (IComponentIdentifier) pr.getRemoteReference().getTargetIdentifier();
			else if (pr.getRemoteReference().getTargetIdentifier() instanceof IServiceIdentifier)
				servicecid = ((IServiceIdentifier)pr.getRemoteReference().getTargetIdentifier()).getProviderId();
			rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(),
				servicecid, content, callid, to, future, nonfunc, sic);
			
			// Provide alternative immediate future result, if method is asynchronous.
			if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
			{
//				System.out.println("Warning, void method call will be executed asynchronously: "
//					+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
				future	= new Future<Object>((Object)null);
			}
		}
		
		CallAccess.resetNextInvocation();
		// todo: also set last call in future
		
		return future;
	}
	
//	/**
//	 *  Invoke a method.
//	 */
//	protected Object internalInvoke(final Object proxy, final Method method, final Object[] args) throws Throwable
//	{
//		// non-func is in command to let stream handlers access the properties in RMI processing
//		final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
//			pr.getRemoteReference(), method, args, callid, IComponentIdentifier.LOCAL.get(), nonfunc);
//		
//		// Can be invoked directly, because internally redirects to agent thread.
////		System.out.println("invoke: "+method.getName());
////		if(method.getName().equals("getResult"))
////			System.out.println("sending invoke");
//		rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
//			null, content, callid, to, future, nonfunc, sic);
//		
//		// Provide alternative immediate future result, if method is asynchronous.
//		if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
//		{
////			System.out.println("Warning, void method call will be executed asynchronously: "
////				+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
//			future	= new Future(null);
//		}
//	}
//		final IComponentIdentifier compid = rsms.getRMSComponentIdentifier();
//		final String callid = SUtil.createUniqueId(compid.getName()+".0."+method.toString());
//		
////		notifyMethodListeners(true, proxy, method, args, callid);
//		
//		ProxyInfo pi = pr.getProxyInfo();
//		
//		// Determine if call goes to
//		// a) cached method
//		// b) method replacement
//		// c) finalize method
//		// d) real method invocation
//		
//		// determine the call target
//		// can be redirected by intelligent proxies
////		final ProxyReference pr = pi.getTargetDeterminer()!=null? pi.getTargetDeterminer().determineTarget(): pr;
//		
//		final ServiceInvocationContext sic = ServiceInvocationContext.SICS.get();
//		ServiceInvocationContext.SICS.remove();
//		
//		// Get the current service invocation 
////		ServiceCall invoc = ServiceCall.getCurrentInvocation();
//		Map<String, Object>	props	= new HashMap<String, Object>();
////		props.put("method2", method.getName());
//		ServiceCall invoc = ServiceCall.getOrCreateNextInvocation(props);
//		
//		// Get method timeout
//		final long to = invoc!=null && invoc.hasUserTimeout()? invoc.getTimeout(): pi.getMethodTimeout(method);
//		// The reatime property is not necessary, as currently message are sent with realtime timeouts always  
//		
////		if(method.getName().indexOf("schedule")!=-1)
////		System.out.println("step: "+method.getName()+", "+invoc);
//		
//		// Get the secure transmission
//		boolean sec = pi.isSecure(method);
//		
//		Map<String, Object> nf = invoc!=null? invoc.getProperties(): new HashMap<String, Object>();
//		if(sec)
//		{
//			nf.put(SecureTransmission.SECURE_TRANSMISSION, sec? Boolean.TRUE: Boolean.FALSE);
//		}
//		nf.put(Timeout.TIMEOUT, Long.valueOf(to));
//		final Map<String, Object> nonfunc = nf; 
//		
//		CallAccess.resetNextInvocation(); 
//		
//		Future future;
//		Class type = determineReturnType(proxy, method, args);
////		Class type = method.getReturnType();
//
//		if(SReflect.isSupertype(IPullSubscriptionIntermediateFuture.class, type))
//		{
//			future = new PullSubscriptionIntermediateDelegationFuture()
//			{
//				public void pullIntermediateResult() 
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
//					RemoteFuturePullCommand content = new RemoteFuturePullCommand(mycallid, callid);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending terminate");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				public void terminate(Exception reason) 
//				{
//					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
//					boolean	set	= setExceptionIfUndone(reason);
//					
//					// Send message to announce termination to remote
//					if(set)
//					{
//						Future<Object> res = new Future<Object>();
//	//					res.addResultListener(new IResultListener()
//	//					{
//	//						public void resultAvailable(Object result)
//	//						{
//	//							System.out.println("received result: "+result);
//	//						}
//	//						public void exceptionOccurred(Exception exception)
//	//						{
//	//							System.out.println("received exception: "+exception);
//	//						}
//	//					});
//						final String mycallid = SUtil.createUniqueId(compid.getName()+".pullsub."+method.toString());
//						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
//						// Can be invoked directly, because internally redirects to agent thread.
//	//					System.out.println("sending terminate");
//						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//							content, mycallid, to, res, nonfunc, sic);
//					}
//				}
//				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".pullsub."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				// Called from delegation listeners in RMS -> ignore if already terminated
//				public void setException(Exception exception)
//				{
//					super.setExceptionIfUndone(exception);
//				}
//			};
//		}
//		else if(SReflect.isSupertype(IPullIntermediateFuture.class, type))
//		{
//			future = new PullIntermediateDelegationFuture()
//			{
//				public void pullIntermediateResult() 
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
//					RemoteFuturePullCommand content = new RemoteFuturePullCommand(mycallid, callid);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending terminate");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				public void terminate(Exception reason) 
//				{
//					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
//					boolean	set	= setExceptionIfUndone(reason);
//					
//					// Send message to announce termination to remote
//					if(set)
//					{
//						Future<Object> res = new Future<Object>();
//	//					res.addResultListener(new IResultListener()
//	//					{
//	//						public void resultAvailable(Object result)
//	//						{
//	//							System.out.println("received result: "+result);
//	//						}
//	//						public void exceptionOccurred(Exception exception)
//	//						{
//	//							System.out.println("received exception: "+exception);
//	//						}
//	//					});
//						final String mycallid = SUtil.createUniqueId(compid.getName()+".pull."+method.toString());
//						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
//						// Can be invoked directly, because internally redirects to agent thread.
//	//					System.out.println("sending terminate");
//						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//							content, mycallid, to, res, nonfunc, sic);
//					}
//				}
//				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".pull."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				// Called from delegation listeners in RMS -> ignore if already terminated
//				public void setException(Exception exception)
//				{
//					super.setExceptionIfUndone(exception);
//				}
//			};
//		}
//		else if(SReflect.isSupertype(ISubscriptionIntermediateFuture.class, type))
//		{
//			future = new SubscriptionIntermediateDelegationFuture()
//			{
//				public void terminate(Exception reason) 
//				{
//					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
//					boolean	set	= setExceptionIfUndone(reason);
//					
//					// Send message to announce termination to remote
//					if(set)
//					{
//						Future res = new Future();
//	//					res.addResultListener(new IResultListener()
//	//					{
//	//						public void resultAvailable(Object result)
//	//						{
//	//							System.out.println("received result: "+result);
//	//						}
//	//						public void exceptionOccurred(Exception exception)
//	//						{
//	//							System.out.println("received exception: "+exception);
//	//						}
//	//					});
//						final String mycallid = SUtil.createUniqueId(compid.getName()+".sub."+method.toString());
//						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
//						// Can be invoked directly, because internally redirects to agent thread.
//	//					System.out.println("sending terminate");
//						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//							content, mycallid, to, res, nonfunc, sic);
//					}
//				}
//				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".sub."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				// Called from delegation listeners in RMS -> ignore if already terminated
//				public void setException(Exception exception)
//				{
//					super.setExceptionIfUndone(exception);
//				}
//			};
//		}
//		else if(SReflect.isSupertype(ITerminableIntermediateFuture.class, type))
//		{
//			future = new TerminableIntermediateDelegationFuture()
//			{
//				public void terminate(Exception e) 
//				{
//					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
//					boolean	set	= setExceptionIfUndone(e);
//					
//					// Send message to announce termination to remote
//					if(set)
//					{
//						Future res = new Future();
//	//					res.addResultListener(new IResultListener()
//	//					{
//	//						public void resultAvailable(Object result)
//	//						{
//	//							System.out.println("received result: "+result);
//	//						}
//	//						public void exceptionOccurred(Exception exception)
//	//						{
//	//							System.out.println("received exception: "+exception);
//	//						}
//	//					});
//						final String mycallid = SUtil.createUniqueId(compid.getName()+".interm."+method.toString());
//						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, e);
//						// Can be invoked directly, because internally redirects to agent thread.
//	//					System.out.println("sending terminate");
//						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
//							null, content, mycallid, to, res, nonfunc, sic);
//					}
//				}
//				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".interm."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				// Called from delegation listeners in RMS -> ignore if already terminated
//				public void setException(Exception exception)
//				{
//					super.setExceptionIfUndone(exception);
//				}
//			};
//		}
//		else if(SReflect.isSupertype(ITerminableFuture.class, type))
//		{
//			future = new TerminableDelegationFuture()
//			{
//				public void terminate(Exception reason) 
//				{
//					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
//					boolean set	= setExceptionIfUndone(reason);
//					
//					// Send message to announce termination to remote
//					if(set)
//					{
//						Future res = new Future();
//	//					res.addResultListener(new IResultListener()
//	//					{
//	//						public void resultAvailable(Object result)
//	//						{
//	//							System.out.println("received result: "+result);
//	//						}
//	//						public void exceptionOccurred(Exception exception)
//	//						{
//	//							System.out.println("received exception: "+exception);
//	//						}
//	//					});
//						final String mycallid = SUtil.createUniqueId(compid.getName()+".term."+method.toString());
//						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
//						// Can be invoked directly, because internally redirects to agent thread.
//	//					System.out.println("sending terminate");
//						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
//							null, content, mycallid, to, res, nonfunc, sic);
//					}
//				}
//				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".term."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
//				
//				// Called from delegation listeners in RMS -> ignore if already terminated
//				public void setException(Exception exception)
//				{
//					super.setExceptionIfUndone(exception);
//				}
//			};
//		}
//		else if(SReflect.isSupertype(ITuple2Future.class, type))
//		{
//			future = new Tuple2Future();
//		}
//		else if(SReflect.isSupertype(IIntermediateFuture.class, type))
//		{
//			future = new IntermediateFuture();
//		}
//		else
//		{
//			future = new Future();
//		}
//		
////		FutureFunctionality func = new FutureFunctionality()
////		{
////			public IFuture<Void> terminate() 
////			{
////				Future res = new Future();
//////				res.addResultListener(new IResultListener()
//////				{
//////					public void resultAvailable(Object result)
//////					{
//////						System.out.println("received result: "+result);
//////					}
//////					public void exceptionOccurred(Exception exception)
//////					{
//////						System.out.println("received exception: "+exception);
//////					}
//////				});
////				final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
////				RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid);
////				// Can be invoked directly, because internally redirects to agent thread.
//////				System.out.println("sending terminate");
////				rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
////					content, mycallid, to, res);
////			
////				return IFuture.DONE;
////			}
////		};
////
////		future = FutureFunctionality.getDelegationFuture(type, func);
//		
//		Object ret = future;
//		
////		if(method.getName().indexOf("store")!=-1)
////			System.out.println("remote method invoc: "+method.getName());
//		
//		// Test if method is excluded.
//		if(pi.isExcluded(method))
//		{
//			Exception	ex	= new UnsupportedOperationException("Method is excluded from interface for remote invocations: "+method.getName());
//			ex.fillInStackTrace();
//			future.setException(ex);
//		}
//		else
//		{
//			// Test if method is constant and a cache value is available.
//			if(pr.getCache()!=null && !pi.isUncached(method) && !pi.isReplaced(method))
//			{
//				Class<?> rt = method.getReturnType();
//				Class<?>[] ar = method.getParameterTypes();
//				if(!rt.equals(void.class) && !(SReflect.isSupertype(IFuture.class, rt)) && ar.length==0)
//				{
//					Object res = pr.getCache().get(method.getName());
//					if(res instanceof Throwable && SReflect.isSupertype(Throwable.class, rt))
//					{
//						throw (Throwable)res;
//					}
//					else
//					{
//						return res;
//					}
//				}
//			}
//			
//			// Test if method has a replacement command.
//			IMethodReplacement	replacement	= pi.getMethodReplacement(method);
//			if(replacement!=null)
//			{
//				// Todo: super pointer for around-advice-like replacements.
//				return replacement.invoke(proxy, args);
//			}
//			
//			// Test if finalize is called.
//			if(finalize.equals(method))
//			{
//	//			System.out.println("Finalize called on: "+proxy);
//				rsms.component.scheduleStep(new IComponentStep<Void>()
//				{
//					@Classname("fin")
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						rsms.getRemoteReferenceModule().decProxyCount(pr.getRemoteReference());
//						return IFuture.DONE;
//					}
//				});
//				return null;
//			}
//			
////			System.out.println("timeout: "+to);
//			
//			// Call remote method using invocation command.	
////			System.out.println("call: "+callid+" "+method);
////			if("getServices".equals(method.getName()))
////				debugcallid	= callid;
//			
//			// non-func is in command to let stream handlers access the properties in RMI processing
//			final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
//				pr.getRemoteReference(), method, args, callid, IComponentIdentifier.LOCAL.get(), nonfunc);
//			
//			// Can be invoked directly, because internally redirects to agent thread.
////			System.out.println("invoke: "+method.getName());
////			if(method.getName().equals("getResult"))
////				System.out.println("sending invoke");
//			rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
//				null, content, callid, to, future, nonfunc, sic);
//			
//			// Provide alternative immediate future result, if method is asynchronous.
//			if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
//			{
////				System.out.println("Warning, void method call will be executed asynchronously: "
////					+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
//				future	= new Future(null);
//			}
//		}
//		
//		// Wait for future, if blocking method.
//		if(!IFuture.class.isAssignableFrom(method.getReturnType()))
//		{
////			Thread.dumpStack();
//			if(future.isDone())
//			{
//				ret = future.get(null);
//			}
//			else
//			{
//				System.out.println("Warning, blocking method call: "+method.getDeclaringClass()
//					+" "+method.getName()+" "+Thread.currentThread()+" "+pi);
//				ret = future.get(new ThreadSuspendable());
//			}
////			System.out.println("Resumed call: "+method.getName()+" "+ret);
//		}
//		
////		if(ret instanceof IFuture)
////		{
////			((IFuture<Object>)ret).addResultListener(new IResultListener<Object>()
////			{
////				public void resultAvailable(Object result)
////				{
////					notifyMethodListeners(false, proxy, method, args, callid);
////				}
////				
////				public void exceptionOccurred(Exception exception)
////				{
////					notifyMethodListeners(false, proxy, method, args, callid);
////				}
////			});
////		}
////		else
////		{
////			notifyMethodListeners(false, proxy, method, args, callid);
////		}
//	
//		return ret;
//	}
	
	/**
	 *  Create a return future of suitable type for a method call.
	 */
	protected Future<Object> createReturnFuture(final IComponentIdentifier compid, final String callid, 
		final Method method, final Class<?> type, final long to, final Map<String, Object> nonfunc, final ServiceInvocationContext sic)
	{
		Future<Object> future;
		
		if(SReflect.isSupertype(IPullSubscriptionIntermediateFuture.class, type))
		{
			future = new PullSubscriptionIntermediateDelegationFuture()
			{
				public void pullIntermediateResult() 
				{
					Future<Object> res = new Future<Object>();
					final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
					RemoteFuturePullCommand content = new RemoteFuturePullCommand(mycallid, callid);
					// Can be invoked directly, because internally redirects to agent thread.
//					System.out.println("sending terminate");
					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
						content, mycallid, to, res, nonfunc, sic);
				}
				
				public void terminate(Exception reason) 
				{
					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
					boolean	set	= setExceptionIfUndone(reason);
					
					// Send message to announce termination to remote
					if(set)
					{
						Future<Object> res = new Future<Object>();
	//					res.addResultListener(new IResultListener()
	//					{
	//						public void resultAvailable(Object result)
	//						{
	//							System.out.println("received result: "+result);
	//						}
	//						public void exceptionOccurred(Exception exception)
	//						{
	//							System.out.println("received exception: "+exception);
	//						}
	//					});
						final String mycallid = SUtil.createUniqueId(compid.getName()+".pullsub."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
							content, mycallid, to, res, nonfunc, sic);
					}
				}
				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".pullsub."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
			};
		}
		else if(SReflect.isSupertype(IPullIntermediateFuture.class, type))
		{
			future = new PullIntermediateDelegationFuture()
			{
				public void pullIntermediateResult() 
				{
					Future<Object> res = new Future<Object>();
					final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
					RemoteFuturePullCommand content = new RemoteFuturePullCommand(mycallid, callid);
					// Can be invoked directly, because internally redirects to agent thread.
//					System.out.println("sending terminate");
					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
						content, mycallid, to, res, nonfunc, sic);
				}
				
				public void terminate(Exception reason) 
				{
					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
					boolean	set	= setExceptionIfUndone(reason);
					
					// Send message to announce termination to remote
					if(set)
					{
						Future<Object> res = new Future<Object>();
	//					res.addResultListener(new IResultListener()
	//					{
	//						public void resultAvailable(Object result)
	//						{
	//							System.out.println("received result: "+result);
	//						}
	//						public void exceptionOccurred(Exception exception)
	//						{
	//							System.out.println("received exception: "+exception);
	//						}
	//					});
						final String mycallid = SUtil.createUniqueId(compid.getName()+".pull."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
							content, mycallid, to, res, nonfunc, sic);
					}
				}
				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".pull."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
			};
		}
		else if(SReflect.isSupertype(ISubscriptionIntermediateFuture.class, type))
		{
			future = new SubscriptionIntermediateDelegationFuture()
			{
				public void terminate(Exception reason) 
				{
					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
					boolean	set	= setExceptionIfUndone(reason);
					
					// Send message to announce termination to remote
					if(set)
					{
						Future res = new Future();
	//					res.addResultListener(new IResultListener()
	//					{
	//						public void resultAvailable(Object result)
	//						{
	//							System.out.println("received result: "+result);
	//						}
	//						public void exceptionOccurred(Exception exception)
	//						{
	//							System.out.println("received exception: "+exception);
	//						}
	//					});
						final String mycallid = SUtil.createUniqueId(compid.getName()+".sub."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
							content, mycallid, to, res, nonfunc, sic);
					}
				}
				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".sub."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
				
//				public void addResultListener(IResultListener listener) 
//				{
//					if(method.getName().indexOf("calculate")!=-1)
//						System.out.println("dfg");
//						
//					super.addResultListener(listener);
//				}
			};
		}
		else if(SReflect.isSupertype(ITerminableIntermediateFuture.class, type))
		{
			future = new TerminableIntermediateDelegationFuture()
			{
				public void terminate(Exception e) 
				{
					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
					boolean	set	= setExceptionIfUndone(e);
					
					// Send message to announce termination to remote
					if(set)
					{
						Future res = new Future();
	//					res.addResultListener(new IResultListener()
	//					{
	//						public void resultAvailable(Object result)
	//						{
	//							System.out.println("received result: "+result);
	//						}
	//						public void exceptionOccurred(Exception exception)
	//						{
	//							System.out.println("received exception: "+exception);
	//						}
	//					});
						final String mycallid = SUtil.createUniqueId(compid.getName()+".interm."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, e);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
							null, content, mycallid, to, res, nonfunc, sic);
					}
				}
				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".interm."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
			};
		}
		else if(SReflect.isSupertype(ITerminableFuture.class, type))
		{
			future = new TerminableDelegationFuture()
			{
				public void terminate(Exception reason) 
				{
					// Set exception for local state (as rms removes waiting call, cannot receive remote result any more)
					boolean set	= setExceptionIfUndone(reason);
					
					// Send message to announce termination to remote
					if(set)
					{
						Future res = new Future();
	//					res.addResultListener(new IResultListener()
	//					{
	//						public void resultAvailable(Object result)
	//						{
	//							System.out.println("received result: "+result);
	//						}
	//						public void exceptionOccurred(Exception exception)
	//						{
	//							System.out.println("received exception: "+exception);
	//						}
	//					});
						final String mycallid = SUtil.createUniqueId(compid.getName()+".term."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
							null, content, mycallid, to, res, nonfunc, sic);
					}
				}
				
//				public void sendBackwardCommand(Object info)
//				{
//					Future<Object> res = new Future<Object>();
//					final String mycallid = SUtil.createUniqueId(compid.getName()+".term."+method.toString());
//					RemoteFutureBackwardCommand content = new RemoteFutureBackwardCommand(mycallid, callid, info);
//					// Can be invoked directly, because internally redirects to agent thread.
////					System.out.println("sending backward cmd");
//					rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
//						content, mycallid, to, res, nonfunc, sic);
//				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
			};
		}
		else if(SReflect.isSupertype(ITuple2Future.class, type))
		{
			future = new Tuple2Future();
		}
		else if(SReflect.isSupertype(IIntermediateFuture.class, type))
		{
			future = new IntermediateFuture();
		}
		else
		{
			future = new Future();
		}
		
		return future;
	}
	
	/**
	 *  Determine return type of method.
	 */
	public Class<?> determineReturnType(Object proxy, final Method method, Object[] args)
	{
		Class<?> ret = null;
		
		// Hack! special case schedule step, which may return different
		// kinds of result types according to the concrete step used.
		if(schedulestep.equals(method))
		{
			try
			{
				Method m = args[0].getClass().getMethod("execute", new Class[]{IInternalAccess.class});
				ret = m.getReturnType();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			ret = method.getReturnType();
		}
		
		return ret;
	}
	
	/**
	 *  Get the pr.
	 *  @return the pr.
	 */
	public ProxyReference getProxyReference()
	{
		return pr;
	}
	
	/**
	 *  Check if a switch call should be done.
	 *  @return True, if switch should be done.
	 */
	public boolean isSwitchCall()
	{
		return false;
	}
	
	/**
	 *  Get the target resolver.
	 *  @return The target resolver.
	 */
	protected ITargetResolver getTargetResolver()
	{
		if(tr==null)
		{
			Class<ITargetResolver> cl = pr.getProxyInfo().getTargetResolverClazz();
			if(cl!=null)
			{
				try
				{
					tr = cl.newInstance();
				}
				catch(RuntimeException e)
				{
					throw e;
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		return tr;
	}
	
	/**
	 *  Test equality.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = obj instanceof RemoteMethodInvocationHandler;
		if(ret)
		{
			ret = pr.getRemoteReference().equals(((RemoteMethodInvocationHandler)obj)
				.getProxyReference().getRemoteReference());
		}
		return ret;
	}
	
	/**
	 *  Hash code.
	 */
	public int hashCode()
	{
		return 31 + pr.getRemoteReference().hashCode();
	}
}

