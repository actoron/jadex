package jadex.android.applications.chat.service;

import jadex.android.applications.chat.ChatUser;
import jadex.android.applications.chat.model.UserModel;
import jadex.android.applications.chat.service.AndroidChatService.ChatEventListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.util.Collection;

/**
 * Interface for the platform service.
 */
public interface IAndroidChatService
{
	boolean isConnected();
	
	IFuture<Void> sendMessage(String message);

	void addChatEventListener(ChatEventListener l);

	void removeMessageListener(ChatEventListener l);
	
//	IIntermediateFuture<ChatUser> getUsers();

	IFuture<Void> sendFile(String path, ChatUser user);
	
	Collection<TransferInfo> getTransfers();
	
	IFuture<Void> acceptFileTransfer(TransferInfo ti);
	
	IFuture<Void> rejectFileTransfer(TransferInfo ti);
	
	IFuture<Void> cancelFileTransfer(TransferInfo ti);

	void shutdown();

	IIntermediateFuture<IChatService> setStatus(String status, byte[] image, IComponentIdentifier[] receivers);
	
	IFuture<String> getNickname();
	
	void setNickname(String name);
	
	UserModel getUserModel();
	
}
