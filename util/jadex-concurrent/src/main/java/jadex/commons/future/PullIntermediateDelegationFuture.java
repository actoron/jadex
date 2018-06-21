package jadex.commons.future;

/**
 *  Delegation future for pull future.
 */
public class PullIntermediateDelegationFuture<E> extends TerminableIntermediateDelegationFuture<E>
	implements IPullIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** Flag if source has to be notified. */
	protected int notifycnt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public PullIntermediateDelegationFuture()
	{
	}
	
	/**
	 *  Create a new future.
	 */
	public PullIntermediateDelegationFuture(IPullIntermediateFuture<?> src)
	{
		src.addResultListener(new TerminableIntermediateDelegationResultListener(this, src));
	}
	
	//-------- methods --------
	
	/**
	 *  Possibly notify the termination source.
	 */
	protected void doNotify()
	{
		super.doNotify();
		
		int mynotifycnt = 0;
		synchronized(this)
		{
			mynotifycnt = notifycnt;
			notifycnt = 0;
		}
		
		for(int i=0; i<mynotifycnt; i++)
			((IPullIntermediateFuture<E>)src).pullIntermediateResult();
	}
	
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult()
	{
		int mynotifycnt = 0;
		synchronized(this)
		{
			// Notify when someone has called terminate (notify is set)
			// src is set and not already notified
			notifycnt++;
			if(src!=null)
			{
				mynotifycnt = notifycnt;
				notifycnt = 0;
			}
		}
		
		for(int i=0; i<mynotifycnt; i++)
			((IPullIntermediateFuture<E>)src).pullIntermediateResult();
	}
}

