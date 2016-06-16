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
	 *  @deprecated - From 3.0. Use method without suspendable. 
	 *  Will NOT use the suspendable that is supplied as parameter.
     *  Get the first result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult(ThreadSuspendable sus)
    {
    	return (E)getXResult(0);
    }
	
	/**
     *  Get the first result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getFirstResult()
    {
    	return (E)getXResult(0);
    }
    
    /**
     *  Get the second result.
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public F getSecondResult()
    {
    	return (F)getXResult(1);
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
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setFirstResultIfUndone(E result)
    {
    	setXResultIfUndone(0, result);
    }
    
    /**
     * Uses two functional result listeners to create a Tuple2ResultListener and add it.
     * The first listener is called upon reception of the first result, the second is called
     * for the second result.
     * Exceptions will be logged to console.
     * 
     * @param firstListener Listener for the first available result.
     * @param secondListener Listener for the second available result.
     */
	public void addTuple2ResultListener(IFunctionalResultListener<E> firstListener, IFunctionalResultListener<F> secondListener)
	{
		addTuple2ResultListener(firstListener, secondListener, null);
	}

    /**
     * Uses two functional result listeners to create a Tuple2ResultListener and add it.
     * The first listener is called upon reception of the first result, the second is called
     * for the second result.
     * 
     * Additionally, a given exception listener is called when exceptions occur.
     * 
     * @param firstListener Listener for the first available result.
     * @param secondListener Listener for the second available result.
	 * @param exListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
     */
	public void addTuple2ResultListener(final IFunctionalResultListener<E> firstListener, final IFunctionalResultListener<F> secondListener, IFunctionalExceptionListener exceptionListener)
	{
		final IFunctionalExceptionListener innerExceptionListener = (exceptionListener == null) ? SResultListener.printExceptions() : exceptionListener;
		addResultListener(new DefaultTuple2ResultListener<E, F>() {

			public void firstResultAvailable(E result) {
				firstListener.resultAvailable(result);
			}

			public void secondResultAvailable(F result) {
				secondListener.resultAvailable(result);
			}

			public void exceptionOccurred(Exception exception) {
				innerExceptionListener.exceptionOccurred(exception);
			}
		});
	}

	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setSecondResultIfUndone(F result)
    {
    	setXResultIfUndone(1, result);
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
     *  Set the xth result.
     */
    protected void setXResultIfUndone(int idx, Object res)
    {
    	addIntermediateResultIfUndone(new TupleResult(idx, res));
    	if(results.size()==getMax())
    		setFinishedIfUndone();
    }
    
    /**  
	 *  Get the x result.
	 *  @return	The next intermediate result.
	 *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
	 */
    protected Object getXResult(int idx)
    {
    	TupleResult res = null;
    	for(int i=0; i<getMax() && res==null; i++)
    	{
    		res = findResult(idx);
    		if(res==null)
    		{
    			res = getNextIntermediateResult();
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

