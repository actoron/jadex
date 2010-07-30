package jadex.standalone.service;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.RemoteMethodInvocationInfo;
import jadex.bridge.RemoteMethodResultInfo;
import jadex.bridge.RemoteServiceSearchInvocationInfo;
import jadex.bridge.RemoteServiceSearchResultInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicService;
import jadex.service.IService;
import jadex.service.IServiceIdentifier;
import jadex.service.SServiceProvider;
import jadex.service.TypeResultSelector;
import jadex.service.library.ILibraryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
		super(BasicService.createServiceIdentifier(component.getServiceProvider().getId(), RemoteServiceManagementService.class));

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
		cms.getExternalAccess((IComponentIdentifier)rmii.getServiceIdentifier().getProviderId()).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				
				// fetch service on target component 
				SServiceProvider.getDeclaredService(exta.getServiceProvider(), rmii.getServiceIdentifier())
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
	 *  @param rms The remote management service where the original service lives.
	 *  @param sid The service identifier.
	 *  @return The service proxy.
	 */
	public Object getProxy(IComponentIdentifier rms, IServiceIdentifier sid, Class service)
	{
		return Proxy.newProxyInstance(libservice.getClassLoader(), new Class[]{service}, 
			new RSMInvocationHandler(rms, sid, service));
	}
	
	/**
	 *  Invoke a method on a remote component.
	 *  (called from arbitrary components)
	 *  @param rms The remote management service where the original service lives.
	 *  @return The service proxy.
	 */
	public IFuture getProxy(IComponentIdentifier rms, Object providerid, Class service)
	{
		final Future ret = new Future();
		
		String convid = SUtil.createUniqueId(component.getComponentIdentifier().getLocalName());
		RemoteServiceSearchInvocationInfo content = new RemoteServiceSearchInvocationInfo(providerid, 
			SServiceProvider.sequentialmanager, SServiceProvider.abortdecider, new TypeResultSelector(service, true, true));

		waitingcalls.put(convid, ret);
		
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rms});
		msg.put(SFipa.CONVERSATION_ID, convid);
		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		msg.put(SFipa.CONTENT, content);
		msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());
		
		return ret;
	}

	/**
	 *  Called when a method invocation result has been retrived.
	 *  (called only from own component)
	 */
	public void remoteSearchResultReceived(RemoteServiceSearchResultInfo result, String convid)
	{
		Future future = (Future)waitingcalls.get(convid);
		if(result.getException()!=null)
		{
			future.setException(result.getException());
		}
		else
		{
			if(result.getResult() instanceof Collection)
			{
				List ret = new ArrayList();
				for(Iterator it=((Collection)result.getResult()).iterator(); it.hasNext(); )
				{
					ProxyInfo pi = (ProxyInfo)it.next();
					ret.add(getProxy(pi.getRms(), pi.getServiceIdentifier(), pi.getService()));
				}
				future.setResult(ret);
			}
			else if(result.getResult() instanceof ProxyInfo)
			{
				ProxyInfo pi = (ProxyInfo)result.getResult();
				future.setResult(getProxy(pi.getRms(), pi.getServiceIdentifier(), pi.getService()));
			}
		}
	}
	
	/**
	 *  Called when component receives message with remote search request.
	 *  (called only from own component)
	 */
	public void remoteSearchReceived(final IComponentIdentifier rms, 
		final RemoteServiceSearchInvocationInfo rssii, final String convid)	
	{
		final Future ret = new Future();
		
		// create result msg
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, component.getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rms});
		msg.put(SFipa.CONVERSATION_ID, convid);
		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		
		// fetch component via provider/component id
		IComponentIdentifier comp = rssii.getProviderId()!=null? (IComponentIdentifier)rssii.getProviderId(): component.getComponentIdentifier();
		cms.getExternalAccess(comp).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				
				// start serach on target component
				exta.getServiceProvider().getServices(rssii.getSearchManager(), rssii.getVisitDecider(), 
					rssii.getResultSelector(), new ArrayList()).addResultListener(new DelegationResultListener(ret));
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
				// Create proxy info(s) for service(s)
				Object content;
				if(result instanceof Collection)
				{
					List res = new ArrayList();
					for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
					{
						Object[] tmp = (Object[])it.next();
						Class sertype = (Class)tmp[0]; 
						IService ser = (IService)tmp[1];
						ProxyInfo pi = new ProxyInfo(component.getComponentIdentifier(), ser.getServiceIdentifier(), sertype);
						res.add(pi);
					}
					content = res;
				}
				else //if(result instanceof Object[])
				{
					Object[] tmp = (Object[])result;
					Class sertype = (Class)tmp[0]; 
					IService ser = (IService)tmp[1];
					content = new ProxyInfo(component.getComponentIdentifier(), ser.getServiceIdentifier(), sertype);
					
				}
				msg.put(SFipa.CONTENT, new RemoteServiceSearchResultInfo(content, null));
				msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				msg.put(SFipa.CONTENT, new RemoteServiceSearchResultInfo(null, exception));
				msgservice.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE, component.getComponentIdentifier(), libservice.getClassLoader());
			}
		});
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
		
		/** The service identifier. */
		protected IServiceIdentifier sid;
		
		/** The service interface. */
		protected Class service;
		
		//-------- constructors --------
		
		/**
		 *  Create a new invocation handler.
		 */
		public RSMInvocationHandler(IComponentIdentifier rms, IServiceIdentifier sid, Class service)
		{
			this.rms = rms;
			this.sid = sid;
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

			RemoteMethodInvocationInfo mii = new RemoteMethodInvocationInfo(sid, method.getName(), 
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

	/**
	 *  Get the component.
	 *  @return the component.
	 */
	public IExternalAccess getComponent()
	{
		return component;
	}

	/**
	 *  Get the cms.
	 *  @return the cms.
	 */
	public IComponentManagementService getComponentManagementService()
	{
		return cms;
	}
}

/**
 * 
 * /
class RemoteMethodInvocationCommand
{
	protected String convid;
	
	protected RemoteMethodInvocationInfo rmii;
	
	protected IComponentIdentifier rms;
	
	/**
	 * 
	 * /
	public RemoteMethodInvocationCommand(String convid, RemoteMethodInvocationInfo rmii, IComponentIdentifier rms)
	{
		this.convid = convid;
		this.rmii = rmii;
		this.rms = rms;
	}
	
	/**
	 * 
	 * /
	public IFuture execute(RemoteServiceManagementService lrms)
	{
		final Future ret = new Future();
		
		// create result msg
		final Map msg = new HashMap();
		msg.put(SFipa.SENDER, lrms.getComponent().getComponentIdentifier());
		msg.put(SFipa.RECEIVERS, new IComponentIdentifier[]{rms});
		msg.put(SFipa.CONVERSATION_ID, convid);
		msg.put(SFipa.LANGUAGE, SFipa.JADEX_XML);
		
		// fetch component via target component id
		lrms.getComponentManagementService().getExternalAccess((IComponentIdentifier)rmii.getServiceIdentifier().getProviderId()).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				
				// fetch service on target component 
				SServiceProvider.getDeclaredService(exta.getServiceProvider(), rmii.getServiceIdentifier())
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
}*/

