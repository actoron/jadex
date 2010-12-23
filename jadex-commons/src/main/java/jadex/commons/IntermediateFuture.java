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
	protected List	results;
	
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
			if(results==null)
				results	= new ArrayList();
			
			results.add(result);
		}
		
		// Todo: notify listeners
	}
}
