package jadex.commons.future;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
// @Reference
public interface IResultListener<E>
{
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(E result);
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception);
}
