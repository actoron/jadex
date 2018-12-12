package jadex.commons.gui.future;


import javax.swing.SwingUtilities;

import jadex.bridge.service.types.simulation.SSimulation;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IUndoneResultListener;
import jadex.commons.gui.SGUI;

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
	
	/** Custom result listener */
	protected IFunctionalResultListener<E>	customResultListener;
	
	/** Custom result listener */
	protected IFunctionalExceptionListener	customExceptionListener;
	
	//-------- constructors --------
	
	/**
	 * Create a new listener with functional interfaces.
	 * @param fut The Delegate.
	 * @param customResultListener The listener.
	 */
	public SwingDelegationResultListener(Future<E> fut, final IFunctionalResultListener<E> customResultListener)
	{
		this(fut, customResultListener, null);
	}

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param fut The Delegate.
	 * @param customResultListener The custom result listener.
	 * @param customExceptionListener The listener that is called on exceptions.
	 */
	public SwingDelegationResultListener(Future<E> fut, final IFunctionalResultListener<E> customResultListener, IFunctionalExceptionListener customExceptionListener)
	{
		this(fut);
		this.customResultListener = customResultListener;
		this.customExceptionListener = customExceptionListener;
	}

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
		SGUI.invokeLaterSimBlock(new Runnable()
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
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	final public void exceptionOccurred(final Exception exception)
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
	public void customResultAvailable(E result)
	{
		if (customResultListener != null) {
			customResultListener.resultAvailable(result);
		} else {
			if(undone)
			{
				future.setResultIfUndone(result);
			}
			else
			{
				future.setResult(result);
			}
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
//		System.err.println("Problem: "+exception);
		if (customExceptionListener != null) {
			customExceptionListener.exceptionOccurred(exception);
		} else {
			if(undone)
			{
				future.setExceptionIfUndone(exception);
			}
			else
			{
				future.setException(exception);
			}
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
