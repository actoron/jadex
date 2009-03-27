package jadex.bdi.examples.garbagecollector2;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.math.IVector1;

/**
 * 
 */
public class CreateWasteProcess implements ISpaceProcess
{
	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void start(IEnvironmentSpace space)
	{
		System.out.println("create waste process started.");
	}

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void shutdown(IEnvironmentSpace space)
	{
		System.out.println("create waste process shutdowned.");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param time the current time
	 * @param deltaT time passed during this step
	 * @param space the space this process is running in
	 */
	public void execute(long time, IVector1 deltaT, IEnvironmentSpace space)
	{
		System.out.println("create waste process called: "+time);
	}


	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "create";
	}
}
