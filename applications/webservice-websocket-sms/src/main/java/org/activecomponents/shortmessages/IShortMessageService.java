package org.activecomponents.shortmessages;

import java.util.Collection;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service to post messages.
 */
@Service
public interface IShortMessageService
{
	/**
	 *  Send a message to the followers.
	 *  @param text The text.
	 *  @param sender The sender.
	 */
	public IFuture<Void> sendMessage(String text, String sendertoken);
	
	/**
	 *  Get all messages.
	 *  @param usertoken The user token.
	 *  @return the messages.
	 */
	public IFuture<Collection<ShortMessage>> getMessages(String usertoken);
}
