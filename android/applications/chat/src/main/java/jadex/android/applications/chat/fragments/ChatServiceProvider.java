package jadex.android.applications.chat.fragments;

import jadex.android.applications.chat.model.ITypedObservable;
import jadex.android.applications.chat.service.IAndroidChatService;

public interface ChatServiceProvider extends ITypedObservable<Boolean> {
	public boolean isConnected();
	
	public IAndroidChatService getChatService();
}