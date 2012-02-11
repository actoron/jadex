package jadex.micro.examples.chat;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IFuture;


/**
 *  Service can receive chat messages.
 */
@Security(Security.UNRESTRICTED)
public interface IChatService
{
	/**
	 *  Hear a new message.
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String text);
}
