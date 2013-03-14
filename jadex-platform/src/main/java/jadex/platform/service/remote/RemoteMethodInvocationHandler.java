package jadex.platform.service.remote;

import jadex.base.service.remote.IMethodReplacement;
import jadex.base.service.remote.ProxyInfo;
import jadex.base.service.remote.ProxyReference;
import jadex.base.service.remote.commands.RemoteFutureTerminationCommand;
import jadex.base.service.remote.commands.RemoteMethodInvocationCommand;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.SecureTransmission;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.annotations.Classname;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *  Class that implements the Java proxy InvocationHandler, which
 *  is called when a method on a proxy is called.
 */
public class RemoteMethodInvocationHandler implements InvocationHandler
{
	protected static Method schedulestep;
	
	protected static Method finalize;
	
	static
	{
		try
		{
			finalize = IFinalize.class.getMethod("finalize", new Class[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			schedulestep = IExternalAccess.class.getMethod("scheduleStep", new Class[]{IComponentStep.class});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//-------- attributes --------
	
	/** The remote service management service. */
	protected RemoteServiceManagementService rsms;

	/** The proxy reference. */
	protected ProxyReference pr;
		
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
	public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable
	{
		final IComponentIdentifier compid = rsms.getRMSComponentIdentifier();
		final String callid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
	
		ProxyInfo pi = pr.getProxyInfo();
		
		// Get the current service invocation 
//		ServiceCall invoc = ServiceCall.getCurrentInvocation();
		ServiceCall invoc = ServiceCall.getInvocation();
		
		// Get method timeout
		final long to = invoc!=null && invoc.getTimeout()!=-1? invoc.getTimeout(): pi.getMethodTimeout(method);
		// The reatime property is not necessary, as currently message are sent with realtime timeouts always  
		
//		if(method.getName().indexOf("schedule")!=-1)
//		System.out.println("step: "+method.getName()+", "+invoc);
		
		// Get the secure transmission
		boolean sec = pi.isSecure(method);
		
		Map<String, Object> nf = invoc!=null? invoc.getProperties(): new HashMap<String, Object>();
		if(sec)
		{
			nf.put(SecureTransmission.SECURE_TRANSMISSION, sec? Boolean.TRUE: Boolean.FALSE);
		}
		nf.put(Timeout.TIMEOUT, new Long(to));
		final Map<String, Object> nonfunc = nf; 
		
		Future future;
		Class type = determineReturnType(proxy, method, args);
//		Class type = method.getReturnType();

		if(SReflect.isSupertype(ISubscriptionIntermediateFuture.class, type))
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
						final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), null,
							content, mycallid, to, res, nonfunc);
					}
				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
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
						final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, e);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
							null, content, mycallid, to, res, nonfunc);
					}
				}
				
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
						final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
						RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid, reason);
						// Can be invoked directly, because internally redirects to agent thread.
	//					System.out.println("sending terminate");
						rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
							null, content, mycallid, to, res, nonfunc);
					}
				}
				
				// Called from delegation listeners in RMS -> ignore if already terminated
				public void setException(Exception exception)
				{
					super.setExceptionIfUndone(exception);
				}
			};
		}
		else if(SReflect.isSupertype(IIntermediateFuture.class, type))
		{
			future = new IntermediateFuture();
		}
		else
		{
			future = new Future();
		}
		
//		FutureFunctionality func = new FutureFunctionality()
//		{
//			public IFuture<Void> terminate() 
//			{
//				Future res = new Future();
////				res.addResultListener(new IResultListener()
////				{
////					public void resultAvailable(Object result)
////					{
////						System.out.println("received result: "+result);
////					}
////					public void exceptionOccurred(Exception exception)
////					{
////						System.out.println("received exception: "+exception);
////					}
////				});
//				final String mycallid = SUtil.createUniqueId(compid.getLocalName()+"."+method.toString());
//				RemoteFutureTerminationCommand content = new RemoteFutureTerminationCommand(mycallid, callid);
//				// Can be invoked directly, because internally redirects to agent thread.
////				System.out.println("sending terminate");
//				rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
//					content, mycallid, to, res);
//			
//				return IFuture.DONE;
//			}
//		};
//
//		future = FutureFunctionality.getDelegationFuture(type, func);
		
		Object ret = future;
		
//		if(method.getName().indexOf("store")!=-1)
//			System.out.println("remote method invoc: "+method.getName());
		
		// Test if method is excluded.
		if(pi.isExcluded(method))
		{
			Exception	ex	= new UnsupportedOperationException("Method is excluded from interface for remote invocations: "+method.getName());
			ex.fillInStackTrace();
			future.setException(ex);
		}
		else
		{
			// Test if method is constant and a cache value is available.
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
			
//			System.out.println("timeout: "+to);
			
			// Call remote method using invocation command.	
//			System.out.println("call: "+callid+" "+method);
//			if("getServices".equals(method.getName()))
//				debugcallid	= callid;
			
			// non-func is in command to let stream handlers access the properties in RMI processing
			final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
				pr.getRemoteReference(), method, args, callid, IComponentIdentifier.LOCAL.get(), nonfunc);
			
			// Can be invoked directly, because internally redirects to agent thread.
//			System.out.println("invoke: "+method.getName());
//			if(method.getName().equals("getResult"))
//				System.out.println("sending invoke");
			rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
				null, content, callid, to, future, nonfunc);
			
			// Provide alternative immediate future result, if method is asynchronous.
			if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
			{
//				System.out.println("Warning, void method call will be executed asynchronously: "
//					+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
				future	= new Future(null);
			}
		}
		
		// Wait for future, if blocking method.
		if(!IFuture.class.isAssignableFrom(method.getReturnType()))
		{
//			Thread.dumpStack();
			if(future.isDone())
			{
				ret = future.get(null);
			}
			else
			{
				System.out.println("Warning, blocking method call: "+method.getDeclaringClass()
					+" "+method.getName()+" "+Thread.currentThread()+" "+pi);
				ret = future.get(new ThreadSuspendable());
			}
//			System.out.println("Resumed call: "+method.getName()+" "+ret);
		}
	
		return ret;
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

