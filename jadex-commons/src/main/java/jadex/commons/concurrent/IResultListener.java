package jadex.commons.concurrent;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
public interface IResultListener
{
	/**
	 *  Called when the result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void resultAvailable(Object source, Object result);
	
	/**
	 *  Called when an exception occurred.
	 *  @param source The source component.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Object source, Exception exception);
}
