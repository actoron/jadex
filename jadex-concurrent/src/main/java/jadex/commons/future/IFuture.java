package jadex.commons.future;

import jadex.commons.functional.BiFunction;
import jadex.commons.functional.Consumer;
import jadex.commons.functional.Function;


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
	
	/**
	 *  A future representing a true result.
	 */
	public static final IFuture<Boolean>	TRUE	= new Future<Boolean>(Boolean.TRUE);
	
	/**
	 *  A future representing a false result.
	 */
	public static final IFuture<Boolean>	FALSE	= new Future<Boolean>(Boolean.FALSE);
	
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

//	/**
//	 *  Get the result - blocking call.
//	 *  @return The future result.
//	 *  deprecated Use get() instead.
//	 */
//	public E get(ISuspendable caller);
//
//	/**
//	 *  Get the result - blocking call.
//	 *  @param timeout The timeout in millis.
//	 *  @return The future result.
//	 *  deprecated Use get(timeout) instead.
//	 */
//	public E get(ISuspendable caller, long timeout);

	/**
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get();
	
	/**
	 *  Get the result - blocking call.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
	 *  @return The future result.
	 */
	public E get(boolean realtime);

	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @return The future result.
	 */
	public E get(long timeout);
	
	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
	 *  @return The future result.
	 */
	public E get(long timeout, boolean realtimr);

	/**
	 *  @deprecated - From 3.0. Use the version without suspendable.
	 *  Will NOT use the suspendable given as parameter.
	 *  
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get(ThreadSuspendable sus);
	
	/**
	 *  Add a result listener.
	 *  @param listener The listener.
	 */
	public void addResultListener(IResultListener<E> listener);
	
	/**
	 * Add an functional result listener, which is only called on success.
	 * Exceptions will be handled by DefaultResultListener.
	 * 
	 * @param listener The listener.
	 */
	public void addResultListener(IFunctionalResultListener<E> listener);

	/**
	 * Add a result listener by combining an OnSuccessListener and an
	 * OnExceptionListener.
	 * 
	 * @param sucListener The listener that is called on success.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
	public void addResultListener(IFunctionalResultListener<E> sucListener, IFunctionalExceptionListener exListener);


	//-------- java8 extensions --------
	
	/**
	 *  Applies a function after the result is available, using the result of this Future as input.
	 *  @param function Function that takes the result of this future as input and delivers t. 
	 *  @return Future of the result after the function has been applied.
	 */
	public <T> IFuture<T> thenApply(Function<? super E, ? extends T> function);
	
	/**
	 *  Applies a function after the result is available, using the result of this Future as input.
	 *  @param function Function that takes the result of this future as input and delivers t. 
	 *  @param futuretype The type of the return future.
	 *  @return Future of the result after the function has been applied.
	 */
	public <T> IFuture<T> thenApply(Function<? super E, ? extends T> function, Class<?> futuretype);
	
	/**
	 *  The result of this future is delegated to the given (future-returning) function.
	 *  The result of the function will be available in the returned future.
	 *  @param function Function that takes the result of this future as input and delivers future(t). 
	 *  @return Future of the result of the second async call.
	 */
	public <T> IFuture<T> thenCompose(Function<? super E, IFuture<T>> function);
	
	/**
	 *  The result of this future is delegated to the given (future-returning) function.
	 *  The result of the function will be available in the returned future.
	 *  @param function Function that takes the result of this future as input and delivers future(t). 
	 *  @param futuretype The type of the return future. If null, a default future is created.
	 *  @return Future of the result of the second async call.
	 */
	public <T> IFuture<T> thenCompose(Function<? super E, IFuture<T>> function, Class<?> futuretype);
	
	/**
	 *  Applies a synchronous function consuming the result after it is available.
	 *  @param consumer Consumer that takes the result of this future as input and consumes it. 
	 *  @return Future of the result of the second async call.
	 */
	public IFuture<Void> thenAccept(Consumer<? super E> consumer);
	
	/**
	 *  Applies a function consuming the result after it is available.
	 *  @param consumer Consumer that takes the result of this future as input and consumes it. 
	 *  @param futuretype The type of the return future. If null, a default future is created.
	 *  @return Future of the second async call (returning void).
	 */
	public IFuture<Void> thenAccept(Consumer<? super E> consumer, Class<?> futuretype);
	
	/**
	 *  Combines this and another future and uses the given bifunction to calculate the result.
	 *  Both future results are passed to the function as input.
	 *  @param function BiFunction that takes the result of this and given other future as input and produces output. 
	 *  @param futuretype The type of the return future. If null, a default future is created.
	 *  @return Future of the second async call.
	 *  
	 *  Types: function is: E,U -> V
	 */
	public <U,V> IFuture<V> thenCombine(IFuture<U> other, BiFunction<? super E,? super U, ? extends V> function, Class<?> futuretype);
	
	/**
	 *  Combines this and another future and uses the given bifunction to asynchronously calculate the result.
	 *  Both future results are passed to the function as input.
	 *  @param function BiFunction that takes the result of this and given other future as input and asynchronously produces output. 
	 *  @param futuretype The type of the return future. If null, a default future is created.
	 *  @return Future of the second async call (returning void).
	 *  
	 *  Types: function is: E,U -> V
	 */
//	public <U,V> IFuture<V> thenCombineAsync(IFuture<U> other, BiFunction<? super E,? super U, IFuture<V>> function, Class<?> futuretype);
	
	/**
	 * The given function will be executed with either of the result of this and the given other future.
	 * The returned Future will receive the result of the function execution.
	 * If both futures return results, the first is used to call the function.
	 * If both futures throw exceptions, the last is passed to the returned future.
	 * 
	 * @param other other future
	 * @param fn function to receive result
	 * @param futuretype The type of the return future. If null, a default future is created.
	 * @return Future of the async function execution.
	 */
	public <U> IFuture<U> applyToEither(IFuture<E> other, Function<E,U> fn, Class<?> futuretype);
	
//	public <U> IFuture<U> applyToEitherAsync(IFuture<? extends E> other, Function<? super E,IFuture<U>> fn);
	
	/**
	 * The given consumer will be executed with either of the result of this and the given other future.
	 * The returned Future will receive the result of the function execution.
	 * If both futures return results, the first is used to call the function.
	 * If both futures throw exceptions, the last is passed to the returned future.
	 * 
	 * @param other other future
	 * @param fn function to receive result
	 * @param futuretype The type of the return future. If null, a default future is created.
	 * @return Future of the async function execution.
	 */
	public IFuture<Void> acceptEither(IFuture<E> other, Consumer<E> action, Class<?> futuretype);
	
	
	/**
	 *  Sequential execution of async methods via implicit delegation.
	 *  @param function Function that takes the result of this future as input and delivers future(t). 
	 *  @param ret The 
	 *  @return Future of the result of the second async call (=ret).
	 */
//	public <T> IFuture<T> thenApplyAndDelegate(final Function<E, IFuture<T>> function, Class<?> futuretype, final Future<T> ret);
}
