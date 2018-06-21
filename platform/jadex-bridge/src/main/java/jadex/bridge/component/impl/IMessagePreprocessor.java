package jadex.bridge.component.impl;

import jadex.bridge.component.IMsgHeader;

/**
 *  Allows adding special treatment of certain user message types
 *  like FIPA messages.
 */
public interface IMessagePreprocessor<T>
{
	/**
	 *  Optionally preprocess a message before sending.
	 *  @param header	The message header, may be changed by preprocessor.
	 *  @param msg	The user object, may be changed by preprocessor.
	 */
	public void	preprocessMessage(IMsgHeader header, T msg);
	
	/**
	 *  Optionally check for reply matches.
	 *  Currently only used in BDIX.
	 *  @param	message	The initial message object.
	 *  @param	reply	The replied message object.
	 *  @return	true when the reply matches the initial message.
	 */
	public boolean	isReply(T message, T reply);
}
