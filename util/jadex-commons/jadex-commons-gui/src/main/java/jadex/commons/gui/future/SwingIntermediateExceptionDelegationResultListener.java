package jadex.commons.gui.future;

import java.util.Collection;

import javax.swing.SwingUtilities;

import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IUndoneIntermediateResultListener;

/**
 *  Exception delegation listener for intermediate results called back on swing thread.
 */
public abstract class SwingIntermediateExceptionDelegationResultListener<E, T> implements IIntermediateFutureCommandResultListener<E>, IUndoneIntermediateResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<T> future;
	
	/** The undone flag. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingIntermediateExceptionDelegationResultListener(Future<T> future)
	{
		this.future = future;
//		this.ex	= new DebugException();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public final void resultAvailable(final Collection<E> result)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
		{
			customResultAvailable(result);			
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customResultAvailable(result);
				}
			});
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public final void exceptionOccurred(final Exception exception)
	{
//		exception.printStackTrace();
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			customExceptionOccurred(exception);			
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customExceptionOccurred(exception);
				}
			});
		}
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public final void intermediateResultAvailable(final E result)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
		{
			customIntermediateResultAvailable(result);			
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customIntermediateResultAvailable(result);
				}
			});
		}
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public final void finished()
    {
    	// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
		{
			customFinished();
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customFinished();
				}
			});
		}
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		for(E e: result)
		{
			customIntermediateResultAvailable(e);
		}
		customFinished();
	}

	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public abstract void customIntermediateResultAvailable(E result);

	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
//		System.err.println("Problem: "+exception);
		if(undone)
		{
			future.setExceptionIfUndone(exception);
		}
		else
		{
			future.setException(exception);
		}
	}
	
	/**
	 *  Called when finished.
	 */
	public abstract void customFinished();
	
	/**
	 *  Called when a command is available.
	 */
	final public void commandAvailable(final Object command)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			customCommandAvailable(command);			
		}
		else
		{
//			Thread.dumpStack();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customCommandAvailable(command);
				}
			});
		}
	}
	
	/**
	 *  Called when a command is available.
	 */
	public void	customCommandAvailable(Object command)
	{
		future.sendForwardCommand(command);
	}

	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(Collection<E> result)
	{
		this.undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		this.undone = true;
		exceptionOccurred(exception);
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result)
	{
		this.undone = true;
		intermediateResultAvailable(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finishedIfUndone()
    {
    	this.undone = true;
    	finished();
    }

	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
}
