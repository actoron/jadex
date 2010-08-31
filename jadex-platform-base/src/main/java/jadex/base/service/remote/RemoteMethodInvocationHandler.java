package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.micro.IMicroExternalAccess;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  Class that implements the Java proxy InvocationHandler, which
 *  is called when a method on a proxy is called.
 */
public class RemoteMethodInvocationHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The host component. */
	protected IMicroExternalAccess component;
	
	/** The proxy info. */
	protected ProxyInfo pi;
	
	/** The waiting calls. */
	protected Map waitingcalls;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public RemoteMethodInvocationHandler(IMicroExternalAccess component, ProxyInfo pi, Map waitingcalls)
	{
		this.component = component;
		this.pi = pi;
		this.waitingcalls = waitingcalls;
//		System.out.println("handler: "+pi.getServiceIdentifier().getServiceType()+" "+pi.getCache());
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke a method.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
//		System.out.println("remote method invoc: "+method.getName());
		
		// Test if method is excluded.
		if(pi.isExcluded(method))
			throw new UnsupportedOperationException("Method is excluded from interface for remote invocations.");
		
		// Test if method is constant and a cache value is available.
		if(pi.getCache()!=null && !pi.isUncached(method) && !pi.isReplaced(method))
		{
			Class rt = method.getReturnType();
			Class[] ar = method.getParameterTypes();
			if(!rt.equals(void.class) && !(rt.isAssignableFrom(IFuture.class)) && ar.length==0)
			{
				return pi.getCache().get(method.getName());
			}
		}
		
		// Test if method has a replacement command.
		IMethodReplacement	replacement	= pi.getMethodReplacement(method);
		if(replacement!=null)
		{
			// Todo: super pointer for around-advice-like replacements.
			return replacement.invoke(proxy, args);
		}
		
		// Call remote method otherwise.
		final Future future = new Future();
		Object ret = future;
		
		final IComponentIdentifier compid = component.getComponentIdentifier();
		final String callid = SUtil.createUniqueId(compid.getLocalName());
		
		RemoteMethodInvocationCommand content;
		if(pi.getServiceIdentifier()!=null)
		{
			content = new RemoteMethodInvocationCommand(pi.getServiceIdentifier(), method.getName(), 
				method.getParameterTypes(), args, callid, compid);
		}
		else
		{
			content = new RemoteMethodInvocationCommand(pi.getComponentIdentifier(), method.getName(), 
				method.getParameterTypes(), args, callid, compid);
		}
		
		RemoteServiceManagementService.sendMessage(component, pi.getRemoteManagementServiceIdentifier(), content, callid, -1, waitingcalls, future);
		
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

