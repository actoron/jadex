package jadex.micro.examples.chat;




/**
 *  Service can receive chat messages.
 */
public interface IChatService
{
	/**
	 *  Hear a new message.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
	public void hear(String name, String text);
}
