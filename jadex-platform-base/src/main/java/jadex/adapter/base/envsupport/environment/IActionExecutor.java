package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;

/**
 *
 */
public interface IActionExecutor
{
	/**
	 * Execute the due actions.
	 * @param currenttime The current time.
	 * @param deltat The time elapsed.
	 * @param syncobject The syncobject holding the actions.
	 */
	public void execute(long currenttime, IVector1 deltat, SynchronizedObject syncobject);
}
