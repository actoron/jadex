package jadex.commons;

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
    		setException(new RuntimeException("setResult() only allowed without intermediate results:"+results));
    	}
    	else
    	{
    		if(result!=null && !(result instanceof Collection))
    		{
    			setException(new IllegalArgumentException("Result must be collection: "+result));
    		}
    		else
    		{
    			this.results = (Collection)result;
    			super.setResult(result);
    		}
    	}
    }
    
    /**
     *  Declare that the future is finished.
     */
    public void setFinished()
    {
    	super.setResult(getIntermediateResults());
    }
}
