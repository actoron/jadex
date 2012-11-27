package jadex.android.applications.chat;

import java.util.Collection;
import java.util.List;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 * Interface for the platform service.
 */
public interface IAndroidChatService
{
	IFuture<Void> sendMessage(String message);

	void addChatEventListener(ChatEventListener l);

	void removeMessageListener(ChatEventListener l);
	
	public IIntermediateFuture<ChatUser> getUsers();

	IFuture<Void> sendFile(String path, ChatUser user);
	
	public Collection<TransferInfo> getTransfers();
	
}
