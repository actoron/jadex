package jadex.android.applications.chat;

import java.io.FileInputStream;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.android.applications.chat.filetransfer.SendFileActivity;
import jadex.android.applications.chat.filetransfer.TransferActivity;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.platform.service.chat.ChatService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Activity (screen) for the jadex android benchmark app. Starts the platform
 * and allows launching the different benchmarks.
 */
public class JadexAndroidChatActivity extends Activity implements ServiceConnection, ChatEventListener
{
	// -------- attributes --------

	/** The text view for showing results. */
	private ListView listView;

	private Button sendButton;

	private EditText messageEditText;

	private IAndroidChatService service;

	private boolean connected;

	// -------- methods --------

	/**
	 * Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.chat);

		sendButton = (Button) findViewById(R.id.send);
		messageEditText = (EditText) findViewById(R.id.chatmsg);
		messageEditText.setEnabled(false);
		sendButton.setEnabled(false);

		sendButton.setOnClickListener(onSendMessageClickListener);

		listView = (ListView) findViewById(R.id.chat_listView);
		chatEventAdapter = new ChatEventArrayAdapter(this);
		listView.setAdapter(chatEventAdapter);
		startService(new Intent(this, AndroidChatService.class));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setProgressBarIndeterminateVisibility(true);
		bindService(new Intent(this, AndroidChatService.class), this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (service != null) {
			service.removeMessageListener(this);
		}
		this.service.setStatus(ChatService.STATE_AWAY, null, null);
		unbindService(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE,0,Menu.NONE,"Shutdown Chat").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(Menu.NONE,1,Menu.NONE,"Show Transfers").setIcon(android.R.drawable.ic_menu_share);
		return true;
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
			startActivity(new Intent(this, TransferActivity.class));
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
							messageEditText.setEnabled(true);
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