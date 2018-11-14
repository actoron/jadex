package jadex.commons.future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *  A listener that collects intermediate results and calls resultAvailable() on setFinished().
 */
public abstract class CollectingIntermediateResultListener<E> implements IIntermediateResultListener<E>
{
	//-------- attributes --------
	
	/** The results. */
	protected Collection<E>	results;
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The final result.
	 */
	public abstract void resultAvailable(Collection<E> result);
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(E result)
	{
		if(results==null)
		{
			results	= new ArrayList<E>();
		}
		results.add(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finished()
    {
    	Collection<E>	results;
    	if(this.results!=null)
    	{
    		results	= this.results;
    	}
    	else
    	{
    		results	= Collections.emptyList();
    	}
    	resultAvailable(results);
    }

	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public abstract void exceptionOccurred(Exception exception);
}
