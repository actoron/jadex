package jadex.commons.concurrent;

/**
 *  Synchronize some behavior.
 */
public interface ISynchronizator
{
	/**
	 *  Invoke some code synchronized with other behavior.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed.
	 *  If the synchronizator does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 */
	public void invokeSynchronized(Runnable code);

	/**
	 *  Add an action from external thread.
	 *  The contract of this method is as follows:
	 *  The synchronizator ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action.
	 */
	public void invokeLater(Runnable action);

	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread();
}