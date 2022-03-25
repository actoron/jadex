package jadex.commons.future;

/**
 *  Send a command from source to listeners (same flow as results).
 */
public interface IForwardCommandFuture
{
	/** The available commands. */
	public enum Type{UPDATETIMER}
	
	/**
	 *  Send a command to the listeners.
	 *  @param command The command.
	 */
	public void sendForwardCommand(Object command);
}
