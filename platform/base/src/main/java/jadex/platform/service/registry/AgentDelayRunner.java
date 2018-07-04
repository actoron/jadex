package jadex.platform.service.registry;

import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.collection.IDelayRunner;
import jadex.commons.future.IFuture;

/**
 *  Delay runner based on agent time scheduling.
 */
public class AgentDelayRunner implements IDelayRunner
{
	/** The agent. */
	protected IInternalAccess agent;
	
	/** Flag if delay runner was cancelled. */
	protected volatile boolean cancelled;
	
	/**
	 *  Create a new AgentDelayRunner.
	 */
	public AgentDelayRunner(IInternalAccess agent)
	{
		this.agent = agent;
	}
	
	/**
	 *  Wait for a delay.
	 *  @param delay The delay.
	 *  @param step The step.
	 */
	public Runnable waitForDelay(long delay, final Runnable step)
	{
		if(cancelled)
			throw new RuntimeException("Runner was cancelled already");
		
		final boolean valid[] = new boolean[]{true};
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IConditionalComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				try
				{
					// TODO: Users of AgentDelayRunner should handle their exceptions themselves!
					// ...printStackTrace() is not a solution!
					step.run();
				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
				return IFuture.DONE;
			}
			
			public boolean isValid()
			{
				return valid[0] && !cancelled;
			}
		}, true);
		
		// TODO: Users of AgentDelayRunner should handle their exceptions themselves!
		// ...printStackTrace() is not a solution!
//		.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				if(!(exception instanceof StepInvalidException)
//					&& !(exception instanceof ComponentTerminatedException))
//					exception.printStackTrace();
//			}
//		});
		
		// Set the valid flag of the step to invalid so that the step is not executed
		return new Runnable()
		{
			public void run()
			{
				valid[0] = false;
			}
		};

	}
	
	/**
	 *  Cancel the timer.
	 */
	public void cancel()
	{
		cancelled = true;
	}
}
