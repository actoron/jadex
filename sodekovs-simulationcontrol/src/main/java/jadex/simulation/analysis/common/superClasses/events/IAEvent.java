package jadex.simulation.analysis.common.superClasses.events;

import jadex.simulation.analysis.common.util.AConstants;

/**
 * A Basic event
 * @author 5Haubeck
 *
 */
public interface IAEvent
{
	/**
	 * Sets Command
	 * Command indicades the eventtype. See {@link AConstants}
	 * @param eventCommand
	 */
	public abstract void setCommand(String eventCommand);

	/**
	 * Returns Command
	 * Command indicades the eventtype. See {@link AConstants}
	 * @return the command
	 */
	public abstract String getCommand();

	/**
	 * Get type of Event. Service, Task, ...
	 * @return the type
	 */
	public abstract String getEventType();

	/**
	 * Returns Object monitor for concurrent acces
	 * @return object monitor
	 */
	public abstract Object getMutex();

}