package jadex.commons.gui.future;


import javax.swing.SwingUtilities;

import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IUndoneResultListener;

/**
 *  Delegation result listener that calls customResultAvailable and
 *  customExceptionOccurred on swing thread.
 */
public abstract class SwingExceptionDelegationResultListener<E, T> implements IUndoneResultListener<E>, IFutureCommandResultListener<E>
{
	//-------- attributes --------
	
	/** The future to which calls are delegated. */
	protected Future<T> future;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingExceptionDelegationResultListener(Future<T> future)
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
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
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
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public abstract void customResultAvailable(E result) throws Exception;
//	{
//		future.setResult(result);
//	}
	
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

