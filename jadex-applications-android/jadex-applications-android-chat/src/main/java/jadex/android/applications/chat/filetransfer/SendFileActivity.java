package jadex.android.applications.chat.filetransfer;

import java.util.Collection;

import jadex.android.applications.chat.R;
import jadex.android.applications.chat.service.AndroidChatService;
import jadex.android.applications.chat.service.IAndroidChatService;
import jadex.android.applications.chat.service.AndroidChatService.ChatEventListener;
import jadex.android.applications.chat.ChatUser;
import jadex.android.applications.chat.ChatUserArrayAdapter;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.IResultListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SendFileActivity extends Activity implements ServiceConnection, ChatEventListener
{
	// --- UI Widgets ---
	private IAndroidChatService service;
	private ChatUserArrayAdapter adapter;
	private ListView contactsListView;
	private TextView statusTextView;
	private Button refreshButton;
	private TextView infoTextView;
	private TransferInfoItemWidget transferInfoItem;
	private LinearLayout bottomBar;

	// ---- private members ----
	private Handler uiHandler;
	private String path;
	private boolean transferring;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTitle(R.string.sendFile_title);
		uiHandler = new Handler();
		startService(new Intent(this, AndroidChatService.class));

		setContentView(R.layout.sendfile);

		transferInfoItem = (TransferInfoItemWidget) findViewById(R.id.sendfile_transferInfoItem);
		bottomBar = (LinearLayout) findViewById(R.id.sendfile_bottomBar);
		setTransferring(false);

		adapter = new ChatUserArrayAdapter(this);

		contactsListView = (ListView) findViewById(R.id.sendfile_contactslist);
		contactsListView.setAdapter(adapter);
		contactsListView.setOnItemClickListener(onChatUserClickListener);

		statusTextView = (TextView) findViewById(R.id.sendfile_statustext);

		refreshButton = (Button) findViewById(R.id.sendfile_refreshButton);
		refreshButton.setOnClickListener(onRefreshClickListener);
		infoTextView = (TextView) findViewById(R.id.sendfile_infotext);
	}


	@Override
	public void onResume()
	{
		super.onResume();
		refreshButton.setEnabled(false);
		setProgressBarIndeterminateVisibility(true);
		setProgressBarIndeterminate(true);

		Intent intent = getIntent();

		if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()))
		{
			Bundle extras = intent.getExtras();
			if (extras != null)
			{
				String text = (String) extras.get(Intent.EXTRA_TEXT);
				Uri uri = (Uri) extras.get(Intent.EXTRA_STREAM);
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

				this.path = path;
				infoTextView.setText("Sending file " + path);
			}
		}

		bindService(new Intent(this, AndroidChatService.class), this, 0);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unbindService(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		stopService(new Intent(this, AndroidChatService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Show Transfers");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		startActivity(new Intent(this, TransferActivity.class));
		return true;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder)
	{
		service = (IAndroidChatService) binder;
		service.addChatEventListener(this);
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
					Thread.sleep(10000);
				} catch (InterruptedException e)
				{
				}
				refreshUsers();
			}
		}).start();
	}

	@Override
	public boolean eventReceived(ChatEvent ce)
	{
		boolean processed = false;
		if (ce.getType() == ChatEvent.TYPE_FILE)
		{
			final TransferInfo ti = (TransferInfo) ce.getValue();
			if (ti.getFilePath().equals(path))
			{
				processed = true;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if (ti.getState().equals(TransferInfo.STATE_COMPLETED))
						{
							statusTextView.setText(R.string.sendFile_transferComplete);
							Toast.makeText(SendFileActivity.this, R.string.sendFile_transferComplete, Toast.LENGTH_LONG).show();
							setTransferring(false);
							finish();
						} else if (ti.getState().equals(TransferInfo.STATE_ABORTED))
						{
							statusTextView.setText(R.string.sendFile_transferAborted);
							setTransferring(false);
						} else if (ti.getState().equals(TransferInfo.STATE_ERROR))
						{
							setTransferring(false);
							statusTextView.setText(R.string.sendFile_transferFailed);
						} else if (ti.getState().equals(TransferInfo.STATE_CANCELLING))
						{
							statusTextView.setText(R.string.sendFile_transferAborted);
							setTransferring(false);
						} else if (ti.getState().equals(TransferInfo.STATE_TRANSFERRING))
						{
							setTransferring(true);
							transferInfoItem.updateFrom(ti);
						}
					}
				});
			}
		}
		return processed;
	}

	private void setTransferring(boolean value)
	{
		if (value != transferring)
		{
			transferring = value;
			if (transferring)
			{
				bottomBar.setVisibility(View.GONE);
				transferInfoItem.setVisibility(View.VISIBLE);
			} else
			{
				bottomBar.setVisibility(View.VISIBLE);
				transferInfoItem.setVisibility(View.GONE);
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
				statusTextView.setText(R.string.sendFile_loadingContacts);
				refreshButton.setEnabled(false);
				setProgressBarIndeterminateVisibility(true);
			}
		});

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						adapter.clear();
					}
				});
				Collection<ChatUser> users = service.getUserModel().getUsers();
				
				for (ChatUser chatUser : users) {
					adapter.add(chatUser);
				}
