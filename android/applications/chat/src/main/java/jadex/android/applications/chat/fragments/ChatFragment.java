package jadex.android.applications.chat.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import jadex.android.applications.chat.ChatEventArrayAdapter;
import jadex.android.applications.chat.R;
import jadex.android.applications.chat.filetransfer.TransferActivity;
import jadex.android.applications.chat.model.ITypedObservable;
import jadex.android.applications.chat.model.ITypedObserver;
import jadex.android.applications.chat.service.AndroidChatService.ChatEventListener;
import jadex.android.applications.chat.service.IAndroidChatService;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.commons.future.DefaultResultListener;
import jadex.platform.service.chat.ChatService;

/**
 * Fragment for the jadex android chat app.
 */
public class ChatFragment extends Fragment implements ChatEventListener, ITypedObserver<Boolean>
{
	
	// -------- attributes --------

	/** The text view for showing results. */
	private ListView listView;

	private Button sendButton;

	private EditText messageEditText;

	private IAndroidChatService service;

	private ChatServiceProvider chatServiceProvider;

	// -------- methods --------
	
	@Override
	public void onAttach(Context ctx) {
		super.onAttach(ctx);
		this.chatServiceProvider = (ChatServiceProvider) getActivity();
		chatServiceProvider.addObserver(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.chat, container, false);

		sendButton = (Button) view.findViewById(R.id.send);
		messageEditText = (EditText) view.findViewById(R.id.chatmsg);
		messageEditText.setEnabled(false);
		sendButton.setEnabled(false);

		sendButton.setOnClickListener(onSendMessageClickListener);

		listView = (ListView) view.findViewById(R.id.chat_listView);
		chatEventAdapter = new ChatEventArrayAdapter(getActivity());
		listView.setAdapter(chatEventAdapter);
		
		setHasOptionsMenu(true);
		
		update(chatServiceProvider, chatServiceProvider.isConnected());
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (service != null) {
			service.removeMessageListener(this);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.add(Menu.NONE,0,Menu.NONE,"Shutdown Chat").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(Menu.NONE,1,Menu.NONE,"Show Transfers").setIcon(android.R.drawable.ic_menu_share);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 0:
			service.shutdown();
			getActivity().finish();	
			break;
		case 1:
			startActivity(new Intent(getActivity(), TransferActivity.class));
			break;
		default:
			break;
		}
		
		return true;
	}
	
	OnClickListener onSendMessageClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			sendButton.setEnabled(false);
			messageEditText.setEnabled(false);
			getActivity().setProgressBarIndeterminateVisibility(true);
			service.sendMessage(messageEditText.getText().toString()).addResultListener(new DefaultResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					getActivity().runOnUiThread(new Runnable()
					{
						public void run()
						{
							messageEditText.setText("");
							sendButton.setEnabled(true);
							messageEditText.setEnabled(true);
							getActivity().setProgressBarIndeterminateVisibility(false);
						}
					});
				}

				@Override
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Chat message problem: " + exception);
					exception.printStackTrace();
					getActivity().runOnUiThread(new Runnable()
					{
						public void run()
						{
							messageEditText.setEnabled(true);
							sendButton.setEnabled(true);
							getActivity().setProgressBarIndeterminateVisibility(false);
						}
					});
				}

			});
		}
	};

	private ChatEventArrayAdapter chatEventAdapter;

	@Override
	public boolean eventReceived(final ChatEvent event)
	{
		boolean processed = false;
		String eventType = event.getType();
		if (eventType.equals(ChatEvent.TYPE_MESSAGE))
		{
			processed = true;
			getActivity().runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					chatEventAdapter.add(event);
				}
			});
		} else if (eventType.equals(ChatEvent.TYPE_STATECHANGE)) {
//			processed = true;
//			getActivity().runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					chatEventAdapter.add(event);
//				}
//			});
//			IComponentIdentifier cid = event.getId();
		}
		return processed;
	}

	@Override
	public void chatConnected()
	{
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sendButton.setEnabled(true);
				messageEditText.setEnabled(true);
			}
		});
		this.service.getNickname().addResultListener(new DefaultResultListener<String>() {

			@Override
			public void resultAvailable(String nick) {
				chatEventAdapter.setOwnNick(nick);
				final ChatEvent chatEvent = new ChatEvent();
				chatEvent.setType(ChatEvent.TYPE_MESSAGE);
				chatEvent.setNick("System");
				chatEvent.setPrivateMessage(true);
				chatEvent.setValue("You are now connected as " + nick);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						chatEventAdapter.add(chatEvent);		
					}
				});
				
			}
		});
		this.service.setStatus(ChatService.STATE_IDLE, null, null);
		refreshUsers();
	}

	private void refreshUsers() {
//		this.service.getUsers().addResultListener(new IntermediateDefaultResultListener<ChatUser>() {
//
//			@Override
//			public void intermediateResultAvailable(ChatUser result) {
//				userModel.refreshUser(result);
				
//				final ChatEvent chatEvent = new ChatEvent();
//				chatEvent.setType(ChatEvent.TYPE_STATECHANGE);
//				chatEvent.setNick(result.getNickName());
//				chatEvent.setValue(result.getStatus());
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						chatEventAdapter.add(chatEvent);		
//					}
//				});
//			}
//		});
	}

	@Override
	public void update(ITypedObservable<Boolean> observable,
			Boolean param, int notificationType) {
		update(observable, param);
	}

	@Override
	public void update(ITypedObservable<Boolean> observable,
			Boolean connected) {
		if (connected) {
			// service connected, wait for chat connected
			service = chatServiceProvider.getChatService();
			service.addChatEventListener(this);
		} else {
			// service disconnected
			sendButton.setEnabled(connected);
			messageEditText.setEnabled(connected);
		}
	}

	// -------- helper methods --------

}