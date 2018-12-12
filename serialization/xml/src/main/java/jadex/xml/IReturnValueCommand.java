package jadex.xml;

/**
 *  Interface for a command with return value.
 */
public interface IReturnValueCommand
{
	/**
	 *  Execute the command and optionally deliver a result.
	 *  @param args The arguments.
	 *  @return The result.
	 */
	public Object execute(Object args);
}
