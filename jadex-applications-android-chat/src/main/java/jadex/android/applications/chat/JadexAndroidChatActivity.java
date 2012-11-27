package jadex.android.applications.chat;

import java.io.FileInputStream;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
		unbindService(this); // remove this to stay connected
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
	public void eventReceived(final ChatEvent event)
	{
		if (event.getType().equals(ChatEvent.TYPE_MESSAGE))
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					chatEventAdapter.add(event);
				}
			});
		} else if (event.getType().equals(ChatEvent.TYPE_FILE))
		{
			final TransferInfo ti = (TransferInfo) event.getValue();
			if (ti.getState().equals(TransferInfo.STATE_WAITING))
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						Builder builder = new AlertDialog.Builder(JadexAndroidChatActivity.this);
						builder.setTitle("Incoming Filetransfer");
						builder.setMessage(event.getNick() + " wants to send you " + ti.getFileName() + ".\n" + "Do you want to download this file?");
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								startActivity(new Intent(JadexAndroidChatActivity.this, TransferActivity.class));
							}

						});
						builder.setNegativeButton("No", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
							}
						});
						builder.create().show();
					}
				});
			}
		}
	}

	@Override
	public void chatConnected()
	{
		setConnected(true);
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}

	// -------- helper methods --------

}