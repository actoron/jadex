package jadex.commons.future;

/**
 * 
 */
public interface ICommandFuture
{
	/** The available commands. */
	public enum Type{UPDATETIMER}
	
	/**
	 *  Send a command to the listeners.
	 *  @param command The command.
	 */
	public void sendCommand(Object command);
}
