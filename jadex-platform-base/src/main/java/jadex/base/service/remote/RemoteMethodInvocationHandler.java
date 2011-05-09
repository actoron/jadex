package jadex.base.service.remote;

import jadex.base.service.remote.commands.RemoteMethodInvocationCommand;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.annotation.XMLClassname;

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
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		final Future future = IIntermediateFuture.class.isAssignableFrom(method.getReturnType())
			? new IntermediateFuture()
			: new Future();
		Object ret = future;
		
		ProxyInfo pi = pr.getProxyInfo();
//		if(method.getName().indexOf("calc")!=-1)
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
				if(!rt.equals(void.class) && !(rt.isAssignableFrom(IFuture.class)) && ar.length==0)
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
				rsms.component.scheduleStep(new IComponentStep()
				{
					@XMLClassname("fin")
					public Object execute(IInternalAccess ia)
					{
						rsms.getRemoteReferenceModule().decProxyCount(pr.getRemoteReference());
						return null;
					}
				});
				return null;
			}
			
			// Get method timeout
			long	to	= pi.getMethodTimeout(method);
//			System.out.println("timeout: "+to);
			
			// Call remote method using invocation command.	
			final IComponentIdentifier compid = rsms.getRMSComponentIdentifier();
			final String callid = SUtil.createUniqueId(compid.getLocalName());
//			System.out.println("call: "+callid+" "+method);
//			if("getServices".equals(method.getName()))
//				debugcallid	= callid;
			final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
				pr.getRemoteReference(), method.getName(), method.getParameterTypes(), args, callid);
			
			// Can be invoked directly, because internally redirects to agent thread.
			rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
				content, callid, to, future);
			
			// Set future result immediately, if method is asynchronous.
			if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
			{
//				System.out.println("Warning, void method call will be executed asynchronously: "
//					+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
				future.setResultIfUndone(null);
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

