package jadex.platform.service.dht;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * A component step that repeats itself. 
 *
 * @param <T>
 */
public abstract class RepetitiveComponentStep<T> implements IComponentStep<T>
{

	private long	delay;
	private IInternalAccess	ia;

	/**
	 * Constructor.
	 * @param delay delay in ms to wait between repeated executions.
	 */
	public RepetitiveComponentStep(long delay)
	{
		this.delay = delay;
	}

	@Override
	public IFuture<T> execute(IInternalAccess ia)
	{
		this.ia = ia;
		IFuture<T> customExecute = customExecute(ia);
		customExecute.addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				reschedule();
			}
			public void exceptionOccurred(Exception exception)
			{
				reschedule();
			}
		});
		return customExecute;
	}
	
	/**
	 * Reschedules this step.
	 */
	protected void reschedule()
	{
		ia.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this);
	}

	/**
	 * Insert work code in this method.
	 * @param ia
	 * @return
	 */
	public abstract IFuture<T> customExecute(IInternalAccess ia);

}
