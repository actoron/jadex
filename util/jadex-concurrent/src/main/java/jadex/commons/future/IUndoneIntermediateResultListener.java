package jadex.commons.future;

import java.util.Collection;

/**
 * 
 */
public interface IUndoneIntermediateResultListener<E> extends IUndoneResultListener<Collection <E>>
{
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result);
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finishedIfUndone();
}
