package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 *  A priority component step can define an execution priority.
 */
public interface IFutureReturnTypeComponentStep<T> extends IComponentStep<T>
{
	/**
	 *  Get the future return type of the step.
	 *  @return The future return type. 
	 */
	public Class<? extends IFuture> getFutureReturnType();
}
