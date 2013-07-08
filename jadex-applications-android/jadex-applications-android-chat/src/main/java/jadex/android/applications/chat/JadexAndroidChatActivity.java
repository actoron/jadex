package jadex.android.applications.chat;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.android.applications.chat.filetransfer.TransferActivity;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.commons.future.DefaultResultListener;
import jadex.platform.service.chat.ChatService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Activity (screen) for the jadex android benchmark app. Starts the platform
 * and allows launching the different benchmarks.
 */
public class JadexAndroidChatActivity extends ClientAppFragment implements ServiceConnection, ChatEventListener
{
	// -------- attributes --------

	/** The text view for showing results. */
	private ListView listView;

	private Button sendButton;

	private EditText messageEditText;

	private IAndroidChatService service;

	private boolean connected;

	// -------- methods --------
	
	@Override
	public void onPrepare(Activity mainActivity)
	{
		super.onPrepare(mainActivity);
		mainActivity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
	}

	/**
	 * Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		startService(new Intent(getActivity(), AndroidChatService.class));
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
		
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setProgressBarIndeterminateVisibility(true);
		bindService(new Intent(getActivity(), AndroidChatService.class), this, BIND_AUTO_CREATE);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (service != null) {
			service.removeMessageListener(this);
			service.setStatus(ChatService.STATE_AWAY, null, null);
			unbindService(this);
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
			finish();	
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
			setProgressBarIndeterminateVisibility(true);
			service.sendMessage(messageEditText.getText().toString()).addResultListener(new DefaultResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							messageEditText.setText("");
							sendButton.setEnabled(true);
							messageEditText.setEnabled(true);
							setProgressBarIndeterminateVisibility(false);
						}
					});
				}

				@Override
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Chat message problem: " + exception);
					exception.printStackTrace();
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							messageEditText.setEnabled(true);
							sendButton.setEnabled(true);
							setProgressBarIndeterminateVisibility(false);
						}
					});
				}

			});
		}
	};

	private ChatEventArrayAdapter chatEventAdapter;

	public void onServiceConnected(ComponentName comp, IBinder binder)
	{
		this.service = (IAndroidChatService) binder;
		this.service.addChatEventListener(this);
	}

	private void setConnected(final boolean b)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				setProgressBarIndeterminateVisibility(false);
				sendButton.setEnabled(b);
				messageEditText.setEnabled(b);
			}
		});
		this.connected = b;
	}

	public void onServiceDisconnected(ComponentName name)
	{
		this.service = null;
		setConnected(false);
	}

	@Override
	public boolean eventReceived(final ChatEvent event)
	{
		boolean processed = false;
		if (event.getType().equals(ChatEvent.TYPE_MESSAGE))
		{
			processed = true;
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					chatEventAdapter.add(event);
				}
			});
		} 
		return processed;
	}

	@Override
	public void chatConnected()
	{
		setConnected(true);
		this.service.setStatus(ChatService.STATE_IDLE, null, null);
	}

	// -------- helper methods --------

}