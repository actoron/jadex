package jadex.android.applications.chat.filetransfer;

import jadex.android.applications.chat.fragments.ChatFragment;
import jadex.android.applications.chat.service.AndroidChatService;
import jadex.android.applications.chat.service.IAndroidChatService;
import jadex.android.applications.chat.service.AndroidChatService.ChatEventListener;
import jadex.android.applications.chat.R;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.DefaultResultListener;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;

public class TransferActivity extends ListActivity implements ServiceConnection, ChatEventListener
{

	public static final String EXTRA_KEY_TRANSFERINFO = "EXTRA_TRANSFERINFO";
	public static final String EXTRA_KEY_OTHERNICK = "OTHERNICK";
	public static final String EXTRA_KEY_METHOD = "EXTRA_METHOD";
	public static final String EXTRA_METHOD_CREATE = "accept";
	
	private IAndroidChatService service;
	private TransferInfoArrayAdapter adapter;
	private Button refreshButton;
	private Map<String, TransferInfo> transfers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle("Jadex Chat Transfers");
		setContentView(R.layout.transferinfo);
		transfers = new HashMap<String, TransferInfo>();
		adapter = new TransferInfoArrayAdapter(this);
		this.getListView().setLongClickable(true);
		this.getListView().setOnItemLongClickListener(itemLongClickListener);
		this.setListAdapter(adapter);
		
		refreshButton = (Button) findViewById(R.id.transferinfo_refreshButton);
		refreshButton.setOnClickListener(onRefreshClickListener);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
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
		service.addChatEventListener(this);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		service.removeMessageListener(this);
		service = null;
	}

	@Override
	public void chatConnected()
	{
		String method = getIntent().getStringExtra(EXTRA_KEY_METHOD);
		if (method != null) {
			if (EXTRA_METHOD_CREATE.equals(method)) {
				TransferInfo ti = (TransferInfo) getIntent().getSerializableExtra(EXTRA_KEY_TRANSFERINFO);
				String nick = getIntent().getStringExtra(EXTRA_KEY_OTHERNICK);
				showAcceptFileDialog(ti, nick);
			}
		}

		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				refreshTransfers();
			}
		}).start();
	}
	
	@Override
	public boolean eventReceived(ChatEvent ce)
	{
		boolean eventProcessed = false;
		if (ce.getType().equals(ChatEvent.TYPE_FILE)) {
			eventProcessed = true;
			TransferInfo ti = (TransferInfo) ce.getValue();
			TransferInfo existingTi = transfers.get(ti.getId());
			if (existingTi != null) {
				existingTi.setDone(ti.getDone());
				existingTi.setSpeed(ti.getSpeed());
				existingTi.setState(ti.getState());
				existingTi.setTimeout(ti.getTimeout());
				runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
					}
				});
			} else if (ti.getState().equals(TransferInfo.STATE_WAITING)) {
				showAcceptFileDialog(ti, ce.getNick());
			} else {
				refreshTransfers();
			}
		}
		return eventProcessed;
	}

	protected void refreshTransfers()
	{
		final Collection<TransferInfo> transfers = service.getTransfers();
		
		runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				adapter.clear();
				for (TransferInfo transferInfo : transfers)
				{
					TransferActivity.this.transfers.put(transferInfo.getId(), transferInfo);
					adapter.add(transferInfo);
				}
			}
		});
	}
	
	private void showAcceptFileDialog(final TransferInfo ti, String nick)
	{
		if (ti.getState().equals(TransferInfo.STATE_WAITING))
		{
			final Builder builder = new AlertDialog.Builder(TransferActivity.this);
			builder.setTitle("Incoming Filetransfer");
			builder.setMessage(nick + " wants to send you " + ti.getFileName() + ".\n" + "Do you want to download this file?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					service.acceptFileTransfer(ti).addResultListener(new DefaultResultListener<Void>()
					{

						@Override
						public void resultAvailable(Void result)
						{
							runOnUiThread(new Runnable()
							{

								@Override
								public void run()
								{
//									refreshTransfers();
								}
							});
						}

						@Override
						public void exceptionOccurred(Exception exception)
						{
							super.exceptionOccurred(exception);
						}

					});
				}

			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					service.rejectFileTransfer(ti);
				}
			});
			
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					builder.create().show();
				}
			});
		}
	}

	
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener()
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			return false;
		}
	};
	
	private OnClickListener onRefreshClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v)
		{
			refreshTransfers();
		}
	};
}
