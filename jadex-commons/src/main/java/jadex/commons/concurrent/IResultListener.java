package jadex.commons.concurrent;

/**
 *  Callback interface for methods on agent platform level. 
 */
public interface IResultListener
{
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(Object result);
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception);
}
