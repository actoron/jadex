package jadex.application.space.envsupport.environment;

import jadex.commons.IPropertyObject;
import jadex.service.clock.IClockService;

/**
 * Space process interface. Use this interface to implement new
 * space processes.
 */
public interface ISpaceProcess extends IPropertyObject
{
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space);

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(/*IClockService clock,*/ IEnvironmentSpace space);

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space);
}
