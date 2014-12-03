package jadex.android.applications.chat.model;

import jadex.android.applications.chat.ChatUser;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.chat.ChatEvent;

import java.util.HashMap;
import java.util.Map;

public class UserModel extends TypedObservable<ChatUser>{

	private static final int NOTIFICATION_TYPE_UPDATE = 0;
	private static final int NOTIFICATION_TYPE_ADD = 1;

	public interface UserModelListener {
		public void userAdded(ChatUser user);
		public void userUpdated(ChatUser user);
	}

	private Map<IComponentIdentifier, ChatUser> users;
	
	public UserModel() {
		users = new HashMap<IComponentIdentifier, ChatUser>();
	}
	
	public void refreshUser(ChatUser result) {
		ChatUser existingUser = users.get(result.getCid());
		if (existingUser != null) {
			existingUser.setNickName(result.getNickName());
			existingUser.setStatus(result.getStatus());
			informUserUpdated(result);
		} else {
			users.put(result.getCid(), result);
			informUserAdded(result);
		}
	}
	
	public void refreshUser(IComponentIdentifier cid, ChatEvent event) {
		
	}
	
	public void refreshUser(IComponentIdentifier cid, String newNick, String newStatus) {
		ChatUser existingUser = users.get(cid);
		if (existingUser != null) {
			existingUser.setNickName(newNick);
			existingUser.setStatus(newStatus);
			informUserUpdated(existingUser);
		} else {
			ChatUser newUser = new ChatUser(newNick, newStatus, cid);
			users.put(cid, newUser);
			informUserAdded(newUser);
		}
	}

	private void informUserAdded(ChatUser result) {
		setChanged();
		notifyObservers(result, NOTIFICATION_TYPE_ADD);
	}

	private void informUserUpdated(ChatUser result) {
		setChanged();
		notifyObservers(result, NOTIFICATION_TYPE_UPDATE);
	}



}
