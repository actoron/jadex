package jadex.android.applications.chat;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.commons.future.IResultListener;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Window;

public class SendFileActivity extends Activity implements ServiceConnection, ChatEventListener
{
	private String path;
	private IAndroidChatService service;
	private ChatUserListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.sendfile);
		
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
				//
			}
		}

		adapter = new ChatUserListAdapter(getApplicationContext());

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
				List<ChatUser> users = service.getUsers();

				for (ChatUser chatUser : users)
				{
					adapter.add(chatUser);
				}

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						setProgressBarIndeterminateVisibility(false);
						Context applicationContext = getApplicationContext();
						AlertDialog.Builder builder = new AlertDialog.Builder(SendFileActivity.this);
						builder.setTitle("Choose send target");

						builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.dismiss();
								if (which != -1)
								{
									ChatUser user = adapter.getItem(which);
									System.out.println("Sending to: " + user);

									service.sendFile(path, user).addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											System.out.println("Transfer started.");
										}

										public void exceptionOccurred(Exception exception)
										{
											System.out.println("Transfer failed to initialize: " + exception);
										}
									});
								}
							}
						});
						AlertDialog alert = builder.create();
						alert.show();
					}
				});
			}
		}).start();
	}

	@Override
	public void eventReceived(ChatEvent ce)
	{
	}

}
