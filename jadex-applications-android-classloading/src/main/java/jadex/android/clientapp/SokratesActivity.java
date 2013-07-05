package jadex.android.clientapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jadex.android.clientapp.MyPlatformService.PlatformBinder;
import jadex.android.clientapp.MyPlatformService.SokratesListener;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.bdi.examples.puzzle.Board;
import jadex.bdi.examples.puzzle.Move;
import jadex.bdi.examples.puzzle.ui.SokratesView;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

public class SokratesActivity extends ClientAppFragment implements ServiceConnection
{
	private PlatformBinder service;
	private TextView statusTextView;
	private SokratesView sokratesView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.sokrates, container, false);

		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setTitle(R.string.app_title);
		Intent i = new Intent(getContext(), MyPlatformService.class);
		bindService(i, this, 0);
		View view = getView();
		sokratesView = (SokratesView) view.findViewById(R.id.sokrates_gameView);
		statusTextView = (TextView) view.findViewById(R.id.sokrates_statusTextView);
		statusTextView.setText("starting Platform...");
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder)
	{
		this.service = (MyPlatformService.PlatformBinder) binder;
		statusTextView.setText("starting Game...");

		this.service.setSokratesListener(sokratesListener);

		IFuture<Void> startSokrates = this.service.startSokrates();
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		this.service = null;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (service != null)
		{
			ThreadSuspendable suspendable = new ThreadSuspendable();
			service.stopSokrates().get(suspendable);
			unbindService(this);
		}
	}

	SokratesListener sokratesListener = new SokratesListener()
	{

		@Override
		public void setBoard(Board board)
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					statusTextView.setText("Game started!");
				}
			});
			sokratesView.setBoard(board);
		}

		@Override
		public void handleEvent(PropertyChangeEvent event)
		{
			Move move = (Move) event.getNewValue();
			sokratesView.performMove(move);
		}

		@Override
		public void showMessage(final String text)
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					statusTextView.setText(text);
				}
			});
		}

	};

}
