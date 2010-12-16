package jadex.base.service.remote;

import jadex.base.service.remote.commands.RemoteMethodInvocationCommand;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;

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
	
	/**
	 *  Invoke a method.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		ProxyInfo pi = pr.getProxyInfo();
//		System.out.println("remote method invoc: "+method.getName());
		
		// Test if method is excluded.
		if(pi.isExcluded(method))
			throw new UnsupportedOperationException("Method is excluded from interface for remote invocations: "+method.getName());
		
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
				public static final String XML_CLASSNAME = "fin"; 
				public Object execute(IInternalAccess ia)
				{
					rsms.getRemoteReferenceModule().decProxyCount(pr.getRemoteReference());
					return null;
				}
			});
			return null;
		}
		
		// Call remote method otherwise.
		final Future future = new Future();
		Object ret = future;
		
		final IComponentIdentifier compid = rsms.getRMSComponentIdentifier();
		final String callid = SUtil.createUniqueId(compid.getLocalName());
		
		final RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(
			pr.getRemoteReference(), method.getName(), method.getParameterTypes(), args, callid);
		
		// Can be invoked directly, because uses internally redirects to agent thread.
		rsms.sendMessage(pr.getRemoteReference().getRemoteManagementServiceIdentifier(), 
			content, callid, -1, future);
		
		if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
		{
//			System.out.println("Warning, void method call will be executed asynchronously: "
//				+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
			future.setResult(null);
		}
		else if(!IFuture.class.isAssignableFrom(method.getReturnType()))
		{
//			Thread.dumpStack();
			System.out.println("Warning, blocking method call: "+method.getDeclaringClass()
				+" "+method.getName()+" "+Thread.currentThread()+" "+pi);
			ret = future.get(new ThreadSuspendable());
//			System.out.println("Resumed call: "+method.getName()+" "+ret);
		}
	
		return ret;
	}

	
}

