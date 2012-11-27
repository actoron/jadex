package jadex.android.applications.chat;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.IResultListener;

import java.io.FileInputStream;
import java.util.List;

import org.h2.value.Transfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SendFileActivity extends Activity implements ServiceConnection, ChatEventListener
{
	private String path;
	private IAndroidChatService service;
	private ChatUserArrayAdapter adapter;
	private ListView contactsListView;
	private TextView statusTextView;
	private Button refreshButton;
	private TextView infoTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.sendfile);
		
		adapter = new ChatUserArrayAdapter(getApplicationContext());

		contactsListView = (ListView) findViewById(R.id.sendfile_contactslist);
		contactsListView.setAdapter(adapter);
		contactsListView.setOnItemClickListener(onChatUserClickListener);

		statusTextView = (TextView) findViewById(R.id.sendfile_statustext);
		
		refreshButton = (Button) findViewById(R.id.sendfile_refreshButton);
		refreshButton.setOnClickListener(onRefreshClickListener);
		infoTextView = (TextView) findViewById(R.id.sendfile_infotext);

		Intent intent = getIntent();

		if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()))
		{
			System.out.println("Intent: " + intent);
			System.out.println("Type: " + intent.getType());
			Bundle extras = intent.getExtras();
			if (extras != null)
			{
				String text = (String) extras.get(Intent.EXTRA_TEXT);
				Uri uri = (Uri) extras.get(Intent.EXTRA_STREAM);
				System.out.println("Text: " + text);
				System.out.println("Uri: " + uri);
				//
				// Convert the image URI to the direct
				// file system path of the image file
				String[] proj = new String[]
				{ MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(uri, proj, // Which
														// columns
														// to
														// return
						null, // WHERE clause; which
								// rows to return (all
								// rows)
						null, // WHERE clause selection
								// arguments (none)
						null); // Order-by clause
								// (ascending by name)
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				final String path = cursor.getString(column_index);
				System.out.println("Path: " + path);

				this.path = path;
				infoTextView.setText("Sending file " + path);
				//
			}
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setProgressBarIndeterminateVisibility(true);
		setProgressBarIndeterminate(true);
		bindService(new Intent(this, AndroidChatService.class), this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder)
	{
		service = (IAndroidChatService) binder;
		service.setChatEventListener(this);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		service = null;
	}

	@Override
	public void chatConnected()
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(3000);
				} catch (InterruptedException e)
				{
				}
				refreshUsers();
			}
		}).start();
	}

	@Override
	public void eventReceived(ChatEvent ce)
	{
		System.out.println(ce);
		if (ce.getType() == ChatEvent.TYPE_FILE) {
			final TransferInfo ti = (TransferInfo) ce.getValue();
			if (ti.getFilePath().equals(path)) {
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if (ti.getState().equals(TransferInfo.STATE_COMPLETED)) {
							statusTextView.setText("Transfer complete.");
						} else if (ti.getState().equals(TransferInfo.STATE_ABORTED)) {
							statusTextView.setText("Transfer aborted.");
						} else if (ti.getState().equals(TransferInfo.STATE_ERROR)) {
							statusTextView.setText("Transfer error");
						} else if (ti.getState().equals(TransferInfo.STATE_TRANSFERRING)) {
							statusTextView.setText("Transferring: " + ti.getDone());
						}
					}
				});
				System.out.println(ti.getDone());
			}
		}
	}
	
	private void refreshUsers()
	{
		runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				statusTextView.setText("Loading Contacts...");
				refreshButton.setEnabled(false);
				setProgressBarIndeterminateVisibility(true);
			}
		});
		

		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				final List<ChatUser> users = service.getUsers();
				
				runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						
						adapter.clear();
						for (ChatUser chatUser : users)
						{
							adapter.add(chatUser);
						}
						setProgressBarIndeterminateVisibility(false);
//				Context applicationContext = getApplicationContext();
						
						statusTextView.setText("Choose receiver");
						refreshButton.setEnabled(true);
					}
				});
			}
		}).start();
	}
	
	// ------------ click listeners -------------

	private OnItemClickListener onChatUserClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			final AlertDialog.Builder builder = new AlertDialog.Builder(SendFileActivity.this);
			final ChatUser receiver = adapter.getItem(position);

			builder.setTitle("Send file " + path + " to " + receiver.getNickName() + "?");

			builder.setPositiveButton("OK", new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					
					System.out.println("Sending to: " + receiver);
					statusTextView.setText("Waiting for " + receiver + " to accept...");
					
					service.sendFile(path, receiver).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							System.out.println("Transfer started.");
							runOnUiThread(new Runnable()
							{
								
								@Override
								public void run()
								{
									statusTextView.setText("Transfer started.");
								}
							});
						}

						public void exceptionOccurred(Exception exception)
						{
							System.out.println("Transfer failed to initialize: " + exception);
							runOnUiThread(new Runnable()
							{
								
								@Override
								public void run()
								{
									statusTextView.setText("Transfer failed to initialize.");
								}
							});
						}
					});
				}
			});

			builder.setNegativeButton("Cancel", new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}
	};
	
	private android.view.View.OnClickListener onRefreshClickListener = new android.view.View.OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			v.setEnabled(false);
			refreshUsers();
		}

	};

}
