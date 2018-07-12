package jadex.bridge.service.component;

import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadLocalTransferHelper;

/**
 *  Allow waiting for futures by blocking a component.
 */
public class ComponentSuspendable extends ThreadLocalTransferHelper implements ISuspendable
{
	/** The component suspendables. */
	public static final ThreadLocal<ComponentSuspendable> COMSUPS = new ThreadLocal<ComponentSuspendable>();
	
	//-------- attributes --------
	
	/** The component adapter. */
	protected IInternalAccess	agent;
	
	/** The current future. */
	protected Future<?>	future;
	
	/** The thread locals. */
	protected Map<ThreadLocal<Object>, Object> vals;
	
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
	 *  @param realtime Flag if timeout is realtime (in contrast to simulation time).
	 */
	public void suspend(Future<?> future, long timeout, boolean realtime)
	{
//		if(agent.toString().indexOf("IntermediateBlockingTest@")!=-1)
//			System.err.println("ComponentSuspendable.suspend "+agent);
		
		if(timeout==Timeout.UNSET)
			timeout = getDefaultTimeout();
		
		synchronized(this)
		{
			this.future	= future;
			
			try
			{
				COMSUPS.set(this);
				((IInternalExecutionFeature)agent.getFeature(IExecutionFeature.class))
					.block(this, timeout, realtime);
			}
//			catch(Error e)
//			{
//				if(agent.toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("ComponentSuspendable.unsuspend 1"+Thread.currentThread());
//				}
//				throw e;
//			}
//			catch(RuntimeException e)
//			{
//				if(agent.toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("ComponentSuspendable.unsuspend 2"+Thread.currentThread());
//				}
//				throw e;
//			}
			finally
			{
//				if(agent.toString().indexOf("IntermediateBlockingTest@")!=-1)
//					System.err.println("ComponentSuspendable.unsuspend "+agent);
				afterSwitch();
				this.future	= null;
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
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
//			System.out.println("ComponentSuspendable.resume1 "+Thread.currentThread());
			agent.getFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
//					System.out.println("ComponentSuspendable.resume2 "+Thread.currentThread());
					synchronized(ComponentSuspendable.this)
					{
//						System.out.println("ComponentSuspendable.resume3 "+Thread.currentThread());
						// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
						if(future==ComponentSuspendable.this.future)
						{
//							System.out.println("ComponentSuspendable.resume4 "+Thread.currentThread());
							beforeSwitch();
							((IInternalExecutionFeature)agent.getFeature(IExecutionFeature.class))
								.unblock(ComponentSuspendable.this, null);
						}
					}
					return IFuture.DONE;
				}
			});
		}
		else
		{
//			System.out.println("ComponentSuspendable.resume5 "+Thread.currentThread());
			synchronized(this)
			{
//				System.out.println("ComponentSuspendable.resume6 "+Thread.currentThread());
				// Only wake up if still waiting for same future (invalid resume might be called from outdated future after timeout already occurred).
				if(future==this.future)
				{
//					System.out.println("ComponentSuspendable.resume7 "+Thread.currentThread());
//					beforeSwitch();	// Todo: why not beforeSwitch()?
					((IInternalExecutionFeature)agent.getFeature(IExecutionFeature.class))
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
		return Starter.getLocalDefaultTimeout(agent.getId());
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
