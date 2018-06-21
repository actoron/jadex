package jadex.bridge.service.component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jadex.bridge.SFuture;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceInvalidException;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.SResultListener;

/**
 *  Lazy service proxy that resolves a service via a search command.
 */
public class UnresolvedServiceInvocationHandler implements InvocationHandler
{
	/** The real service. */
	protected IService delegate;
	
	/** The search command for a lazy proxy. */
	protected IResultCommand<IFuture<Object>, Void> searchcmd;
	
	/** The search future. */
	protected IFuture<Object> searchfut;

	/**
	 *  Create a new invocation handler.
	 */
	public UnresolvedServiceInvocationHandler(IResultCommand<IFuture<Object>, Void> searchcmd)
	{
		this.searchcmd = searchcmd;
	}
	
	/**
	 *  Called on method invocation.
	 */
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		Object ret = null;
		
		if(delegate!=null)
		{
			// Directly forward to resolved service
			ret = method.invoke(delegate, args);
		}
		else if(searchfut==null) // use ongoing search
		{
			searchfut = searchcmd.execute(null);
		}
		
		if(searchfut!=null) // start search
		{
			// Resolve delegate service
			
			final Future<Object> fut = (Future)(SReflect.isSupertype(IFuture.class, method.getReturnType())? SFuture.getFuture(method.getReturnType()): null);
			ret = fut;
			
			searchfut.addResultListener(new SearchResultListener(fut, proxy, method, args));
			
			if(fut==null)
				throw new ServiceInvalidException("Service is not yet resolved: "+this);
		}
		
		return ret;
	}

	/**
	 *  Listener that executes service invocation after a search.
	 */
	class SearchResultListener implements IResultListener<Object>
	{
		/** The future to that the method result must be delegated. Can be null if method is synchronous. */
		protected Future<Object> fut;
		
		/** Proxy object from original call. */
		protected Object proxy;
		
		/** Method object from original call. */
		protected Method method;
		
		/** Arguments from original call. */
		protected Object[] args;
		
		/**
		 *  Create a new UnresolvedServiceInvocationHandler.
		 */
		public SearchResultListener(Future<Object> fut, Object proxy, final Method method, final Object[] args)
		{
			this.fut = fut;
			this.proxy = proxy;
			this.method = method;
			this.args = args;
		}
		
		/**
		 *  Called after a successful search.
		 */
		public void resultAvailable(Object result)
		{
			searchfut = null;
			delegate = (IService)result;
			
			if(fut!=null)
			{
				try
				{
					IFuture resfut = (IFuture)invoke(proxy, method, args);
					SResultListener.delegateFromTo(resfut, fut);
				}
				catch(Throwable t)
				{
					fut.setException(new RuntimeException(t));
				}
			}
		}

		/**
		 *  Called after a failed search.
		 */
		public void exceptionOccurred(Exception exception)
		{
			searchfut = null;
			
			if(fut!=null)
			{
				fut.setException(exception);
			}
			else
			{
				System.out.println("Exception in resolving service: "+exception);
			}
		}
	}
}
