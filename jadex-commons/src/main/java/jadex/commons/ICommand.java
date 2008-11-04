package jadex.commons;

/**
 *  Interface for commands that represent methods
 *  with arguments but without a return value.
 */
public interface ICommand
{
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public void execute(Object args); 
}
