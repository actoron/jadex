package jadex.bridge.service.component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SResultListener;

/**
 *  Lazy service proxy that resolves a service via a search command.
 */
public class UnresolvedServiceInvocationHandler implements InvocationHandler
{
	/** The component. */
	protected IInternalAccess ia;
	
	/** The service. */
	protected Future<IService> delegate;
	
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
			delegate = new Future<>();
			@SuppressWarnings("unchecked")
			ISubscriptionIntermediateFuture<Object> queryfut = (ISubscriptionIntermediateFuture<Object>) ia.addQuery(query);
			queryfut.addResultListener(new IIntermediateResultListener<Object>()
			{
				public void intermediateResultAvailable(Object result)
				{
					queryfut.terminate();
					delegate.setResultIfUndone((IService) result);
				}

				public void resultAvailable(Collection<Object> result)
				{
				}
				public void exceptionOccurred(Exception exception)
				{
				}
				public void finished()
				{
				};
			});
			
			long timeout = ServiceCall.getNextInvocation() != null ? ServiceCall.getNextInvocation().getTimeout() : SUtil.DEFTIMEOUT;
			ia.waitForDelay(timeout).thenAccept(done ->
			{
				queryfut.terminate();
				delegate.setExceptionIfUndone(new ServiceNotFoundException("Service not found: " + query.toString()));
			});
			delegate.exceptionally(ex -> delegate = null);
		}
		
		IService serv = null;
		
		Object ret = null;
		try
		{
			serv = delegate.get();
			ret = method.invoke(serv, args);
			if (ret instanceof IFuture)
			{
				((IFuture<?>) ret).exceptionally(e ->
				{
					if (e instanceof TimeoutException)
						delegate = null;
				});
				return ret;
			}
		}
		catch (Exception e)
		{
			if (e instanceof TimeoutException)
				delegate = null;
			throw SUtil.throwUnchecked(e);
		}
		
		return ret;
	}
}
