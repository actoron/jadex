package jadex.bridge.service.component.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Logger;

import jadex.bridge.ComponentNotFoundException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

/**
 *  Interceptor for realizing intelligent proxies. These proxies
 *  are used e.g. by a global service pool to dynamically redirect service calls.
 */ 
public class IntelligentProxyInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The target resolver. */
	protected ITargetResolver tr;
	
	/** The worker timeout. */
	protected long wto;
	
	protected final static ITargetResolver NULL = new ITargetResolver() 
	{
		public IFuture<IService> determineTarget(IServiceIdentifier sid, IExternalAccess agent, IServiceIdentifier broken) 
		{
			return null;
		}
	};
		
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public IntelligentProxyInterceptor(IExternalAccess ea, IServiceIdentifier sid)//, long wto)
	{
		this.ea = ea;
		this.sid = sid;
//		this.wto = wto;
	}
	
	//-------- methods --------

	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
//		if(sic.getMethod().getName().indexOf("methodA")!=-1)
//			System.out.println("methodA");
		
		ITargetResolver tr = getTargetResolver(sic);
		if(tr!=null && isRedirectable(sic))
		{
//			System.out.println("redirecting call: "+sic.getMethod());
			invoke(null, sic, 3, 0).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
		}
					
		return ret;
	}

	/**
	 * 
	 */
	protected IFuture<Void> invoke(final IServiceIdentifier broken, final ServiceInvocationContext sic, final int maxretries, final int cnt)
	{
		final Future<Void> ret = new Future<Void>();
		
//		System.out.println("Intelligent proxy called: "+broken+" "+cnt);
		tr.determineTarget(sid, ea, broken)
			.addResultListener(new ExceptionDelegationResultListener<IService, Void>(ret) 
		{
			public void customResultAvailable(final IService ser) 
			{
//				System.out.println("invoking on: "+ser.getServiceIdentifier()+" "+cnt);
				try
				{
					final Object res = sic.getMethod().invoke(ser, sic.getArgumentArray());
					
					if(res instanceof IIntermediateFuture)
					{
						IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
						{
							boolean done;
							public void intermediateResultAvailable(Object result)
							{
								proceed();
//								System.out.println("iires: "+result);
							}
							
							public void finished()
							{
								proceed();
							}
							
							public void resultAvailable(Collection<Object> result) 
							{
								proceed();
							}
							
							public void exceptionOccurred(Exception exception) 
							{
								exception.printStackTrace();
								if(exception instanceof ComponentNotFoundException
									|| exception instanceof TimeoutException)
								{
									if(cnt<maxretries)
									{
										// Invoke again and rebind service
										System.out.println("Exception during service invocation, retrying: "+cnt+"/"+maxretries);
										invoke(ser.getServiceIdentifier(), sic, maxretries, cnt+1).addResultListener(new DelegationResultListener<Void>(ret));
									}
									else
									{
										proceed();
									}
								}
							}
							
							protected void proceed()
							{
								if(!done)
								{
									sic.setResult(res);
									ret.setResult(null);
									done = true;
								}
							}
						};
						
						if(res instanceof ISubscriptionIntermediateFuture)
						{
							((ISubscriptionIntermediateFuture)res).addQuietListener(lis);
						}
						else
						{
							((IIntermediateFuture)res).addResultListener(lis);
						}
					}
					else if(res instanceof IFuture)
					{
						((IFuture<Object>)res).addResultListener(new IResultListener<Object>() 
						{
							public void resultAvailable(Object result) 
							{
								sic.setResult(res);
								ret.setResult(null);
							}
							
							public void exceptionOccurred(Exception exception) 
							{
								if(exception instanceof ComponentNotFoundException
									|| exception instanceof TimeoutException)
								{
									if(cnt<maxretries)
									{
										// Invoke again and rebind service
										System.out.println("Exception during service invocation, retrying: "+cnt+"/"+maxretries);
										invoke(ser.getServiceIdentifier(), sic, maxretries, cnt+1).addResultListener(new DelegationResultListener<Void>(ret));
									}
									else
									{
										sic.setResult(res);
										ret.setResult(null);
									}
								}
							}
						});
					}
					else
					{
						sic.setResult(res);
						ret.setResult(null);
					}
				}
				catch(Exception e)
				{
					System.out.println("Intelli execption: "+e);
					Exception res = handleException(e);
					if(sic.getMethod().getReturnType().equals(IFuture.class))
					{
						sic.setResult(new Future(res));
					}
					else
					{
						throw SUtil.throwUnchecked(res);
					}
					ret.setResult(null);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected static Exception handleException(Exception e)
	{
		Throwable t	= e instanceof InvocationTargetException
			? ((InvocationTargetException)e).getTargetException() : e;
		Exception re = t instanceof Exception ? (Exception)t : new RuntimeException(t);
		return re;
	}
	
	/**
	 *  Get the target resolver.
	 *  @return The target resolver.
	 */
	protected ITargetResolver getTargetResolver(ServiceInvocationContext sic)
	{
		if(tr==null && tr!=NULL)
		{
			IService ser;
			Object service = sic.getObject();
			if(service instanceof ServiceInfo)
			{
				ServiceInfo si = (ServiceInfo)service;
				ser = si.getManagementService();
			}
			else 
			{
				ser = (IService)service;
			}
			
//			if(ser.getServiceIdentifier().getServiceType().getTypeName().indexOf("ITestService")!=-1)
//				System.out.println("reached");
			
			Class<ITargetResolver> cl = (Class<ITargetResolver>)ser.getPropertyMap().get(ITargetResolver.TARGETRESOLVER);
			if(cl!=null)
			{
				try
				{
					tr = cl.newInstance();
				}
				catch(RuntimeException e)
				{
					throw e;
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
//			else
//			{
//				System.out.println("no target resolver found for: "+sid.getServiceType());
//			}
			
			// search only once
			if(tr==null)
			{
				tr = NULL;
			}
		}
		
		return tr==NULL? null: tr;
	}
	
	/**
	 * 
	 */
	public static boolean isRedirectable(ServiceInvocationContext sic)
	{
		return !ResolveInterceptor.SERVICEMETHODS.contains(sic.getMethod()) 
			&& !ResolveInterceptor.START_METHOD.equals(sic.getMethod()) 
			&& !ResolveInterceptor.SHUTDOWN_METHOD.equals(sic.getMethod())
			&& !ValidationInterceptor.ALWAYSOK.contains(sic.getMethod())
			&& SReflect.isSupertype(IFuture.class, sic.getMethod().getReturnType());
	}
	
	/**
	 * 
	 */
	public static IFuture<Object> invoke(final IServiceIdentifier broken, final ServiceInvocationContext sic, 
			final IServiceIdentifier sid, final IExternalAccess ea, final ITargetResolver tr, final int maxretries, final int cnt)
	{
		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(sic.getMethod().getReturnType(), new FutureFunctionality((Logger)null));
		
		tr.determineTarget(sid, ea, broken)
			.addResultListener(new ExceptionDelegationResultListener<IService, Object>(ret) 
		{
			public void customResultAvailable(final IService ser) 
			{
				try
				{
					final Object res = sic.getMethod().invoke(ser, sic.getArgumentArray());
					if(res instanceof IIntermediateFuture)
					{
						IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
						{
							public void intermediateResultAvailable(Object result)
							{
								System.out.println("rec inter: "+result);
								((IntermediateFuture)ret).addIntermediateResult(result);
							}
							
							public void finished()
							{
								System.out.println("fini result");
								ret.setResult(res);
							}
							
							public void resultAvailable(Collection<Object> result) 
							{
								ret.setResult(res);
							}
							
							public void exceptionOccurred(Exception exception) 
							{
								if(exception instanceof ComponentNotFoundException)
								{
									if(cnt<maxretries)
									{
										// Invoke again and rebind service
										System.out.println("Exception during service invocation, retrying: "+cnt+"/"+maxretries);
										invoke(ser.getServiceIdentifier(), sic, sid, ea, tr, maxretries, cnt+1).addResultListener(new DelegationResultListener<Object>(ret));
									}
									else
									{
										ret.setResult(res);
									}
								}
							}
						};
						
						if(res instanceof ISubscriptionIntermediateFuture)
						{
							((ISubscriptionIntermediateFuture)res).addQuietListener(lis);
						}
						else
						{
							((IIntermediateFuture)res).addResultListener(lis);
						}
					}
					else if(res instanceof IFuture)
					{
						((IFuture)res).addResultListener(new IResultListener() 
						{
							public void resultAvailable(Object result) 
							{
								ret.setResult(res);
							}
							
							public void exceptionOccurred(Exception exception) 
							{
								if(exception instanceof ComponentNotFoundException)
								{
									if(cnt<maxretries)
									{
										// Invoke again and rebind service
										System.out.println("Exception during service invocation, retrying: "+cnt+"/"+maxretries);
										invoke(ser.getServiceIdentifier(), sic, sid, ea, tr, maxretries, cnt+1).addResultListener(new DelegationResultListener<Object>(ret));
									}
									else
									{
										ret.setResult(res);
									}
								}
							}
						});
					}
					else
					{
						ret.setResult(res);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Exception res = handleException(e);
					if(sic.getMethod().getReturnType().equals(IFuture.class))
					{
						ret.setResult(new Future(res));
					}
					else
					{
						throw SUtil.throwUnchecked(res);
					}
				}
			}
		});
		
		return ret;
	}
}
