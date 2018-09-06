package jadex.commons.gui.future;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;


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
	
	/** Future for clock advancement blocking. */
	protected Future<Void>	adblock;
	
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
		
		adblock	= SwingDefaultResultListener.block();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	final public void	resultAvailable(final E result)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			try
			{
				customResultAvailable(result);
			}
			finally
			{
				SwingDefaultResultListener.unblock(adblock);
			}
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						customResultAvailable(result);
					}
					finally
					{
						SwingDefaultResultListener.unblock(adblock);
					}
				}
			});
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	final public void	exceptionOccurred(final Exception exception)
	{
//		exception.printStackTrace();
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			try
			{
				customExceptionOccurred(exception);			
			}
			finally
			{
				SwingDefaultResultListener.unblock(adblock);
			}
		}
		else
		{
//			Thread.dumpStack();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						customExceptionOccurred(exception);			
					}
					finally
					{
						SwingDefaultResultListener.unblock(adblock);
					}
				}
			});
		}
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
