package jadex.android.applications.chat.model;

import jadex.android.applications.chat.ChatUser;
import jadex.android.commons.Logger;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.chat.ChatEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserModel extends TypedObservable<ChatUser>{

	public static final int NOTIFICATION_TYPE_UPDATE = 0;
	public static final int NOTIFICATION_TYPE_ADD = 1;


	private Map<IComponentIdentifier, ChatUser> users;
	
	public UserModel() {
		users = new HashMap<IComponentIdentifier, ChatUser>();
	}
	
	public Collection<ChatUser> getUsers() {
		return users.values();
	}
	
	public void refreshUser(ChatUser result) {
		refreshUser(result.getCid(), result.getNickName(), result.getStatus());
	}
	
	public void refreshUser(IComponentIdentifier cid, ChatEvent event) {
		refreshUser(cid, event.getNick(), event.getValue() != null ? event.getValue().toString() : null);
	}
	
	public void refreshUser(IComponentIdentifier cid, String newNick, String newStatus) {
		ChatUser existingUser = users.get(cid);
		if (existingUser != null) {
			if (newNick != null && !newNick.equals(existingUser.getNickName())) {
				existingUser.setNickName(newNick);
			}
			if (newStatus != null && !newStatus.equals(existingUser.getStatus())) {
				existingUser.setStatus(newStatus);
			}
			Logger.i("Status/nick change: " + newNick + " is now: " + newStatus);
			informUserUpdated(existingUser);
		} else {
			ChatUser newUser = new ChatUser(newNick, newStatus, cid);
			users.put(cid, newUser);
			informUserAdded(newUser);
			Logger.i("New user: " + newNick + " is now: " + newStatus);
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
