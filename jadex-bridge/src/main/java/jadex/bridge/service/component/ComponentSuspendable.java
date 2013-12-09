package jadex.bridge.service.component;

import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;

/**
 *  Allow waiting for futures by blocking a component.
 */
public class ComponentSuspendable implements ISuspendable
{
	/** The component suspendables. */
	public static final ThreadLocal<ComponentSuspendable> COMSUPS = new ThreadLocal<ComponentSuspendable>();
	
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The current future. */
	protected Future<?>	future;
	
	//-------- constructors --------
	
	/**
	 *  Create a component suspendable.
	 */
	public ComponentSuspendable(IComponentAdapter adapter)
	{
		this.adapter	= adapter;
	}
	
	//-------- ISuspandable interface --------

	/**
	 *  Suspend the execution of the suspendable.
	 *  @param future	The future to wait for.
	 *  @param timeout The timeout.
	 */
	public void suspend(Future<?> future, long timeout)
	{
//		System.out.println("ComponentSuspendable.suspend "+Thread.currentThread());
		
		synchronized(this)
		{
			this.future	= future;
			
			// Todo: timeout
//			if(timeout>0)
//			{
//				this.wait(timeout);
//			}
			
			try
			{
				COMSUPS.set(this);
				adapter.block(this);
			}
			finally
			{
				this.future	= null;
//				System.out.println("ComponentSuspendable.unsuspend "+Thread.currentThread());
			}
		}
	}
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume(final Future<?> future)
	{
		System.out.println("ComponentSuspendable.resume "+Thread.currentThread());
//		Thread.dumpStack();
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable()
			{
				public void run()
				{
					synchronized(ComponentSuspendable.this)
					{
						// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
						if(future==ComponentSuspendable.this.future)
						{
							adapter.unblock(ComponentSuspendable.this);
						}
					}
				}
			});
		}
		else
		{
			synchronized(this)
			{
				// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
				if(future==this.future)
				{
					adapter.unblock(this);
				}
			}			
		}
//		System.out.println("ComponentSuspendable.unresume "+Thread.currentThread());
	}
	
	/**
	 *  Get the monitor for waiting.
	 *  @return The monitor.
	 */
	public Object getMonitor()
	{
		return this;
	}

	/**
	 *  Get the future.
	 *  @return The future.
	 */
	public Future<?> getFuture()
	{
		return future;
	}
}
