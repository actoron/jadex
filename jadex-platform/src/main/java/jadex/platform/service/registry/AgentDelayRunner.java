package jadex.platform.service.registry;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.collection.IDelayRunner;
import jadex.commons.future.IFuture;

/**
 *  Delay runner based on agent time scheduling.
 */
public class AgentDelayRunner implements IDelayRunner
{
	public static final Runnable NOP = new Runnable()
	{
		public void run()
		{
		}
	};
	
	/** The agent. */
	protected IInternalAccess agent;
	
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
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				step.run();
				return IFuture.DONE;
			}
		});
		return NOP;
	}
	
	/**
	 *  Cancel the timer.
	 */
	public void cancel()
	{
		// nop
	}
}
