package jadex.bridge.component.impl;

import jadex.bridge.component.IMsgHeader;

/**
 *  Allows adding special treatment of certain user message types
 *  like FIPA messages.
 */
public interface IMessagePreprocessor
{
	/**
	 *  Preprocess a message before sending.
	 *  @param header	The message header, may be changed by preprocessor.
	 *  @param msg	The user object, may be changed by preprocessor.
	 */
	public void	preprocessMessage(IMsgHeader header, Object msg);
}
