package jadex.android.exampleproject.extended;

import jadex.android.exampleproject.R;
import jadex.android.exampleproject.extended.MyJadexService.MyPlatformListener;
import jadex.android.exampleproject.extended.MyJadexService.MyServiceInterface;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Hello World Activity.
 * Can Launch a platform and run agents.
 */
public class HelloWorldActivity extends Activity implements ServiceConnection
{

	/** UI Button */
	private Button startAgentButton;
	/** UI Button */
	private Button startPlatformButton;
	/** UI textView */
	private TextView textView;
	/** Intent to connect to service **/
	private Intent serviceIntent;
	/** service binder **/
	private MyServiceInterface myService;
	/** id of the platform **/
	protected String platformId;

	/**
	 * Constructor to set jadex platform parameters. 
	 */
	public HelloWorldActivity()
	{
		super();
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.serviceIntent = new Intent(this, MyJadexService.class);
		setContentView(R.layout.main);

		// lookup all ui elements:

		startAgentButton = (Button) findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(buttonListener);

		startPlatformButton = (Button) findViewById(R.id.startPlatformButton);
		startPlatformButton.setOnClickListener(buttonListener);

		textView = (TextView) findViewById(R.id.infoTextView);
		startService(serviceIntent); // this will cause the service to stay in background
	}

	protected void onResume()
	{
		super.onResume();
		bindService(serviceIntent, this, 0);
		refreshButtons();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		unbindService(this); // This won't cause the platform to stop!
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	

	/**
	 * Called when the MyJadexService is started and bound.
	 */
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.myService = (MyServiceInterface) service;
		myService.setPlatformListener(new MyPlatformListener()
		{
			
			public void onPlatformStarting()
			{
				runOnUiThread(new Runnable()
				{
					
					public void run()
					{
						textView.setText(R.string.starting);
					}
				});
			}
			
			public void onPlatformStarted()
			{
				platformId = myService.getPlatformId();
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						refreshButtons();
					}
				});
			}

			public void onHelloWorldAgentStarted(final String name)
			{
				runOnUiThread(new Runnable()
				{

					public void run()
					{
						textView.setText("Agent started: " + name);
						startAgentButton.setEnabled(true);

					}
				});
			}
		});
	}

	/**
	 * Called when MyJadexService disconnects (because we called unbind or stop)
	 */
	public void onServiceDisconnected(ComponentName name)
	{
		this.myService = null;
	}

	/**
	 * Set correct button state/label.
	 */
	private void refreshButtons()
	{
		if (isJadexPlatformRunning())
		{
			textView.setText(R.string.started);
			textView.append(platformId.toString());
			startAgentButton.setEnabled(true);
			startPlatformButton.setText("Stop Platform");
			startPlatformButton.setEnabled(true);
		} else
		{
			startAgentButton.setEnabled(false);
			startPlatformButton.setText("Start Platform");
			startPlatformButton.setEnabled(true);
			textView.setText(R.string.stopped);
		}
	}



	private OnClickListener buttonListener = new OnClickListener()
	{


		public void onClick(View view)
		{
			if (view == startPlatformButton)
			{
				if (isJadexPlatformRunning())
				{
					startPlatformButton.setEnabled(false);
					Thread thread = new Thread() {
						public void run() {
							myService.stopPlatforms();
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									refreshButtons();
									startPlatformButton.setEnabled(true);
								}
							});
						};
					};
					thread.start();
				} else
				{
					startPlatformButton.setEnabled(false);
					textView.setText(R.string.starting);
					myService.startPlatform();
				}
			} else if (view == startAgentButton)
			{
				startAgentButton.setEnabled(false);
				myService.startHelloWorldAgent();
			}
		}
	};
	
	private boolean isJadexPlatformRunning()
	{
		return myService != null && myService.isJadexPlatformRunning();
	}
}