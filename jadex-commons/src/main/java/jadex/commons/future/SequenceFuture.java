package jadex.commons.future;


import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 */
public class SequenceFuture<E,F> extends IntermediateFuture<Object> implements ISequenceFuture<E, F>
{
	//-------- constructors--------
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public SequenceFuture()
	{
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public SequenceFuture(E result1, F result2)
	{
		super(Arrays.asList(new Object[]{result1, result2}));
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public SequenceFuture(Exception exception)
	{
		super(exception);
	}
	
	/**
     *  Get the first result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult()
    {
    	if(results!=null && results.size()>0)
    	{
    		return (E)results.iterator().next();
    	}
    	else
    	{
    		return (E)getNextIntermediateResult();
    	}
    }
    
    /**
     *  Get the second result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult()
    {
    	if(results!=null && results.size()>1)
    	{
    		Iterator<Object> it = results.iterator(); 
    		it.next();
    		return (F)it.next();
    	}
    	else
    	{
    		if(results==null || results.size()==0)
    			getNextIntermediateResult();
    		return (F)getNextIntermediateResult();
    	}
    }
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setFirstResult(E result)
    {
    	addIntermediateResult(result);
    }
    
    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setSecondResult(F result)
    {
    	addIntermediateResult(result);
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyIntermediateResult(IIntermediateResultListener<Object> listener, Object result)
    {
    	if(listener instanceof ISequenceResultListener)
    	{
    		ISequenceResultListener<E, F> lis = (ISequenceResultListener<E, F>)listener;
    		if(indexOf(result)==1)
    		{
    			lis.resultAvailable1((E)result);
    		}
    		else
    		{
    			lis.resultAvailable2((F)result);
    		}
    	}
    	else
    	{
    		listener.intermediateResultAvailable(result);
    	}
    }
    
    /**
     * 
     */
    protected int indexOf(Object result)
    {
    	Iterator<Object> it = results.iterator();
    	if(it.next().equals(result))
    	{
    		return 1;
    	}
    	else if(it.next().equals(result))
    	{	
    		return 2;
    	}
    	else
    	{
    		throw new RuntimeException();
    	}
    }
}
