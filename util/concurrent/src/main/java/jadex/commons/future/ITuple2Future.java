package jadex.commons.future;


import java.util.NoSuchElementException;

/**
 *  A tuple future has a defined number of results of possibly different types.
 *  
 *  The future is considered as finished when all tuple elements have been set.
 */
public interface ITuple2Future<E, F> extends IIntermediateFuture<TupleResult>
{
	/**
	 *  @deprecated - From 3.0. Use method without suspendable. 
     *  Get the first result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult(ThreadSuspendable sus);
	
	/**
     *  Get the first result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult();
    
    /**
     *  Get the second result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult();
    
    /**
     *  Get the first result.
     *  @param timeout The timeout in millis.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult(long timeout);
    
    /**
     *  Get the second result.
     *  @param timeout The timeout in millis.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult(long timeout);
    
    /**
     *  Get the first result.
     *  @param timeout The timeout in millis.
     *  @param realtime Flag if wait should be realtime (in contrast to simulation time).
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult(long timeout, boolean realtime);
    
    /**
     *  Get the second result.
     *  @param timeout The timeout in millis.
     *  @param realtime Flag if wait should be realtime (in contrast to simulation time).
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult(long timeout, boolean realtime);
    
    /**
     * Uses two functional result listeners to create a Tuple2ResultListener and add it.
     * The first listener is called upon reception of the first result, the second is called
     * for the second result.
     * Exceptions will be logged to console.
     * 
     * @param firstListener Listener for the first available result.
     * @param secondListener Listener for the second available result.
     */
    public void addTuple2ResultListener(IFunctionalResultListener<E> firstListener, IFunctionalResultListener<F> secondListener);
    
    /**
     * Uses two functional result listeners to create a Tuple2ResultListener and add it.
     * The first listener is called upon reception of the first result, the second is called
     * for the second result.
     * 
     * Additionally, a given exception listener is called when exceptions occur.
     * 
     * @param firstListener Listener for the first available result.
     * @param secondListener Listener for the second available result.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
     */
    public void addTuple2ResultListener(IFunctionalResultListener<E> firstListener, IFunctionalResultListener<F> secondListener, IFunctionalExceptionListener exceptionListener);
}
