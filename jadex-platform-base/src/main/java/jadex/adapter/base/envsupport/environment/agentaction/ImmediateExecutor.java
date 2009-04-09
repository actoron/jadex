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
	 *  @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 *  @param space the space this process is running in
	 */
	public void execute(IVector1 progress, IEnvironmentSpace space)
	{
		getSynchronizer().executeEntries(null);
	}
}