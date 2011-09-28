package jadex.android.bluetooth.service;

public interface IFuture {

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