package jadex.adapter.base.envsupport.environment.agentaction;

import java.util.Comparator;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.SynchronizedObject;
import jadex.adapter.base.envsupport.math.IVector1;

/**
 *  The immediate executor executes all action at the next clock tick.
 */
public class ImmediateExecutor extends AbstractActionExecutor
							   implements IActionExecutor
{
	public ImmediateExecutor()
	{
		super();
	}
	
	public ImmediateExecutor(Comparator comp)
	{
		super(comp);
	}
	
	//-------- methods --------
	
	/**
	 *  Executes the environment process
	 *  @param time the current time
	 *  @param deltaT time passed during this step
	 *  @param space the space this process is running in
	 */
	public void execute(long currenttime, IVector1 deltat, IEnvironmentSpace space)
	{
		getSynchronizer().executeEntries(null);
	}
}