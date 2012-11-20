package jadex.android.applications.chat;

import java.util.List;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 * Interface for the platform service.
 */
public interface IAndroidChatService
{
	IFuture<Void> sendMessage(String message);

	void setChatEventListener(ChatEventListener l);

	void removeMessageListener(ChatEventListener l);
	
	public List<ChatUser> getUsers();

	IFuture<Void> sendFile(String path, ChatUser user);
	
}
