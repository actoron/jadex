package jadex.commons.future;


/**
 * Interface for futures. Similar to Java Future interface but adds a listener
 * notification mechanism.
 */
//@Reference
public interface IFuture<E>
{
	// -------- constants --------

	/**
	 *  A future representing a completed action. Can be used as direct return
	 *  value of methods that do not perform asynchronous operations and do not
	 *  return a result value.
	 */
	public static final IFuture<Void>	DONE	= new Future<Void>((Void)null);
	
	// -------- methods --------

	/**
	 *  Test if done, i.e. result is available.
	 *  @return True, if done.
	 */
	public boolean isDone();

	/**
	 *  Get the exception, if any.
	 *  @return	The exception, if any, or null if the future is not yet done or succeeded without exception.
	 */
	public Exception	getException();

	/**
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get(ISuspendable caller);

	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @return The future result.
	 */
	public E get(ISuspendable caller, long timeout);

	/**
	 *  Add a result listener.
	 *  @param listener The listener.
	 */
	public void addResultListener(IResultListener<E> listener);
}
