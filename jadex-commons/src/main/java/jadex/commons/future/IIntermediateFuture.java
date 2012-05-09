package jadex.commons.future;


import java.util.Collection;

/**
 *  Future that support intermediate results.
 */
//@Reference
public interface IIntermediateFuture<E> extends IFuture<Collection <E>>
{
	// -------- constants --------

	/**
	 *  A future representing a completed action. Can be used as direct return
	 *  value of methods that do not perform asynchronous operations and do not
	 *  return a result value.
	 */
	public static final IntermediateFuture<Void>	DONE	= new IntermediateFuture<Void>((Collection<Void>)null);

	
    /**
     *  Get the intermediate results that are currently available.
     *  Non-blocking method.
     *  @return The future result.
     */
    public Collection<E> getIntermediateResults();

	
    /**
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning true.
     *  
     *  @param caller	The caller that wants to iterate over intermediate results.
     *  @return	True, when there are more intermediate results for the caller.
     */
    public boolean hasNextIntermediateResult(ISuspendable caller);
	
    /**
     *  Iterate over the intermediate results in a blocking fashion.
     *  Manages results independently for different callers, i.e. when called
     *  with different suspendables, each suspendable receives all intermediate results.
     *  
     *  @param caller	The caller that wants to iterate over intermediate results.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getNextIntermediateResult(ISuspendable caller);
}
