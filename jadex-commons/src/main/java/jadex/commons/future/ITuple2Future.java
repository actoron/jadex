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
