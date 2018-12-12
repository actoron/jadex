package jadex.commons.gui.future;

import java.util.logging.Logger;

import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;
import jadex.commons.gui.SGUI;


/**
 *  Listener that performs notifications on swing thread..
 */
public class SwingResultListener<E> implements IUndoneResultListener<E>, IFutureCommandResultListener<E>
{
	//-------- attributes --------
	
	/** The delegation listener. */
	protected IResultListener<E> listener;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;
	
	//-------- constructors --------

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param listener The listener.
	 * @param exceptionListener The listener that is called on exceptions.
	 */
	public SwingResultListener(final IFunctionalResultListener<E> listener)
	{
		this(listener, null);
	}
	
	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param listener The listener.
	 * @param exceptionListener The listener that is called on exceptions.
	 */
	public SwingResultListener(final IFunctionalResultListener<E> listener, final IFunctionalExceptionListener exceptionListener)
	{
		this(new DefaultResultListener<E>()
		{
			public void resultAvailable(E result)
			{
				listener.resultAvailable(result);
			}
			public void exceptionOccurred(Exception exception)
			{
				if (exceptionListener != null) {
					exceptionListener.exceptionOccurred(exception);
				} else {
					super.exceptionOccurred(exception);
				}
			}
		});
	}
	
	/**
	 *  Create a new listener.
	 */
	public SwingResultListener(final IResultListener<E> listener)
	{
		this.listener = listener;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	final public void	resultAvailable(final E result)
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
	 * @param exception The exception.
	 */
	final public void	exceptionOccurred(final Exception exception)
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
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void	customResultAvailable(E result)
	{
		if(undone && listener instanceof IUndoneResultListener)
		{
			((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
		}
		else
		{
			listener.resultAvailable(result);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void	customExceptionOccurred(Exception exception)
	{
		if(undone && listener instanceof IUndoneResultListener<?>)
		{
			((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
		}
		else
		{
			listener.exceptionOccurred(exception);
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
	public void resultAvailableIfUndone(E result)
	{
		undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		undone = true;
		exceptionOccurred(exception);
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
