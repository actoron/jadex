package jadex.bridge.service.component;

import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;

/**
 *  Allow waiting for futures by blocking a component.
 * @author Alex
 *
 */
public class ComponentSuspendable implements ISuspendable
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The current future. */
	protected IFuture<?>	future;
	
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
	public void suspend(IFuture<?> future, long timeout)
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
			
			adapter.block(this);
			this.future	= null;
		}
//		System.out.println("ComponentSuspendable.unsuspend "+Thread.currentThread());
	}
	
	/**
	 *  Resume the execution of the suspendable.
	 */
	public void resume(final IFuture<?> future)
	{
//		System.out.println("ComponentSuspendable.resume "+Thread.currentThread());
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
}
