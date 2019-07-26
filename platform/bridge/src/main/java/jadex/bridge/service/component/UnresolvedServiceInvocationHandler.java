package jadex.bridge.service.component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Lazy service proxy that resolves a service via a search command.
 */
public class UnresolvedServiceInvocationHandler implements InvocationHandler
{
	/** The component. */
	protected IInternalAccess ia;
	
	/** The service. */
	protected IService delegate;
	
	/** The service being acquired. */
	IFuture<IService> delegatefut;
	
	/** The search query for a lazy proxy. */
	protected ServiceQuery<?> query;

	/**
	 *  Create a new invocation handler.
	 */
	public UnresolvedServiceInvocationHandler(IInternalAccess ia, ServiceQuery<?> query)
	{
		this.ia = ia;
		this.query = query;
	}
	
	/**
	 *  Called on method invocation.
	 */
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		if (delegate == null)
		{
			if (delegatefut == null)
			{
				@SuppressWarnings("unchecked")
				IFuture<IService> fut = (IFuture<IService>) ia.searchService(query, null);
				fut.thenAccept(serv ->
				{
					delegate = serv;
					delegatefut = null;
				}).exceptionally(e -> delegatefut = null);
				delegatefut = (IFuture<IService>) fut;
			}
			if (!SReflect.isSupertype(IFuture.class, method.getReturnType()))
			{
				// Method is synchronous, no choice...
				IService serv = delegatefut.get();
				return method.invoke(serv, args);
			}
			else
			{
				Future<?> ret = FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality(ia.getLogger()));
				delegatefut.addResultListener(new IResultListener<IService>()
				{
					public void resultAvailable(IService result)
					{
						IFuture<?> origret = null;
						try
						{
							origret = (IFuture<?>) method.invoke(result, args);
						}
						catch (Exception e)
						{
							ret.setException(e);
						}
						FutureFunctionality.connectDelegationFuture(ret, origret);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
				return ret;
			}
		}
		
		return method.invoke(delegate, args);
	}
}
