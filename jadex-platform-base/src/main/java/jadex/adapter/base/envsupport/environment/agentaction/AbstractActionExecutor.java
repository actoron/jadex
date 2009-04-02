package jadex.adapter.base.envsupport.environment.agentaction;

import java.util.Comparator;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;

public abstract class AbstractActionExecutor implements IActionExecutor
{
	/** The synchronizer for the actions. */
	private ActionSynchronizer synchronizer;
	
	public AbstractActionExecutor()
	{
		synchronizer = new ActionSynchronizer();
	}
	
	public AbstractActionExecutor(Comparator comp)
	{
		synchronizer = new ActionSynchronizer(comp);
	}
	
	/**
	 * Returns the synchronizer.
	 * @return synchronizer
	 */
	public ActionSynchronizer getSynchronizer()
	{
		return synchronizer;
	}
	
	public void start(IEnvironmentSpace space)
	{
		synchronizer.setMonitor(space.getMonitor());
	}
	
	public void shutdown(IEnvironmentSpace space)
	{
	}
}
