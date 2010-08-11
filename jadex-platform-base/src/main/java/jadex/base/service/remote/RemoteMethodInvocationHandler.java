package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IMessageService;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;

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
		// Test if method is excluded.
		if(pi.isExcluded(method))
			throw new UnsupportedOperationException("Method is excluded from interface for remote invocations.");
		
		// Test if method is constant and a cache value is available.
		if(pi.getCache()!=null)
		{
			Class rt = method.getReturnType();
			Class[] ar = method.getParameterTypes();
			if(!rt.equals(void.class) && !(rt.isAssignableFrom(IFuture.class)) && ar.length==0)
			{
				return pi.getCache().get(method.getName());
			}
		}
		
		// Call remote method otherwise.
		final Future future = new Future();
		Object ret = future;
		
		final IComponentIdentifier compid = component.getComponentIdentifier();
		final String callid = SUtil.createUniqueId(compid.getLocalName());
		waitingcalls.put(callid, future);
		
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
		
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{pi.getRemoteManagementServiceIdentifier()});
		msg.put(SFipa.CONTENT, content);
		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		msg.put(SFipa.CONVERSATION_ID, callid);
		
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
						IMessageService ms = (IMessageService)result;
						ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, compid, ls.getClassLoader())
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// ok message could be sent.
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								// message could not be sent -> fail immediately.
								System.out.println("Callee could not be reached: "+exception);
								waitingcalls.remove(compid);
								future.setException(exception);
							}
						});
						component.scheduleStep(new ICommand() 
						{
							public void execute(Object args) 
							{
								ProxyAgent pa = (ProxyAgent)args;
								pa.waitFor(getTimeout(), new ICommand() 
								{
									public void execute(Object args) 
									{
										waitingcalls.remove(compid);
										future.setException(new RuntimeException("No reply received and timeout occurred: "+callid+" "+msg));
									}
								});
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						waitingcalls.remove(compid);
						future.setException(exception);
					}
				}));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				waitingcalls.remove(compid);
				future.setException(exception);
			}
		}));
		
		if(method.getReturnType().equals(void.class) && !pi.isSynchronous(method))
		{
//			System.out.println("Warning, void method call will be executed asynchronously: "
//				+method.getDeclaringClass()+" "+method.getName()+" "+Thread.currentThread());
			future.setResult(null);
		}
		else if(!method.getReturnType().isAssignableFrom(IFuture.class))
		{
			Thread.dumpStack();
			System.out.println("Warning, blocking method call: "+method.getDeclaringClass()
				+" "+method.getName()+" "+Thread.currentThread()+" "+pi.getServiceIdentifier().getServiceType());
			ret = future.get(new ThreadSuspendable());
//			System.out.println("Resumed call: "+method.getName()+" "+ret);
		}
	
		return ret;
	}

	/**
	 *  Get the message timeout.
	 *  @return The timeout.
	 */
	protected long getTimeout()
	{
		// todo: make customizable from proxy info
		return 10000;
	}
}

