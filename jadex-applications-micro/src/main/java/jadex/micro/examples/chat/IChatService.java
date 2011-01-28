package jadex.micro.examples.chat;

import jadex.commons.IRemoteChangeListener;
import jadex.commons.service.IService;


/**
 *  Service can receive chat messages.
 */
public interface IChatService extends IService
{
	/**
	 *  Hear a new message.
	 *  @param name The name of the sender.
	 *  @param text The text message.
	 */
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
