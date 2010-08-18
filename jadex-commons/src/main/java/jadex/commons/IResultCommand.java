package jadex.commons;

/**
 *  Command with a result.
 */
public interface IResultCommand
{
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public Object execute(Object args); 
}
