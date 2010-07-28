package jadex.standalone.service;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.RemoteMethodInvocationInfo;
import jadex.bridge.RemoteMethodResultInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicService;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *  The remote service management service is responsible for 
 *  handling remote service invocations (similar to RMI).
 */
public class RemoteServiceManagementService extends BasicService implements IRemoteServiceManagementService
{
	//-------- attributes --------
	
	/** The component. */
	protected IExternalAccess component;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The service service. */
	protected IMessageService msgservice;
	
	/** The component management service. */
	protected IComponentManagementService cms;
	
	/** The waiting futures. */
	protected Map waitingcalls;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote service management service.
	 */
	public RemoteServiceManagementService(IExternalAccess component)
	{
		this.component = component;
		this.waitingcalls = new HashMap();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a method invocation result has been retrived.
	 *  (called only from own component)
	 */
	public void remoteResultReceived(RemoteMethodResultInfo result, String convid)
	{
		Future future = (Future)waitingcalls.get(convid);
		if(result.getException()!=null)
		{
			future.setException(result.getException());
		}
		else
		{
			future.setResult(result.getResult());
		}
	}
	
	/**
	 *  Called when component receives message with remote method invocation request.
	 *  (called only from own component)
	 */
	public void remoteInvocationReceived(final IComponentIdentifier rms, 
		final RemoteMethodInvocationInfo rmii, final String convid)	
	{
		final Future ret = new Future();
		
		// create result msg
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rms});
		msg.put(SFipa.CONVERSATION_ID, convid);
		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		
		// fetch component via target component id
		cms.getExternalAccess(rmii.getTarget()).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				
				// fetch service on target component 
				// todo: via id
				SServiceProvider.getDeclaredService(exta.getServiceProvider(), rmii.getService())
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						try
						{
							// fetch method on service and invoke method
							Method m = result.getClass().getMethod(rmii.getMethodName(), rmii.getParameterTypes());
							Object res = m.invoke(result, rmii.getParameterValues());
							
							if(res instanceof IFuture)
							{
								((IFuture)res).addResultListener(new DelegationResultListener(ret));
							}
							else
							{
								ret.setResult(res);
							}
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);	
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);	
			}
		});
		
		ret.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				msg.put(SFipa.CONTENT, new RemoteMethodResultInfo(result, null));
				msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				msg.put(SFipa.CONTENT, new RemoteMethodResultInfo(null, exception));
				msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());
			}
		});
	}
	
	/**
	 *  Invoke a method on a remote component.
	 *  (called from arbitrary components)
	 */
	public Object getProxy(IComponentIdentifier rms, IComponentIdentifier target, Class service)
	{
		return Proxy.newProxyInstance(libservice.getClassLoader(), new Class[]{service}, 
			new RSMInvocationHandler(rms, target, service));
	}

	/**
	 *  Start the service.
	 */
	public IFuture startService()
	{
		final Future ret = new Future();
		
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				cms = (IComponentManagementService)result;
			
				SServiceProvider.getService(component.getServiceProvider(), ILibraryService.class)
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						libservice = (ILibraryService)result;
						SServiceProvider.getService(component.getServiceProvider(), IMessageService.class)
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								msgservice = (IMessageService)result;
						
								RemoteServiceManagementService.super.startService()
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Class that implements the Java proxy InvocationHandler, which
	 *  is called when a method on a proxy is called.
	 */
	public class RSMInvocationHandler implements InvocationHandler
	{
		//-------- attributes --------
		
		/** The remote rms component identifier. */
		protected IComponentIdentifier rms;
		
		/** The target component identifier. */
		protected IComponentIdentifier target;
		
		/** The service interface. */
		protected Class service;
		
		//-------- constructors --------
		
		/**
		 *  Create a new invocation handler.
		 */
		public RSMInvocationHandler(IComponentIdentifier rms, IComponentIdentifier target, Class service)
		{
			this.rms = rms;
			this.target = target;
			this.service = service;
		}
		
		//-------- methods --------
		
		/**
		 *  Invoke a method.
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			Future future = new Future();
			Object ret = future;

			RemoteMethodInvocationInfo mii = new RemoteMethodInvocationInfo(target, service, method.getName(), 
				method.getParameterTypes(), args);
			String convid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
			waitingcalls.put(convid, future);
			
			Map msg = new HashMap();
			msg.put(SFipa.SENDER, component.getComponentIdentifier());
			msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rms});
			msg.put(SFipa.CONTENT, mii);
			msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
			msg.put(SFipa.CONVERSATION_ID, convid);
			msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());

			if(!method.getReturnType().isAssignableFrom(IFuture.class))
			{
				System.out.println("Warning, blocking method call: "+method.getName());
				ret = future.get(new ThreadSuspendable());
//				System.out.println("Resumed call: "+method.getName()+" "+ret);
			}
		
			return ret;
		}
	}
}


