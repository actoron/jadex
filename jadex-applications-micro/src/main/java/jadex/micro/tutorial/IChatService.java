package jadex.micro.tutorial;

/**
 * 
 */
public interface IChatService
{
	/**
	 *  Receives a chat message.
	 *  @param sender The sender's name.
	 *  @param text The message text.
	 */
	public void message(String sender, String text);
}
