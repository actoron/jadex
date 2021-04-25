package jadex.commons.gui.future;

import java.util.Collection;

import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IUndoneIntermediateResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.gui.SGUI;

/**
 *  Exception delegation listener for intermediate results called back on swing thread.
 */
public class SwingIntermediateDelegationResultListener<E> implements IIntermediateFutureCommandResultListener<E>, IUndoneIntermediateResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected IntermediateFuture<E> future;
	
	/** The undone flag. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingIntermediateDelegationResultListener(IntermediateFuture<E> future)
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
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customResultAvailable(result);
			}
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public final void exceptionOccurred(final Exception exception)
	{
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customExceptionOccurred(exception);
			}
		});
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public final void intermediateResultAvailable(final E result)
	{
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customIntermediateResultAvailable(result);
			}
		});
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
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customFinished();
			}
		});
    }
    
    /**
   	 *  Declare that the future result count is available.
   	 *  This method is only called for intermediate futures,
   	 *  i.e. when this method is called it is guaranteed that the
   	 *  intermediateResultAvailable method will be called as
   	 *  often as the result count indicates except an exception occurs.
   	 */
    public void maxResultCountAvailable(int max) 
    {
    	SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				future.setMaxResultCount(max);
			}
		});
    }
	
	/**
     *  Declare that the future is finished.
     */
    public void customFinished()
    {
    	if(undone)
		{
    		future.setFinishedIfUndone();
		}
    	else
    	{
    		future.setFinished();
    	}
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		if(undone)
		{
			future.setResultIfUndone(result);
		}
		else
		{
			future.setResult(result);
		}
	}

	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
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
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void customIntermediateResultAvailable(E result)
	{
		if(undone)
		{
			future.addIntermediateResultIfUndone(result);
		}
		else
		{
			future.addIntermediateResult(result);
		}
	}
	/**
	 *  Called when a command is available.
	 */
	final public void commandAvailable(final Object command)
	{
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customCommandAvailable(command);
			}
		});
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
