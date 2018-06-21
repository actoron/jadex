package jadex.android.applications.chat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import jadex.android.applications.chat.ChatUser;
import jadex.android.applications.chat.ChatUserArrayAdapter;
import jadex.android.applications.chat.model.ITypedObservable;
import jadex.android.applications.chat.model.ITypedObserver;
import jadex.android.applications.chat.model.TypedObserver;
import jadex.android.applications.chat.model.UserModel;
import jadex.android.applications.chat.service.IAndroidChatService;

public class UsersFragment extends ListFragment implements ITypedObserver<Boolean> {

	private ChatUserArrayAdapter listAdapter;
	private IAndroidChatService chatService;
	private ChatServiceProvider chatServiceProvider;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		chatServiceProvider = (ChatServiceProvider) getActivity();
		chatServiceProvider.addObserver(this);
		update(chatServiceProvider, chatServiceProvider.isConnected());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ChatUserArrayAdapter adapter = new ChatUserArrayAdapter(getActivity());
		this.listAdapter = adapter;
		setListAdapter(adapter);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE,0,Menu.NONE,"Refresh Users").setIcon(android.R.drawable.ic_menu_rotate);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			refreshUsers();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void update(ITypedObservable<Boolean> observable,
			Boolean paramObject, int notificationType) {
		update(observable, paramObject);
	}

	@Override
	public void update(ITypedObservable<Boolean> observable,
			Boolean connected) {
		if (connected) {
			this.chatService = chatServiceProvider.getChatService();
			refreshUsers();
			chatService.getUserModel().addObserver(new TypedObserver<ChatUser>() {

				@Override
				public void update(ITypedObservable<ChatUser> paramObservable,
						final ChatUser chatUser, int notificationType) {
					if (notificationType == UserModel.NOTIFICATION_TYPE_ADD) {
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								listAdapter.add(chatUser);
							}
						});
					} else if (notificationType == UserModel.NOTIFICATION_TYPE_UPDATE) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
//								int position = listAdapter.getPosition(chatUser);
//								ChatUser item = listAdapter.getItem(position);
								listAdapter.notifyDataSetChanged();
							}
						});
					}
				}
				
			});
		} else {
			this.chatService = null;
		}
	}

	private void refreshUsers() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listAdapter.clear();
			}
		});
		final UserModel userModel = chatService.getUserModel();
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (final ChatUser user : userModel.getUsers()) {
					listAdapter.add(user);
				}
			}
		});
	}
}
