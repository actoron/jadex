package jadex.base;

import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *  Result listener that redirects callbacks on the swing thread.
 */
public abstract class SwingDefaultResultListener extends DefaultResultListener
{
	//-------- attributes --------
	
	/** The static instance. */
	private static IResultListener instance;
	
	/** The component. */
	protected Component parent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingDefaultResultListener()
	{
	}
	
	/**
	 *  Create a new listener.
	 *  @param parent The parent component (when errors should be shown as dialog).
	 */
	public SwingDefaultResultListener(Component parent)
	{
		this.parent	= parent;
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public SwingDefaultResultListener(Logger logger)
	{
		super(logger);
	}
	
	/**
	 *  Get the listener instance.
	 *  @return The listener.
	 */
	public static IResultListener getInstance()
	{
		// Hack! Implement that logger can be passed
		if(instance==null)
		{
			instance = new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object source, Object result)
				{
				}
			};
		}
		return instance;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	final public void resultAvailable(final Object source, final Object result)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				customResultAvailable(source, result);
			}
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	final public void exceptionOccurred(final Object source, final Exception exception)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				customExceptionOccurred(source, exception);
			}
		});
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public abstract void customResultAvailable(Object source, Object result);
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Object source, Exception exception)
	{
		if(parent!=null)
		{
			String text = SUtil.wrapText("A problem occurred while performing the requested action: "+exception.getMessage());
			JOptionPane.showMessageDialog(SGUI.getWindowParent(parent), text,
				"Problem Occurred", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			super.exceptionOccurred(source, exception);
		}
	}
}
