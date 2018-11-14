package jadex.commons.concurrent;

/**
 *  Interface for objects that can be executed. Similar to interface
 *  Runnable with the difference that a short-stepped interruptable
 *  execution is assumed here.
 */
public interface IExecutable
{
	/**
	 *  Execute the executable.
	 *  @return True, if the object wants to be executed again.
	 */
	public boolean execute();
}