//				service.getUsers().addResultListener(new IntermediateDefaultResultListener<ChatUser>()
//				{
//
//					@Override
//					public void intermediateResultAvailable(final ChatUser chatUser)
//					{
//						uiHandler.post(new Runnable()
//						{
//
//							@Override
//							public void run()
//							{
//								adapter.add(chatUser);
//							}
//						});
//					}

//					@Override
//					public void finished()
//					{
//						super.finished();
						uiHandler.post(new Runnable()
						{
							@Override
							public void run()
							{
								setProgressBarIndeterminateVisibility(false);
								statusTextView.setText(R.string.sendFile_chooseReceiver);
								refreshButton.setEnabled(true);
							}
						});
//					}

//					@Override
//					public void exceptionOccurred(Exception exception)
//					{
//						super.exceptionOccurred(exception);
//						uiHandler.post(new Runnable()
//						{
//							@Override
//							public void run()
//							{
//								setProgressBarIndeterminateVisibility(false);
//								statusTextView.setText(R.string.sendFile_chooseReceiver);
//								refreshButton.setEnabled(true);
//							}
//						});
//					}
//				});
			}
		}).start();
	}

	// ------------ click listeners -------------

	private OnItemClickListener onChatUserClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if (transferring)
			{
				Toast.makeText(SendFileActivity.this, R.string.sendFile_alreadyTransferring, Toast.LENGTH_SHORT).show();
			} else
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(SendFileActivity.this);
				final ChatUser receiver = adapter.getItem(position);

				builder.setTitle(String.format(getResources().getString(R.string.sendFile_confirmSend), path, receiver.getNickName()));

				builder.setPositiveButton(R.string.sendFile_ok, new OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						setTransferring(true);
						dialog.dismiss();
						statusTextView.setText(String.format(getResources().getString(R.string.sendFile_transferAwaitingAccept), receiver));

						service.sendFile(path, receiver).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								runOnUiThread(new Runnable()
								{

									@Override
									public void run()
									{
										statusTextView.setText(R.string.sendFile_transferStarted);
										// startActivity(new
										// Intent(SendFileActivity.this,
										// TransferActivity.class));
									}
								});
							}

							public void exceptionOccurred(Exception exception)
							{
								System.err.println("Transfer failed to initialize: " + exception);
								setTransferring(false);
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										statusTextView.setText(R.string.sendFile_transferFailed);
									}
								});
							}
						});
					}
				});

				builder.setNegativeButton(R.string.sendFile_cancel, new OnClickListener()
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
