package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;

/**
 *  The roundbased executor executes all action at the end of
 *  a round. A round is defined by a roundtime.
 */
public class RoundbasedActionExecutor implements IActionExecutor
{
	//-------- attributes --------
	
	/** The round time. */
	protected IVector1 roundtime;
	
	/** The elapsed time. */
	protected IVector1 elapsed;
	
	//-------- constructors --------
	
	/**
	 *  Create a new executor.
	 */
	public RoundbasedActionExecutor(IVector1 roundtime)
	{
		this.roundtime = roundtime;
		this.elapsed = Vector1Double.ZERO;
	}
	
	//-------- methods --------

	/**
	 * Execute the due actions.
	 * @param currenttime The current time.
	 * @param deltat The time elapsed.
	 * @param syncobject The syncobject holding the actions.
	 */
	public void execute(long currenttime, IVector1 deltat, SynchronizedObject syncobject)
	{
		elapsed.add(deltat);
		if(elapsed.greater(roundtime) || elapsed.equals(roundtime))
		{
			syncobject.executeEntries(null);
			elapsed.subtract(roundtime);
		}
	}
}