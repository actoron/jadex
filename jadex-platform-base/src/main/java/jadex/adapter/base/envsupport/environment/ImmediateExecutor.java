package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;

/**
 *  The immediate executor executes all action at the next clock tick.
 */
public class ImmediateExecutor implements IActionExecutor
{
	//-------- methods --------
	
	/**
	 * Execute the due actions.
	 * @param currenttime The current time.
	 * @param deltat The time elapsed.
	 * @param syncobject The syncobject holding the actions.
	 */
	public void execute(long currenttime, IVector1 deltat, SynchronizedObject syncobject)
	{
		syncobject.executeEntries(null);
	}
}