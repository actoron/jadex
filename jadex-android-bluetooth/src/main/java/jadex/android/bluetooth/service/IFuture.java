package jadex.android.bluetooth.service;

/**
 * Used to pass asynchronous results.
 * @author Julian Kalinowski
 */
public interface IFuture {
	
	/**
	 * Result Listener for the Future
	 */
	public interface IResultListener {
		/**
		 * Called when a result is available
		 * @param result
		 */
		void resultAvailable(Object result);
		
		/**
		 * Called when an exception occurred during execution
		 * @param exception
		 */
		void exceptionOccurred(Exception exception);
	}

	/**
	 *  Set the result. 
	 *  Listener notifications occur on calling thread of this method.
	 *  @param result The result.
	 */
	public abstract void setResult(Object result);

	/**
	 *  Set the exception. 
	 *  Listener notifications occur on calling thread of this method.
	 *  @param exception The exception.
	 */
	public abstract void setException(Exception exception);

	/**
	 *  Add a result listener.
	 *  @param listsner The listener.
	 */
	public abstract void addResultListener(IResultListener listener);

	/**
	 *  Test if done, i.e. result is available.
	 *  @return True, if done.
	 */
	public abstract boolean isDone();

}