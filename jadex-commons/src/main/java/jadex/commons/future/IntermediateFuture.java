package jadex.commons.future;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  Default implementation of an intermediate future.
 */
public class IntermediateFuture extends Future	implements	IIntermediateFuture
{
	//-------- attributes --------
	
	/** The intermediate results. */
	protected Collection results;
	
	/** Flag indicating that addIntermediateResult()has been called. */
	protected boolean intermediate;
	
	//-------- constructors--------
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public IntermediateFuture()
	{
	}
	
	/**
	 *  Create a future that is already done.
	 *  @param result	The result, if any.
	 */
	public IntermediateFuture(Collection results)
	{
		super(results);
	}
	
	//-------- IIntermediateFuture interface --------
		
    /**
     *  Get the intermediate results that are available.
     *  @return The current intermediate results (copy of the list).
     */
	public synchronized Collection getIntermediateResults()
	{
		return results!=null ? new ArrayList(results) : Collections.emptyList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add an intermediate result.
	 */
	public void	addIntermediateResult(Object result)
	{
	   	synchronized(this)
		{
        	if(resultavailable)
        	{
        		first.printStackTrace();
        		throw new RuntimeException("Duplicate result: "+result+", "+this.result);
        	}
        }
	   	
		intermediate = true;
		
		List	ilisteners	= null;
		synchronized(this)
		{
			if(results==null)
				results	= new ArrayList();
			
			results.add(result);
			
			if(listeners!=null)
			{
				// Find intermediate listeners to be notified.
				for(int i=0; i<listeners.size(); i++)
				{
					if(listeners.get(i) instanceof IIntermediateResultListener)
					{
						if(ilisteners==null)
							ilisteners	= new ArrayList();
						ilisteners.add(listeners.get(i));
					}
				}
			}
		}

		for(int i=0; ilisteners!=null && i<ilisteners.size(); i++)
		{
			try
			{
				((IIntermediateResultListener)ilisteners.get(i)).intermediateResultAvailable(result);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
	Exception first;
    public void	setResult(Object result)
    {
    	boolean ex = false;
    	synchronized(this)
		{
    		if(results!=null)
    			ex = true;
		}
    	if(ex)
    	{
    		throw new RuntimeException("setResult() only allowed without intermediate results:"+results);
    	}
    	else
    	{
    		if(result!=null && !(result instanceof Collection))
    		{
    			throw new IllegalArgumentException("Result must be collection: "+result);
    		}
    		else
    		{
    			first = new RuntimeException();
    			if(result!=null)
    				this.results = (Collection)result;
    			super.setResult(results);
    		}
    	}
    }
    
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setResultIfUndone(Object result)
    {
    	boolean ex = false;
    	synchronized(this)
		{
    		if(results!=null)
    			ex = true;
		}
    	if(ex)
    	{
    		throw new RuntimeException("setResultIfUndone() only allowed without intermediate results:"+results);
    	}
    	else
    	{
    		if(result!=null && !(result instanceof Collection))
    		{
    			throw new IllegalArgumentException("Result must be collection: "+result);
    		}
    		else
    		{
    			this.results = (Collection)result;
    			super.setResultIfUndone(result);
    		}
    	}
    }
    
    /**
     *  Declare that the future is finished.
     */
    public void setFinished()
    {
    	Collection	res;
    	synchronized(this)
    	{
    		res	= getIntermediateResults();
			// Hack!!! Set results to avoid inconsistencies between super.result and this.results,
    		// because getIntermediateResults() returns empty list when results==null.
    		if(results==null)
    		{
    			results	= res;
    		}
    	}
    	super.setResult(res);
    }
    
    /**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener listener)
    {
    	if(listener==null)
    		throw new RuntimeException();
    	
    	boolean	notify	= false;
    	Object[] inter = null;
    	synchronized(this)
    	{
	    	if(resultavailable)
	    	{
	    		notify	= true;
	    	}
	    	
    		if(listener instanceof IIntermediateResultListener && results!=null)
    		{
    			inter = results.toArray();
    		}
    		
    		if(listeners==null)
    			listeners	= new ArrayList();
    		listeners.add(listener);
    	}
    	
    	if(intermediate && inter!=null)
    	{
    		IIntermediateResultListener lis =(IIntermediateResultListener)listener;
    		for(int i=0; i<inter.length; i++)
    		{
    			lis.intermediateResultAvailable(inter[i]);
    		}
    	}
    	if(notify)
    	{
    		notifyListener(listener);
    	}
    		
    }
    
    /**
     *  Notify a result listener.
     *  @param listener The listener.
     */
    protected void notifyListener(IResultListener listener)
    {
    	try
    	{
			if(exception!=null)
			{
				listener.exceptionOccurred(exception);
			}
			else
			{
				if(listener instanceof IIntermediateResultListener)
				{
					IIntermediateResultListener lis = (IIntermediateResultListener)listener;
					Object[] inter = null;
					synchronized(this)
					{
						if(!intermediate && results!=null)
						{
							inter = results.toArray();
						}
					}
					if(inter!=null)
			    	{
			    		for(int i=0; i<inter.length; i++)
			    		{
			    			lis.intermediateResultAvailable(inter[i]);
			    		}
			    	}
					lis.finished();
				}
				else
				{
					listener.resultAvailable(results); 
				}
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
