package jadex.micro.testcases.seqfuture;

import jadex.commons.future.IIntermediateFuture;

import java.util.NoSuchElementException;

/**
 * 
 */
public interface ISequenceFuture<E, F> extends IIntermediateFuture<Object>
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
}
