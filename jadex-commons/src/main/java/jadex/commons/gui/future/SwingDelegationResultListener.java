package jadex.commons.gui.future;


import jadex.commons.future.Future;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IUndoneResultListener;
import jadex.commons.gui.SGUI;

import javax.swing.SwingUtilities;

/**
 *  Delegation result listener that calls customResultAvailable and
 *  customExceptionOccurred on swing thread.
 */
public class SwingDelegationResultListener<E> implements IUndoneResultListener<E>, IFutureCommandResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<E> future;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingDelegationResultListener(Future<E> future)
	{
		this.future = future;
	}
	
	//-------- IResultListener --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	final public void resultAvailable(final E result)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SGUI.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			try
			{
				customResultAvailable(result);
			}
			catch(Exception e)
			{
				// Could happen that overridden customResultAvailable method
				// first sets result and then throws exception (listener ex are catched).
				future.setExceptionIfUndone(e);
//				if(undone)
//				{
//					future.setExceptionIfUndone(e);
//				}
//				else
//				{
//					future.setException(e);
//				}
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
					catch(Exception e)
					{
						// Could happen that overridden customResultAvailable method
						// first sets result and then throws exception (listener ex are catched).
						future.setExceptionIfUndone(e);
//						if(undone)
//						{
//							future.setExceptionIfUndone(e);
//						}
//						else
//						{
//							future.setException(e);
//						}
					}
				}
			});
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	final public void exceptionOccurred(final Exception exception)
	{
//		exception.printStackTrace();
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SGUI.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
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
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(E result)
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
	 *  Called when a command is available.
	 */
	final public void commandAvailable(final Object command)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SGUI.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
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
		future.sendCommand(command);
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
