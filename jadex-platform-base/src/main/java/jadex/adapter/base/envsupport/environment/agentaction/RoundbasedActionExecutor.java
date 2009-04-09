package jadex.adapter.base.envsupport.environment.agentaction;

import java.util.Comparator;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.SynchronizedObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;

/**
 *  The roundbased executor executes all action at the end of
 *  a round. A round is defined by a roundtime.
 */
public class RoundbasedActionExecutor extends AbstractActionExecutor implements IActionExecutor
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
	
	/**
	 *  Create a new executor.
	 */
	public RoundbasedActionExecutor(IVector1 roundtime, Comparator comp)
	{
		super(comp);
		this.roundtime = roundtime;
		this.elapsed = Vector1Double.ZERO;
	}
	
	//-------- methods --------

	/**
	 *  Executes the environment process
	 *  @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 *  @param space the space this process is running in
	 */
	public void execute(IVector1 progress, IEnvironmentSpace space)
	{
		// TODO: FIXME!
		//elapsed.add(deltat);
		if(elapsed.greater(roundtime) || elapsed.equals(roundtime))
		{
			getSynchronizer().executeEntries(null);
			elapsed.subtract(roundtime);
		}
	}
}