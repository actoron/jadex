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
	public static final IntermediateFuture<Void>	DONE	= new IntermediateFuture<Void>((Collection)null);

	
    /**
     *  Get the intermediate results that are available.
     *  @return The future result.
     */
    public Collection<E> getIntermediateResults();
}
