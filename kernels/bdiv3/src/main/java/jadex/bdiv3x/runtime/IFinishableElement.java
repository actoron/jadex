package jadex.bdiv3x.runtime;

import jadex.commons.future.IResultListener;

/**
 *  Interface for finishable elements.
 */
public interface IFinishableElement<E>
{
	/**
	 *  Add a new listener to get notified when the goal is finished.
	 *  @param listener The listener.
	 */
	public void addListener(IResultListener<E> listener);

	/**
	 *  Remove a listener.
	 *  @param listener The listener.
	 */
	public void removeListener(IResultListener<E> listener);
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException();
	
	/**
	 *  Test if element is succeeded.
	 *  @return True, if is succeeded.
	 */
	public abstract boolean	isSucceeded();
	
	/**
	 *  Test if element is failed.
	 *  @return True, if is failed.
	 */
	public abstract boolean	isFailed();
	
	/**
	 *  Test if goal is finished.
	 *  @return True, if is finished.
	 */
	public boolean isFinished();
}
