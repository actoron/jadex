package jadex.android.applications.chat;

import jadex.android.applications.chat.AndroidChatService.ChatEventListener;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.TransferInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.ComponentName;
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
		refreshTransfers();
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
				refreshTransfers();
			}
		}).start();
	}
	
	@Override
	public void eventReceived(ChatEvent ce)
	{
		if (ce.getType().equals(ChatEvent.TYPE_FILE)) {
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
			} else {
				refreshTransfers();
			}
		}
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
