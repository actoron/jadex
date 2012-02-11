package jadex.base.gui;

import jadex.commons.future.IIntermediateResultListener;

import java.util.Collection;

import javax.swing.SwingUtilities;

/**
 *  Default implementation of intermediate result listener
 *  with methods called on swing thread.
 */
public abstract class SwingIntermediateDefaultResultListener<E> extends SwingDefaultResultListener<Collection<E>>	implements IIntermediateResultListener<E>
{
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
    public abstract void customFinished();

    //-------- methods --------
	/**
	 *  Call customIntermediateResultAvailable() on swing thread.
	 */
	public final void intermediateResultAvailable(final E result)
	{
		SwingUtilities.invokeLater(new Runnable()
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
		SwingUtilities.invokeLater(new Runnable()
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
		for(E e: result)
		{
			customIntermediateResultAvailable(e);
		}
		customFinished();
	}
}
