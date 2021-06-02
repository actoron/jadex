package jadex.base.test.impl;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.interceptors.ResolveInterceptor;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  A factory for creating service instances that delegate to the same shared instance, i.e. it uses the first instance and ignores subsequent ones.
 */
public class SharedServiceFactory<T>	implements Function<Supplier<T>, T>
{
	//-------- attributes --------
	
	/** The wrapper creation function. */
	protected BiFunction<IComponentIdentifier, SharedServiceFactory<T>, SharedService<T>>	wrapperfactory;
	
	/** The shared service instance. */
	protected volatile T	instance	=  null;
	
	/** The init future of the shared service. */
	protected volatile IFuture<Void>	inited	= null;
	
	/** The usage count to know when the service can be shut down. */
	protected volatile int	cnt	= 0;
	
	//-------- constructors --------
	
	/**
	 *  Create a factory for service sharing.
	 *  @param wrapperfactory	Function to create the individual wrappers for each platform.
	 */
	public SharedServiceFactory(BiFunction<IComponentIdentifier, SharedServiceFactory<T>, SharedService<T>> wrapperfactory)
	{
		this.wrapperfactory	= wrapperfactory;
	}

	/**
	 *  Create a new wrapper for the first supplied instance.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T apply(Supplier<T> creator)
	{
		synchronized(this)
		{
			if(instance==null)
			{
				instance	= creator.get();
//				System.out.println("Created shared service instance: "+instance);
			}
		}
		return (T) wrapperfactory.apply(IComponentIdentifier.LOCAL.get(), this);
	}
	
	/**
	 *  Start the original service on first invocation.
	 */
	public IFuture<Void> startService()
	{
		Future<Void>	init	= null;
		synchronized(this)
		{
			cnt++;
			if(inited==null)
			{
				init	=  new Future<Void>();
				inited	= init;
			}
		}
		
		if(init!=null)
		{
//			System.out.println("Starting shared service instance: "+instance);
			if(instance instanceof IInternalService)
			{
				((IInternalService)instance).startService().addResultListener(new DelegationResultListener<Void>(init));
			}
			else
			{
				Method	m	= ResolveInterceptor.searchMethod(instance.getClass(), OnStart.class);
				if(m!=null)
				{
					try
					{
						Object	ret	= m.invoke(instance);
						if(ret instanceof IFuture)
						{
							@SuppressWarnings("unchecked")
							IFuture<Void>	fut	= (IFuture<Void>)ret;
							fut.addResultListener(new DelegationResultListener<Void>(init));
						}
						else
						{
							init.setResult(null);
						}
					}
					catch(Exception e)
					{
						init.setException(e);
					}
				}
				else
				{
					init.setResult(null);					
				}
			}
		}
		
		return inited;
	}
	
	/**
	 *  Shutdown the original service on last invocation.
	 */
	public IFuture<Void> shutdownService()
	{
		Object	tmp	= null;
		synchronized(this)
		{
			cnt--;
			if(cnt==0)
			{
				tmp	= instance;
				// Clear to allow another platform to be started with new service.
				instance	= null;
				inited	=  null;
			}
		}
		
		if(tmp!=null)
		{
//			System.out.println("Terminating shared service instance: "+tmp);
			if(tmp instanceof IInternalService)
			{
				return ((IInternalService)tmp).shutdownService();
			}
			else
			{
				Method	m	= ResolveInterceptor.searchMethod(tmp.getClass(), OnEnd.class);
				if(m!=null)
				{
					try
					{
						Object	ret	= m.invoke(tmp);
						if(ret instanceof IFuture)
						{
							@SuppressWarnings("unchecked")
							IFuture<Void>	fut	= (IFuture<Void>)ret;
							return fut;
						}
						else
						{
							return IFuture.DONE;
						}
					}
					catch(Exception e)
					{
						return new Future<>(e);
					}
				}
				else
				{
					return IFuture.DONE;				
				}
			}
		}
		else
		{
			return IFuture.DONE;
		}
	}
}