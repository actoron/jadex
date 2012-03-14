package jadex.commons.gui.future;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.gui.SGUI;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *  Result listener that redirects callbacks on the swing thread.
 */
public abstract class SwingDefaultResultListener<E> extends DefaultResultListener<E>
{
	//-------- attributes --------
	
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
	
	//-------- methods --------
	
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
	 * @param result The result.
	 */
	public abstract void customResultAvailable(E result);
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		if(parent!=null)
		{
			exception.printStackTrace();
			String text = SUtil.wrapText("A problem occurred while performing the requested action: "
				+SReflect.getInnerClassName(exception.getClass())+" "+exception.getMessage());
			JOptionPane.showMessageDialog(SGUI.getWindowParent(parent), text,
				"Problem Occurred", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			super.exceptionOccurred(exception);
		}
	}
}
