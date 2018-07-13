package jadex.commons.future;

import java.util.ArrayList;
import java.util.List;

/**
 *  A terminable delegation future can be used when a termination future 
 *  should be delegated. This kind of future needs to be connected to the
 *  termination source (another delegation or a real future). Termination
 *  calls are forwarded to the termination source. The future remembers
 *  when terminate() was called in unconnected state and forwards the request
 *  as soon as the connection is established.
 */
public class TerminableDelegationFuture<E> extends Future<E> implements ITerminableFuture<E>
{
	//-------- attributes --------
	
	/** The termination source. */
	protected ITerminableFuture<?> src;
	
	/** Flag if source has to be notified. */
	protected boolean notify;
	
	/** Flag if source has been notified. */
	protected boolean notified;

	/** Exception used for notification. */
	protected Exception reason;
	
	/** The list of stored infos, to be sent when src is connected. */ 
	protected List<Object> storedinfos;

	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFuture()
	{
//		System.out.println("tfut: "+hashCode());
	}
	
	/**
	 *  Create a new future.
	 */
	public TerminableDelegationFuture(ITerminableFuture<?> src)
	{
		if(this.getClass().getName().indexOf("DelegatingTerminableDelegationFuture")!=-1)
			System.out.println("func: "+hashCode());
		src.addResultListener(new TerminableDelegationResultListener(this, src));
	}
	
	//-------- methods --------
	
	/**
	 *  Set the termination source.
	 */
	public void setTerminationSource(ITerminableFuture<?> src)
	{
		assert this.src==null;
		
		boolean mynotify;
		synchronized(this)
		{
			// Notify when someone has called terminate (notify is set)
			// src is set and not already notified
			this.src = src;
			mynotify = notify && !notified;
			notified = notified || mynotify;
		}
		
		if(mynotify)
			src.terminate(reason);
		
		if(storedinfos!=null)
		{
			for(Object info: storedinfos)
			{
				src.sendBackwardCommand(info);
			}
			storedinfos = null;
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
			// Notify when src is set and not already notified
			// Remember to notify when src is not set and not already notified
			mynotify = src!=null && !notified;
			notify = src==null && !notified;
			notified = notified || mynotify;
			if(notify)
				this.reason	= reason;
		}
		
		if(mynotify)
			src.terminate(reason);
	}
	
//	/**
//	 *  Test if future is terminated.
//	 *  @return True, if terminated.
//	 */
//	public boolean isTerminated()
//	{
//		return isDone() && exception instanceof FutureTerminatedException;
//	}
	
	/**
	 *  Send a backward command in direction of the source.
	 *  @param info The command info.
	 */
	public void sendBackwardCommand(Object info)
	{
		if(src!=null)
		{
			src.sendBackwardCommand(info);
		}
		else
		{
			if(storedinfos==null)
				storedinfos = new ArrayList<Object>();
			storedinfos.add(info);
		}
	}
}

