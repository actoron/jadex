package jadex.commons.gui.future;

import java.awt.Component;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import jadex.commons.SReflect;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.gui.SGUI;

/**
 *  Default implementation of intermediate result listener
 *  with methods called on swing thread.
 */
public abstract class SwingIntermediateDefaultResultListener<E> extends SwingDefaultResultListener<Collection<E>>	implements IIntermediateResultListener<E>
{
	//-------- constructors --------
	
	/**
	 *  Create a new listener.
	 */
	public SwingIntermediateDefaultResultListener()
	{
	}
	
	/**
	 *  Create a new listener.
	 *  @param parent The parent component (when errors should be shown as dialog).
	 */
	public SwingIntermediateDefaultResultListener(Component parent)
	{
		super(parent);
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public SwingIntermediateDefaultResultListener(Logger logger)
	{
		super(logger);
	}
	//-------- template methods --------

	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public abstract void customIntermediateResultAvailable(E result);
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void customFinished()
    {
    	// Empty default implementation.
    }

    //-------- methods --------
	/**
	 *  Call customIntermediateResultAvailable() on swing thread.
	 */
	public final void intermediateResultAvailable(final E result)
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
	 *  Call customFinished() on swing thread.
	 */
	public final void finished()
	{
		SGUI.invokeLaterSimBlock(new Runnable()
		{
			public void run()
			{
				customFinished();
			}
		});
	}

	/**
	 *  Overwritten to call intermediate and finished methods.
	 */
	public void customResultAvailable(Collection<E> result)
	{
		// Already called on swing thread.
		for(E e: result)
		{
			customIntermediateResultAvailable(e);
		}
		customFinished();
	}
}
