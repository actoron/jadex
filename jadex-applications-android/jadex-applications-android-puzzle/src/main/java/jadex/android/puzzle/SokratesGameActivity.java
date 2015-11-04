package jadex.android.puzzle;

import jadex.android.puzzle.SokratesService.PlatformBinder;
import jadex.android.puzzle.SokratesService.SokratesListener;
import jadex.android.puzzle.ui.SokratesView;
import jadex.bdiv3.examples.puzzle.IBoard;
import jadex.bdiv3.examples.puzzle.Move;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.DefaultResultListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class SokratesGameActivity extends Activity implements ServiceConnection
{
	protected static final String BDI = "BDI";
	protected static final String BDIBenchmark = "BDIBenchmark";
	protected static final String BDIV3 = "BDIV3";
	protected static final String BDIV3Benchmark = "BDIV3Benchmark";
	
	private PlatformBinder service;
	private TextView statusTextView;
	private SokratesView sokratesView;
	
	private Intent serviceIntent;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_title);
		serviceIntent = new Intent(this, SokratesService.class);
		setContentView(R.layout.sokrates);
		sokratesView = (SokratesView) findViewById(R.id.sokrates_gameView);
		statusTextView = (TextView) findViewById(R.id.sokrates_statusTextView);
	}


	@Override
	public void onResume()
	{
		super.onResume();

		bindService(serviceIntent, this, 0);
		statusTextView.setText("starting Platform...");
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (!this.isFinishing()) {
			// if isFinishing, onDestroy will be called where
			// we want to stop sokrates before unbinding
			unbindService(this);
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder)
	{
		this.service = (SokratesService.PlatformBinder) binder;
		this.service.setSokratesListener(sokratesListener);
		String mode = getIntent().getStringExtra("mode");
		
		if (!service.isSokratesRunning()) {
			statusTextView.setText("starting Game in mode: " + mode);
			if (mode.equals(BDI)) {
				this.service.startSokrates();
			} else if (mode.equals(BDIBenchmark)) {
				this.service.startSokratesBench();
			} else if (mode.equals(BDIV3)) {
				this.service.startSokratesV3();
			} else if (mode.equals(BDIV3Benchmark)) {
				this.service.startSokratesV3Bench();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		this.service = null;
	}

	@Override
	public void onDestroy()
	{
		if (service != null)
		{
			service.stopSokrates().addResultListener(new DefaultResultListener<Void>() {
				@Override
				public void resultAvailable(Void result) {
					unbindService(SokratesGameActivity.this);
				}
			});
		}
		super.onDestroy();
	}

	SokratesListener sokratesListener = new SokratesListener()
	{
		@Override
		public void setBoard(IBoard board)
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
