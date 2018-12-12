package jadex.commons.future;

/**
 * 
 */
public interface IUndoneResultListener<E>	extends IResultListener<E>
{
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result);
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception);
}
