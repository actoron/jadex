package jadex.rules.tools.stateviewer;

import jadex.commons.concurrent.ISynchronizator;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 *  Synchronize code execution with the swing thread.
 */
public class SwingSynchronizator implements ISynchronizator
{
	/**
	 *  Invoke some code synchronized with other behavior.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed.
	 *  If the synchronizator does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 */
	public void invokeSynchronized(Runnable code)
	{
		try
		{
			SwingUtilities.invokeAndWait(code);
		}
		catch(InvocationTargetException e)
		{
			if(e.getTargetException() instanceof RuntimeException)
				throw (RuntimeException)e.getTargetException();
			else if(e.getTargetException() instanceof Error)
				throw (Error)e.getTargetException();
			else
				throw new RuntimeException(e.getTargetException());
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Add an action from external thread.
	 *  The contract of this method is as follows:
	 *  The synchronizator ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action.
	 */
	public void invokeLater(Runnable action)
	{
		SwingUtilities.invokeLater(action);
	}

	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread()
	{
		return !SwingUtilities.isEventDispatchThread();
	}
}
