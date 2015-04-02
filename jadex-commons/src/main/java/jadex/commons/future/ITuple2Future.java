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
    
//	/**
//     *  Get the first result.
//     *  @return	The next intermediate result.
//     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
//     */
//    public E getFirstResult(ISuspendable caller);
    
    /**
     *  Get the second result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult();
    
//    /**
//     *  Get the second result.
//     *  @return	The next intermediate result.
//     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
//     */
//    public F getSecondResult(ISuspendable caller);
}
