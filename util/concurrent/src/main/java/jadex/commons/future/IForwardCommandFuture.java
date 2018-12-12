package jadex.commons.future;

/**
 * 
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
