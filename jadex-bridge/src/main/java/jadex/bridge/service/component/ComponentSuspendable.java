package jadex.bridge.service.component;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
	protected IInternalAccess	agent;
	
	/** The current future. */
	protected Future<?>	future;
	
	//-------- constructors --------
	
	/**
	 *  Create a component suspendable.
	 */
	public ComponentSuspendable(IInternalAccess agent)
	{
		this.agent	= agent;
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
		
		if(timeout==Timeout.UNSET)
			timeout = getDefaultTimeout();
		
		synchronized(this)
		{
			this.future	= future;
			
			try
			{
				COMSUPS.set(this);
				((IInternalExecutionFeature)agent.getComponentFeature(IExecutionFeature.class))
					.block(this, timeout);
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
//		System.out.println("ComponentSuspendable.resume "+Thread.currentThread());
//		Thread.dumpStack();
		if(!agent.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			agent.getComponentFeature(IExecutionFeature.class).scheduleImmediate(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					synchronized(ComponentSuspendable.this)
					{
						// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
						if(future==ComponentSuspendable.this.future)
						{
							((IInternalExecutionFeature)agent.getComponentFeature(IExecutionFeature.class))
								.unblock(ComponentSuspendable.this, null);
						}
					}
					return IFuture.DONE;
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
					((IInternalExecutionFeature)agent.getComponentFeature(IExecutionFeature.class))
						.unblock(this, null);
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
	
	/**
	 *  Get the default timeout.
	 *  @return The default timeout (-1 for none).
	 */
	public long getDefaultTimeout()
	{
		return BasicService.getLocalDefaultTimeout();
//		return ((INonUserAccess)agent).getPlatformData().
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ComponentSuspendable [adapter=" + agent + "]";
	}
}
