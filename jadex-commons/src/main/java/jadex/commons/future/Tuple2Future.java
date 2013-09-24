package jadex.commons.future;


import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 *  Implementation of tuple2 future.
 *  
 *  The future is considered as finished when all tuple elements have been set.
 */
public class Tuple2Future<E, F> extends IntermediateFuture<TupleResult> implements ITuple2Future<E, F>
{
	//-------- constructors--------
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public Tuple2Future()
	{
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public Tuple2Future(E result1, F result2)
	{
//		super(Arrays.asList(new Object[]{result1, result2}));
		super(Arrays.asList(new TupleResult[]{new TupleResult(0, result1), new TupleResult(1, result2)}));
	}
	
	/**
	 *  Create a future that is already done (failed).
	 *  @param exception	The exception.
	 */
	public Tuple2Future(Exception exception)
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
    	return getFirstResult(null);
    }
    
    /**
     *  Get the first result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult(ISuspendable caller)
    {
    	return (E)getXResult(0, caller);
    }
    
    /**
     *  Get the second result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult()
    {
    	return getSecondResult(null);
    }
    
    /**
     *  Get the second result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult(ISuspendable caller)
    {
    	return (F)getXResult(1, caller);
    }
    
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setFirstResult(E result)
    {
    	setXResult(0, result);
    }
    
    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setSecondResult(F result)
    {
    	setXResult(1, result);
    }
    
    /**
     *  Set the xth result.
     */
    protected void setXResult(int idx, Object res)
    {
    	addIntermediateResult(new TupleResult(idx, res));
    	if(results.size()==getMax())
    		setFinishedIfUndone();
    }
     
    /**  
	 *  Get the x result.
	 *  @return	The next intermediate result.
	 *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
	 */
    protected Object getXResult(int idx, ISuspendable sus)
    {
    	TupleResult res = null;
    	for(int i=0; i<getMax() && res==null; i++)
    	{
    		res = findResult(idx);
    		if(res==null)
    		{
    			res = getNextIntermediateResult(sus);
    			if(res.getNum()!=idx)
    				res = null;
    		}
    	}
   	
    	return res.getResult();
    }
   
	/**
	 *  Find result in results
	 */
	protected TupleResult findResult(int idx)
	{
		TupleResult ret = null;
		if(results!=null && results.size()>0)
		{
			for(TupleResult res: results)
			{
				if(res.getNum()==idx)
				{
					ret = res;
				}
			}
		}
		return ret;
	}
    
    /**
     *  Get the number of results of the type of future..
     */
    protected int getMax()
    {
    	return 2;
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyIntermediateResult(IIntermediateResultListener<TupleResult> listener, TupleResult result)
    {
    	if(listener instanceof ITuple2ResultListener)
    	{
    		ITuple2ResultListener<E, F> lis = (ITuple2ResultListener<E, F>)listener;
    		if(result.getNum()==0)
    		{
    			lis.firstResultAvailable((E)result.getResult());
    		}
    		else
    		{
    			lis.secondResultAvailable((F)result.getResult());
    		}
    	}
    	else
    	{
    		listener.intermediateResultAvailable(result);
    	}
    }
}

