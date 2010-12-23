package jadex.commons;

import jadex.commons.concurrent.IResultListener;

/**
 *  Result listener with additional notifications in case 
 */
public interface IIntermediateResultListener extends IResultListener
{
	/**
	 *  Called when an intermediate result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(Object source, Object result);
	
	/**
	 *  Called when an intermediate exception occurred.
	 *  @param source The source component.
	 *  @param exception The exception.
	 */
	public void intermediateExceptionOccurred(Object source, Exception exception);
}
