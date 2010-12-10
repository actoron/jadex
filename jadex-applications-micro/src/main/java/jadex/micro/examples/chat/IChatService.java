package jadex.micro.examples.chat;

import jadex.commons.service.IService;


/**
 *  Service can receive chat messages.
 */
public interface IChatService extends IService
{
	/**
	 *  Hear something.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
	public void hear(String name, String text);
		
}
