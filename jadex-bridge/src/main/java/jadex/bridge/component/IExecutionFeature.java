package jadex.bridge.component;

import jadex.bridge.IComponentStep;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The execution feature allows to schedule steps
 *  to be synchronously executed on the component.
 */
public interface IExecutionFeature
{
	/** Constant for first normal step level. */
	public static final int STEP_PRIORITY_NOMRAL = 0;

	/** Constant for first immediate step level. */
	public static final int STEP_PRIORITY_IMMEDIATE = 100;
	
	/**
	 *  Execute a component step.
	 *  @param step The component step.
	 */
	public <T>	IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Execute a component step.
	 *  @param step The component step.
	 *  @param priority The step priority.
	 *  The priority x>STEP_PRIORITY_IMMEDIATE being immediate steps,
	 *  i.e. all steps with prio x>=STEP_PRIORITY_IMMEDIATE are always executed (even when suspended).
	 *  Default steps get prio STEP_PRIORITY_NOMRAL (not immediate). 
	 */
	public <T>	IFuture<T> scheduleStep(int priority, IComponentStep<T> step);
	
//	/**
//	 *  Execute an immediate component step,
//	 *  i.e., the step is executed also when the component is currently suspended.
//	 */
//	public <T>	IFuture<T> scheduleImmediate(IComponentStep<T> step);
	
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
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	// TimerWrapper
	public IFuture<Void> waitForTick();
	
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

	/**
	 * Repeats a ComponentStep periodically, until terminate() is called on result future or a failure occurs in a step.
	 * @param initialDelay delay before first execution in milliseconds
	 * @param delay delay between scheduled executions of the step in milliseconds
	 * @param step The component step
	 * @return The intermediate results
	 */
	public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialDelay, long delay, IComponentStep<T> step);
	
	/**
	 * Repeats a ComponentStep periodically, until terminate() is called on result future.
	 * @param initialDelay delay before first execution in milliseconds
	 * @param delay delay between scheduled executions of the step in milliseconds
	 * @param step The component step
	 * @param ignorefailures Don't terminate repeating after a failed step.
	 * @return The intermediate results
	 */
	public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialDelay, long delay, IComponentStep<T> step, boolean ignorefailures);
	
}
