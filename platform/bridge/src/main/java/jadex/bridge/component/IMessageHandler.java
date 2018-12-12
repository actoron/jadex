package jadex.bridge.component;

import jadex.bridge.service.types.security.ISecurityInfo;

/**
 *  Interface for message handlers.
 */
public interface IMessageHandler
{
	/**
	 *  Test if handler should handle a message.
	 *  @return True if it should handle the message. 
	 */
	public boolean isHandling(ISecurityInfo secinfos, IMsgHeader header, Object msg);
	
	/**
	 *  Test if handler should be removed.
	 *  @return True if it should be removed. 
	 */
	public boolean isRemove();
	
	/**
	 *  Handle the message.
	 *  @param header The header.
	 *  @param msg The message.
	 */
	public void handleMessage(ISecurityInfo secinfos, IMsgHeader header, Object msg);
}
