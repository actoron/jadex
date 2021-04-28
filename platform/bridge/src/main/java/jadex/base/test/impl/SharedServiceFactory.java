package jadex.base.test.impl;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
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
	protected volatile Future<Void>	inited	= null;
	
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
		boolean	init	= false;
		synchronized(this)
		{
			cnt++;
			if(inited==null)
			{
				init	= true;
				inited	= new Future<Void>();
			}
		}
		
		if(init)
		{
//			System.out.println("Starting shared service instance: "+instance);
			 ((BasicService)instance).startService().addResultListener(new DelegationResultListener<Void>(inited));
		}
		
		return inited;
	}
	
	/**
	 *  Shutdown the original service on last invocation.
	 */
	public IFuture<Void> shutdownService()
	{
		BasicService	tmp	= null;
		synchronized(this)
		{
			cnt--;
			if(cnt==0)
			{
				tmp	= (BasicService)instance;
				// Clear to allow another platform to be started with new service.
				instance	= null;
				inited	=  null;
			}
		}
		
		if(tmp!=null)
		{
//			System.out.println("Terminating shared service instance: "+tmp);
			return tmp.shutdownService();
		}
		else
		{
			return IFuture.DONE;
		}
	}
}