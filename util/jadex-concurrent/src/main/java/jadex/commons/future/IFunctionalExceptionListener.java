package jadex.commons.future;

/**
 *  Callback interface for methods that should operate decoupled from caller thread. 
 */
// @Reference
// @FunctionalInterface // this is a functional interface in java 8
public interface IFunctionalExceptionListener
{
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception);
}
