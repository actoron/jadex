package jadex.tools.jcc;

import jadex.base.gui.plugin.IControlCenter;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  JCC result listener.
 */
public class JCCResultListener<E> extends SwingDefaultResultListener<E> 
{
	/** The global control center. */
	protected IControlCenter	controlcenter;
	
	/**
	 *  Create a new jcc listener.
	 */
	public JCCResultListener(IControlCenter controlcenter)
	{
		this.controlcenter = controlcenter;
	}
	
	/**
	 *  Called when result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(E result)
	{
	}
	
	/**
	 *  Called when exception occurred.
	 *  @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		controlcenter.setStatusText(exception.getMessage());
	}
}
