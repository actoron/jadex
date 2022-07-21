package jadex.commons.future;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;

/**
 *  A terminable intermediate delegation future can be used when a termination intermediate future 
 *  should be delegated. This kind of future needs to be connected to the
 *  termination source (another delegation or a real future). Termination
 *  calls are forwarded to the termination source. The future remembers
 *  when terminate() was called in unconnected state and forwards the request
 *  as soon as the connection is established.
 */
public class TerminableIntermediateDelegationFuture<E> extends IntermediateFuture<E>
	implements ITerminableIntermediateFuture<E>
{
	//-------- attributes --------
	
	/** The termination source. */
	protected ITerminableIntermediateFuture<?> src;
	
	/** Flag if source has to be notified. */
	protected boolean notify;
	
	/** Flag if source has been notified. */
	protected boolean notified;

	/** Exception used for notification. */
	protected Exception reason;
	
	/** The list of stored infos, to be sent when src is connected. */ 
	protected List<Object> storedinfos;
	
	protected Exception ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public TerminableIntermediateDelegationFuture()
	{
		ex = new RuntimeException();
	}
	
	/**
	 *  Create a new future.
	 */
	public TerminableIntermediateDelegationFuture(ITerminableIntermediateFuture<?> src)
	{
		ex = new RuntimeException();
		src.addResultListener(new TerminableIntermediateDelegationResultListener(this, src));
	}
	
	//-------- methods --------
	
	/**
	 *  Set the source.
	 */
	public void setSource(ITerminableIntermediateFuture<?> src)
	{
		assert this.src==null;
	
		this.src = src;
		
		doNotify();
	}
	
	/**
	 *  Get the src.
	 *  @return The src.
	 */
	public ITerminableIntermediateFuture<?> getSource()
	{
		return src;
	}

	/**
	 *  Possibly notify the termination source.
	 */
	protected void doNotify()
	{
		boolean mynotify;
		synchronized(this)
		{
			// Notify when someone has called terminate (notify is set)
			// src is set and not already notified
			mynotify = notify && !notified;
			notified = notified || mynotify;
		}
		
		if(mynotify)
			src.terminate(reason);
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
		
		if(src==null)
		{
			System.out.println("ERROR, delegation future without source: "+this);
			//ex.printStackTrace();
		}
		
		if(mynotify)
		{
			//System.out.println("terminate forwarded: "+this+" "+src+" "+getTerminationCommand());
			src.terminate(reason);
		}
		else
		{
			System.out.println("terminate not forwarded: "+this+" "+src+" "+getTerminationCommand());
			ex.printStackTrace();
			//System.out.println("terminate not forwarded: "+notified+" "+this+" "+src);
		}
	
		// TODO: why stored infos after terminate? -> should be done in set source??? 
//		if(storedinfos!=null)
//		{
//			for(Object info: storedinfos)
//			{
//				src.sendBackwardCommand(info);
//			}
//			storedinfos = null;
//		}
	}
	
	/**
	 * 
	 * /
	protected void printSourceDetails()
	{
		try
		{
			Object p = src;
			
			while(p!=null)
			{
				Field f = SReflect.getField(p.getClass(), "src");
				if(f!=null)
				{
					Object nextp = f.get(p);
					if(nextp==null)
						break;
					else
						p = nextp;
				}
				else
				{
					break;
				}
			}
			
			Object term = null;
			if(p!=null)
			{
				Field f = SReflect.getField(p.getClass(), "terminate");
				if(f!=null)
					term = f.get(p);
			}
			
			System.out.println("Source infos of terminable: "+this+" term="+term+" p="+p+" src="+src);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 
	 */
	protected Object getTerminationCommand()
	{
		try
		{
			Object p = src;
			
			while(p!=null)
			{
				Field f = SReflect.getField(p.getClass(), "src");
				if(f!=null)
				{
					Object nextp = f.get(p);
					if(nextp==null)
						break;
					else
						p = nextp;
				}
				else
				{
					break;
				}
			}
			
			Object term = null;
			if(p!=null)
			{
				Field f = SReflect.getField(p.getClass(), "terminate");
				if(f!=null)
					term = f.get(p);
			}
			
			return term;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
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
	
//	/**
//	 *  Test if future is terminated.
//	 *  @return True, if terminated.
//	 */
//	public boolean isTerminated()
//	{
//		return isDone() && exception instanceof FutureTerminatedException;
//	}
}

