package jadex.bridge.component;

import jadex.bridge.IComponentStep;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

/**
 *  The execution feature allows to schedule steps
 *  to be synchronously executed on the component.
 */
public interface IExecutionFeature
{
	/**
	 *  Execute a component step.
	 */
	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Execute an immediate component step,
	 *  i.e., the step is executed also when the component is currently suspended.
	 */
	public <T>	IFuture<T> scheduleImmediate(IComponentStep<T> step);
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime);

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step);

	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay, boolean realtime);
	
	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay);
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	// TimerWrapper
	public IFuture<Void> waitForTick(final IComponentStep<Void> run);
	
	// todo:?
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitForImmediate(long delay, IComponentStep step);

	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread();

	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener);
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener);
}
