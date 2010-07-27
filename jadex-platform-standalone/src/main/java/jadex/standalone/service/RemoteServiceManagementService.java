package jadex.standalone.service;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
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
 * 
 */
public class RemoteServiceManagementService extends BasicService implements IRemoteServiceManagementService
{
	/** The component. */
	protected IExternalAccess component;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The service service. */
	protected IMessageService msgservice;
	
	/** The component management service. */
	protected IComponentManagementService cms;
	
	/**
	 * 
	 */
	public RemoteServiceManagementService(IExternalAccess component)
	{
		this.component = component;
	}
	
	
	/**
	 *  Invoke a method on a remote component.
	 * /
	public IFuture invokeServiceMethod(IComponentIdentifier cid, Class service, String methodname, Object[] args)
	{
	}*/
	
	/**
	 *  Called when component receives message with remote method invocation request.
	 */
	public void invocationReceived(final IComponentIdentifier sendercid, final IComponentIdentifier targetcid, final Class service, 
		final String methodname, final Class[] argtypes, final Object[] args, final String convid)
	{
		// fetch component
		// fetch service on comp
		// invoke method and get results
		// send result message to sender
	
		cms.getExternalAccess(targetcid).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				SServiceProvider.getDeclaredService(exta.getServiceProvider(), service).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						try
						{
							Method m = result.getClass().getMethod(methodname, argtypes);
							Object res = m.invoke(result, args);
							
							Map msg = new HashMap();
							msg.put(SFipa.SENDER, targetcid);
							msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{sendercid});
							msg.put(SFipa.CONTENT, "test");
							msg.put(SFipa.CONVERSATION_ID, convid);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
			}
		});
	}
	
	/**
	 *  Invoke a method on a remote component.
	 */
	public Object getProxy(IComponentIdentifier targetcid, Class service)
	{
		 return Proxy.newProxyInstance(libservice.getClassLoader(), new Class[]{service}, new RSMInvocationHandler(targetcid, service));
	}

	/**
	 *  Start the service.
	 */
	public IFuture startService()
	{
		final Future ret = new Future();
		
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
		
		return ret;
	}
	
	/**
	 * 
	 */
	public class RSMInvocationHandler implements InvocationHandler
	{
		/** The component identifier. */
		protected IComponentIdentifier targetcid;
		
		/** The service interface. */
		protected Class service;
		
		/**
		 *  Create a new invocation handler.
		 */
		public RSMInvocationHandler(IComponentIdentifier targetcid, Class service)
		{
			this.targetcid = targetcid;
			this.service = service;
		}
		
		/**
		 *  Invoke a method.
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			Future ret = new Future();
			
			String convid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
			Map msg = new HashMap();
			msg.put(SFipa.SENDER, component.getComponentIdentifier());
			msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{targetcid});
			msg.put(SFipa.CONTENT, "test");
			msg.put(SFipa.CONVERSATION_ID, convid);
			
			msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());
		
			return ret;
		}
	}
}


