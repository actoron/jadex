package jadex.commons.future;

import java.util.Collection;


/**
 * 
 */
public class SubscriptionIntermediateDelegationFuture<E> extends TerminableIntermediateDelegationFuture<E>
	implements ISubscriptionIntermediateFuture<E>
{
	//-------- attributes --------
	
    /** Flag if results should be stored till first listener is. */
    protected boolean storeforfirst;
	
	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateDelegationFuture()
	{
		super();
		storeforfirst = true;
	}
	
	/**
	 *  Create a new future.
	 */
	public SubscriptionIntermediateDelegationFuture(ITerminableIntermediateFuture<?> src)
	{
		super(src);
		storeforfirst = true;
	}
	
	//-------- methods --------
	
	/**
	 *  Don't store results.
	 */
	protected void addResult(E result)
	{
		if(storeforfirst && (listeners==null || listeners.size()==0))
			super.addResult(result);
	}
	
	/**
     *  Add a result listener.
     *  @param listsner The listener.
     */
    public void	addResultListener(IResultListener<Collection<E>> listener)
    {
//    	System.out.println("adding listener: "+listener);
    	boolean first;
    	synchronized(this)
		{
			first = listeners==null || listeners.size()==0;
		}
    	super.addResultListener(listener);
    	
    	synchronized(this)
		{
			if(first)
				results=null;
		}
    }
}
