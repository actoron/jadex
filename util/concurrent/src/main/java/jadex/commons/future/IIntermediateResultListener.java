package jadex.commons.future;

import java.util.Collection;


/**
 *  Result listener with additional notifications in case of intermediate results.
 */
//@Reference
public interface IIntermediateResultListener<E> extends IResultListener<Collection<E>>
{	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(E result);
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finished();
    
    /**
	 *  Declare that the future result count is available.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method will be called as
	 *  often as the result count indicates except an exception occurs.
	 */
	public void maxResultCountAvailable(int max);
}
