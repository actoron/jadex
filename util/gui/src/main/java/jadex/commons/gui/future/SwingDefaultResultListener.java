package jadex.commons.gui.future;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import jadex.bridge.service.types.simulation.SSimulation;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.gui.SGUI;

/**
 *  Result listener that redirects callbacks on the swing thread.
 */
public class SwingDefaultResultListener<E> extends DefaultResultListener<E>	implements IFutureCommandResultListener<E>
{
	//-------- attributes --------
	
	/** The component. */
	protected Component parent;
	
	/** Custom result listener */
	protected IFunctionalResultListener<E>	customResultListener;
	
	/** Custom result listener */
	protected IFunctionalExceptionListener	customExceptionListener;
	
	//-------- constructors --------
	
	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param listener The listener.
	 */
	public SwingDefaultResultListener(IFunctionalResultListener<E> customResultListener)
	{
		this(customResultListener, null);
	}

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param customResultListener The custom result listener.
	 * @param customExceptionListener The listener that is called on exceptions.
	 */
	public SwingDefaultResultListener(IFunctionalResultListener<E> customResultListener, IFunctionalExceptionListener customExceptionListener)
	{
		this(customResultListener, customExceptionListener, null);
	}

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param customResultListener The custom result listener.
	 * @param customExceptionListener The listener that is called on exceptions.
	 * @param parent The parent component (when errors should be shown as
	 *        dialog).
	 */
	public SwingDefaultResultListener(IFunctionalResultListener<E> customResultListener, IFunctionalExceptionListener customExceptionListener, Component parent)
	{
		this(parent);
		this.customResultListener = customResultListener;
		this.customExceptionListener = customExceptionListener;
	}

	protected static void	unblock(Future<Void> adblock)
	{
		if(adblock!=null)
			adblock.setResult(null);
	}

	/**
	 *  Create a new listener.
	 */
	public SwingDefaultResultListener()
	{
//		Thread.dumpStack();
	}
	
	/**
	 *  Create a new listener.
	 *  @param parent The parent component (when errors should be shown as dialog).
	 */
	public SwingDefaultResultListener(Component parent)
	{
		this.parent	= parent;
//		Thread.dumpStack();
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public SwingDefaultResultListener(Logger logger)
	{
		super(logger);
//		Thread.dumpStack();
	}
	
	//-------- methods --------
	
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
				customResultAvailable(result);
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
	 * @param result The result.
	 */
	public void customResultAvailable(E result) {
		if (customResultListener != null) {
			customResultListener.resultAvailable(result);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		if (customExceptionListener != null) {
			customExceptionListener.exceptionOccurred(exception);
		} else {
			if(parent!=null)
			{
				SGUI.showError(parent, "Problem Occurred", "A problem occurred while performing the requested action: "
						+SReflect.getInnerClassName(exception.getClass())+" "+exception.getMessage(), exception);
	//			exception.printStackTrace();
			}
			else
			{
				super.exceptionOccurred(exception);
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
		Logger.getLogger("swing-result-listener").fine("Cannot forward command: "+this+" "+command);
	}
}
