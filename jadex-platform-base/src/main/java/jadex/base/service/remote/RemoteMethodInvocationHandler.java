package jadex.base.service.remote;

import jadex.base.service.remote.commands.RemoteFutureTerminationCommand;
import jadex.base.service.remote.commands.RemoteMethodInvocationCommand;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
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

/**
 *  Class that implements the Java proxy InvocationHandler, which
 *  is called when a method on a proxy is called.
 */
public class RemoteMethodInvocationHandler implements InvocationHandler
{
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
		// Get method timeout
		final long to = pi.getMethodTimeout(method);
		
		Future future;
		Class type = method.getReturnType();

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
							content, mycallid, to, res);
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
							null, content, mycallid, to, res);
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
							null, content, mycallid, to, res);
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
				Class rt = method.getReturnType();
				Class[] ar = method.getParameterTypes();
				if(!rt.equals(void.class) && !(SReflect.isSupertype(IFuture.class, rt)) && ar.length==0)
				{
					return pr.getCache().get(method.getName());
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
			
			final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
				pr.getRemoteReference(), method, args, callid, IComponentIdentifier.LOCAL.get());
			
			// Can be invoked directly, because internally redirects to agent thread.
//			System.out.println("invoke: "+method.getName());
//			if(method.getName().equals("getResult"))
//				System.out.println("sending invoke");
			rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
				null, content, callid, to, future);
			
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

