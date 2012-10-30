package jadex.android.applications.chat;

import jadex.android.applications.chat.AndroidChatService.ChatMessageListener;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 *	Interface for the platform service.
 */
public interface IAndroidChatService
{
	IFuture<Void> sendMessage(String message);
	
	void setMessageListener(ChatMessageListener l);
	
	void removeMessageListener(ChatMessageListener l);
}
