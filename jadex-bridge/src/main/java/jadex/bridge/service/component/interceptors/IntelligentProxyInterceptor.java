package jadex.bridge.service.component.interceptors;

import jadex.bridge.IExternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationTargetException;

/**
 * 
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
	
	protected final static ITargetResolver NULL = new ITargetResolver() 
	{
		public IFuture<IService> determineTarget(IServiceIdentifier sid, IExternalAccess agent) 
		{
			return null;
		}
	};
		
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public IntelligentProxyInterceptor(IExternalAccess ea, IServiceIdentifier sid)
	{
		this.ea = ea;
		this.sid = sid;
	}
	
	//-------- methods --------

	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(sic.getMethod().getName().indexOf("methodA")!=-1)
			System.out.println("methodA");
		
		ITargetResolver tr = getTargetResolver(sic);
		if(tr!=null && isRedirectable(sic))
		{
			System.out.println("redirecting call: "+sic.getMethod());
			
			tr.determineTarget(sid, ea)
				.addResultListener(new ExceptionDelegationResultListener<IService, Void>(ret) 
			{
				public void customResultAvailable(IService ser) 
				{
					try
					{
						Object res = sic.getMethod().invoke(ser, sic.getArgumentArray());
						sic.setResult(res);
						ret.setResult(null);
					}
					catch(Exception e)
					{
						Throwable t	= e instanceof InvocationTargetException
							? ((InvocationTargetException)e).getTargetException() : e;
						Exception re = t instanceof Exception ? (Exception)t : new RuntimeException(t);
						
						if(sic.getMethod().getReturnType().equals(IFuture.class))
						{
							sic.setResult(new Future(re));
						}
						else
						{
							throw re instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
						}
					}
				}
			});
		}
		else
		{
			sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
		}
					
		return ret;
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
	protected boolean isRedirectable(ServiceInvocationContext sic)
	{
		return !ResolveInterceptor.SERVICEMETHODS.contains(sic.getMethod()) 
			&& !ResolveInterceptor.START_METHOD.equals(sic.getMethod()) 
			&& !ResolveInterceptor.SHUTDOWN_METHOD.equals(sic.getMethod())
			&& !ValidationInterceptor.ALWAYSOK.contains(sic.getMethod())
			&& SReflect.isSupertype(IFuture.class, sic.getMethod().getReturnType());
	}
}
