/**
 * 
 */
package jadex.commons.gui.future;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Logger;

import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IUndoneIntermediateResultListener;
import jadex.commons.future.IUndoneResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;

/**
 *
 */
public class SwingIntermediateResultListener<E> implements IIntermediateFutureCommandResultListener<E>, IUndoneIntermediateResultListener<E>
{
	//-------- attributes --------

	/** The delegation listener. */
	protected IIntermediateResultListener<E> listener;
	
	/** The undone flag. */
	protected boolean undone;
	
//	/** Future for clock advancement blocking. */
//	protected Future<Void>	adblock;
	
	//-------- constructors --------

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param intermediateListener The intermediate listener.
	 */
	public SwingIntermediateResultListener(final Consumer<E> intermediateListener)
	{
		this(intermediateListener, null);
	}
	
	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param ilistener The intermediate listener.
	 * @param flistener The finished listener, called when no more
	 *        intermediate results will arrive.
	 */
	public SwingIntermediateResultListener(final Consumer<E> ilistener, final Consumer<Void> flistener)
	{
		this(ilistener, flistener, null, null);
	}
	
	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param ilistener The intermediate listener.
	 * @param flistener The finished listener, called when no more
	 *        intermediate results will arrive.
	 * @param elistener The listener that is called on exceptions.
	 */
	public SwingIntermediateResultListener(final Consumer<E> ilistener, final Consumer<Void> flistener, 
		final Consumer<Exception> elistener, final Consumer<Integer> clistener)
	{
		this(new IntermediateDefaultResultListener<E>()
		{
			public void intermediateResultAvailable(E result)
			{
				ilistener.accept(result);
			}
			public void finished()
			{
				if(flistener != null) 
					flistener.accept(null);
			}
			public void exceptionOccurred(Exception exception)
			{
				if(elistener != null) 
				{
					elistener.accept(exception);
				} 
				else 
				{
					super.exceptionOccurred(exception);
				}
			}
			public void maxResultCountAvailable(int max) 
			{
				if(clistener!=null)
					clistener.accept(max);
			}
		});
	}
	
	/**
	 *  Create a new listener.
	 */
	public SwingIntermediateResultListener(final IIntermediateResultListener<E> listener)
	{
		this.listener = listener;
		
//		adblock	= SSimulation.block();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  This method is only called for non-intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method has not been called.
	 *  @param result The final result.
	 */
	public void resultAvailable(final Collection<E> result)
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
	public void exceptionOccurred(final Exception exception)
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
	public void intermediateResultAvailable(final E result)
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
    public void finished()
    {
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customFinished();
			}
		});
    }
    
    public void maxResultCountAvailable(int max) 
    {
    	SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				listener.maxResultCountAvailable(max);
			}
		});
    }
	
    /**
     *  Declare that the future is finished.
     */
    public void customFinished()
    {
    	if(undone && listener instanceof IUndoneIntermediateResultListener)
    	{
    		((IUndoneIntermediateResultListener<E>)listener).finishedIfUndone();
    	}
    	else
    	{
    		listener.finished();
    	}
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		if(undone && listener instanceof IUndoneResultListener)
    	{
    		((IUndoneResultListener<Collection<E>>)listener).resultAvailableIfUndone(result);
    	}
    	else
    	{
    		listener.resultAvailable(result);
    	}
	}

	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		if(undone && listener instanceof IUndoneResultListener)
    	{
    		((IUndoneResultListener<Collection<E>>)listener).exceptionOccurredIfUndone(exception);
    	}
    	else
    	{
    		listener.exceptionOccurred(exception);
    	}
	}
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void customIntermediateResultAvailable(E result)
	{
		if(undone && listener instanceof IUndoneIntermediateResultListener)
    	{
    		((IUndoneIntermediateResultListener<E>)listener).intermediateResultAvailableIfUndone(result);
    	}
    	else
    	{
    		listener.intermediateResultAvailable(result);
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
		if(listener instanceof IFutureCommandResultListener)
		{
			((IFutureCommandResultListener<?>)listener).commandAvailable(command);
		}
		else
		{
			Logger.getLogger("swing-result-listener").fine("Cannot forward command: "+listener+" "+command);
		}
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
