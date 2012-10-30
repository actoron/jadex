package jadex.android.applications.chat;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity (screen) for the jadex android benchmark app. Starts the platform
 * and allows launching the different benchmarks.
 */
public class JadexAndroidChatActivity extends Activity implements ServiceConnection, ChatEventListener
{
	// -------- attributes --------

	/** The chat gui service. */
	private IChatGuiService chatgui;

	/** The chat subscription. */
	private ISubscriptionIntermediateFuture<ChatEvent> subscription;

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
		unbindService(this);
	}

	/**
	 * Cleanup on exit.
	 */
	protected void onDestroy()
	{
		subscription.terminate();
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
		this.service.setChatEventListener(this);
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
		runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				chatEventAdapter.add(event);
			}
		});
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
	
	
//	Intent intent = getIntent();
//	if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()))
//	{
//		System.out.println("Intent: " + intent);
//		System.out.println("Type: " + intent.getType());
//		Bundle extras = intent.getExtras();
//		if (extras != null)
//		{
//			String text = (String) extras.get(Intent.EXTRA_TEXT);
//			Uri uri = (Uri) extras.get(Intent.EXTRA_STREAM);
//			System.out.println("Text: " + text);
//			System.out.println("Uri: " + uri);
//
//			// Convert the image URI to the direct
//			// file system path of the image file
//			String[] proj = new String[]
//			{ MediaStore.Images.Media.DATA };
//			Cursor cursor = managedQuery(uri, proj, // Which
//													// columns
//													// to
//													// return
//					null, // WHERE clause; which
//							// rows to return (all
//							// rows)
//					null, // WHERE clause selection
//							// arguments (none)
//					null); // Order-by clause
//							// (ascending by name)
//			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			cursor.moveToFirst();
//			final String path = cursor.getString(column_index);
//			System.out.println("Path: " + path);
//
//			chatgui.findUsers().addResultListener(new IResultListener<Collection<IChatService>>()
//			{
//				public void exceptionOccurred(Exception exception)
//				{
//				}
//
//				public void resultAvailable(final Collection<IChatService> results)
//				{
//					runOnUiThread(new Runnable()
//					{
//						public void run()
//						{
//							AlertDialog.Builder builder = new AlertDialog.Builder(JadexAndroidChatActivity.this);
//							builder.setTitle("Choose send target");
//							List<CharSequence> items = new ArrayList<CharSequence>();
//							for (IChatService chat : results)
//							{
//								items.add(((IService) chat).getServiceIdentifier().getProviderId().getName());
//							}
//							builder.setSingleChoiceItems(items.toArray(new CharSequence[items.size()]), -1,
//									new DialogInterface.OnClickListener()
//									{
//										public void onClick(DialogInterface dialog, int which)
//										{
//											dialog.dismiss();
//											if (which != -1)
//											{
//												IChatService chat = new ArrayList<IChatService>(results).get(which);
//												System.out.println("Sending to: " + chat);
//												chatgui.sendFile(path, ((IService) chat).getServiceIdentifier().getProviderId())
//														.addResultListener(new IResultListener<Void>()
//														{
//															public void resultAvailable(Void result)
//															{
//																System.out.println("Transfer started.");
//															}
//
//															public void exceptionOccurred(Exception exception)
//															{
//																System.out.println("Transfer failed to initialize: " + exception);
//															}
//														});
//											}
//										}
//									});
//							AlertDialog alert = builder.create();
//							alert.show();
//						}
//					});
//				}
//			});
//		}
//	}
//}
//
//public void exceptionOccurred(Exception exception)
//{
//	// No chat service.
//	System.out.println("Not connected to chat: " + exception);
//}
//});
//}

	// -------- helper methods --------

}