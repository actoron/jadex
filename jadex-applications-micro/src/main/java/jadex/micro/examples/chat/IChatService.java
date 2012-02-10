package jadex.micro.examples.chat;

import jadex.bridge.service.annotation.Security;
import jadex.commons.IRemoteChangeListener;


/**
 *  Service can receive chat messages.
 */
@Security(Security.UNRESTRICTED)
public interface IChatService
{
	/**
	 *  Hear a new message.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
	@Security(Security.UNRESTRICTED)
	public void hear(String name, String text);
		
	/**
	 *  Add a local listener.
	 */
	public void addChangeListener(IRemoteChangeListener listener);
	
	/**
	 *  Remove a local listener.
	 */
	public void removeChangeListener(IRemoteChangeListener listener);
	
}
