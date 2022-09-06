package jadex.commons.future;

import java.util.ArrayList;
import java.util.List;

/**
 *  Handler for common code of (non) intermediate terminable delegation future.
 *  Workaround for missing multiple inheritance.
 */
public class TerminableDelegationFutureHandler<E>
{
	//-------- attributes --------
	
	/** The termination source. */
	protected ITerminableFuture<E> src;
	
	/** Flag if source has to be terminated. */
	protected boolean terminate;
	
	/** Flag if source has been terminated. */
	protected boolean terminated;
//	Throwable tex;	// for debugging double termination

	/** Exception used for termination. */
	protected Exception reason;
	
	/** The list of stored backward commands, to be sent when src is connected. */ 
	protected List<Object> storedcmds;
	
	/** The number of intermediate pulls to be performed, when the source is connected. */
	protected int	pullcnt;

	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFutureHandler()
	{
	}
		
	//-------- methods --------
	
	/**
	 *  Set the termination source.
	 */
	public void setTerminationSource(ITerminableFuture<E> src)
	{
		boolean myterminate;
		int	mypull;
		List<Object> mycmds;
		
		synchronized(this)
		{
			if(this.src!=null)
				throw new IllegalStateException("Source already set: "+this);
			// Notify when someone has called terminate (notify is set)
			// src is set and not already notified
			this.src = src;
			myterminate = terminate && !terminated;
			terminated = terminated || myterminate;
			mypull	= pullcnt;
			pullcnt	= 0;
			mycmds	= storedcmds;
			storedcmds	= null;
		}
		
		if(myterminate)
		{
//			tex=new RuntimeException().fillInStackTrace();
			src.terminate(reason);
		}
		
		// TODO: Send commands/pulls only if not terminated?
		else
		{
			if(mypull>0)
			{
				IPullIntermediateFuture<?>	pull	= (IPullIntermediateFuture<?>)src;
				for(; mypull>0; mypull--)
				{
					pull.pullIntermediateResult();
				}
			}
			
			if(mycmds!=null)
			{
				for(Object info: mycmds)
				{
					src.sendBackwardCommand(info);
				}
			}
		}
	}
	
	/**
	 *  Terminate the future.
	 *  The exception will be set to FutureTerminatedException.
	 */
	public void terminate()
	{
		terminate(new FutureTerminatedException());
	}
	
	/**
	 *  Terminate the future and supply a custom reason.
	 */
	public void terminate(Exception reason)
	{
		boolean mynotify;
		synchronized(this)
		{
			if(terminated||terminate)
				throw new IllegalStateException("Already terminated: "+this);//, tex);
			this.terminate	= true;
			this.reason	= reason;
			mynotify = src!=null;
			terminated = mynotify;
		}
		
		
		if(mynotify)
		{
//			tex=new RuntimeException().fillInStackTrace();
			src.terminate(reason);
		}
	}
	
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult()
	{
		int	mypull	= 0;
		synchronized(this)
		{
			pullcnt++;
			if(src!=null)
			{
				mypull	= pullcnt;
				pullcnt	= 0;
			}
		}
		
		if(mypull>0)
		{
			IPullIntermediateFuture<?>	pull	= (IPullIntermediateFuture<?>)src;
			for(; mypull>0; mypull--)
			{
				pull.pullIntermediateResult();
			}
		}
	}
	
	/**
	 *  Send a backward command in direction of the source.
	 *  @param info The command info.
	 */
	public void sendBackwardCommand(Object info)
	{
		boolean	send;
		synchronized(this)
		{				
			send	= src!=null;
			if(!send)
			{
				if(storedcmds==null)
					storedcmds = new ArrayList<Object>();
				storedcmds.add(info);
			}
		}
		
		if(send)
		{
			src.sendBackwardCommand(info);
		}
	}
}
