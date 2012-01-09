package jadex.commons.future;

import java.util.Collection;


/**
 *  Result listener with additional notifications in case of intermediate results.
 */
//@Reference
public interface IIntermediateResultListener<E> extends IResultListener<Collection <E>>
{
	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public void resultAvailable(Collection<E> result);
	
//	/**
//	 *  Called when an exception occurred.
//	 *  @param exception The exception.
//	 */
//	public void exceptionOccurred(Exception exception);
	
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
}
