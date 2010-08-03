package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceIdentifier;
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
	protected IExternalAccess component;
	
	/** The remote rms component identifier. */
	protected IComponentIdentifier rms;
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The service interface. */
	protected Class service;
	
	/** The waiting calls. */
	protected Map waitingcalls;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public RemoteMethodInvocationHandler(IExternalAccess component, IComponentIdentifier rms, 
		IServiceIdentifier sid, Class service, Map waitingcalls)
	{
		this.component = component;
		this.rms = rms;
		this.sid = sid;
		this.service = service;
		this.waitingcalls = waitingcalls;
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke a method.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		final Future future = new Future();
		Object ret = future;

		final IComponentIdentifier compid = component.getComponentIdentifier();
		String callid = SUtil.createUniqueId(compid.getLocalName());
		waitingcalls.put(callid, future);
		
		RemoteMethodInvocationCommand content = new RemoteMethodInvocationCommand(sid, method.getName(), 
			method.getParameterTypes(), args, callid, compid);
		
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rms});
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
						ms.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, compid, ls.getClassLoader());
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
		
		if(!method.getReturnType().isAssignableFrom(IFuture.class))
		{
			System.out.println("Warning, blocking method call: "+method.getDeclaringClass()+" "+method.getName());
//			Thread.dumpStack();
			ret = future.get(new ThreadSuspendable());
//			System.out.println("Resumed call: "+method.getName()+" "+ret);
		}
	
		return ret;
	}
}

