package jadex.extension.envsupport.environment;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.IPropertyObject;

/**
 * Space process interface. Use this interface to implement new
 * space processes.
 */
public interface ISpaceProcess extends IPropertyObject
{
	public static final String ID = "##_id"; 
	
	//-------- methods --------
	
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
