package jadex.android.exampleproject.simple;

import jadex.android.EventReceiver;
import jadex.android.JadexAndroidActivity;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.android.exampleproject.MyEvent;
import jadex.android.exampleproject.R;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Hello World Activity.
 * Can Launch a platform and run agents.
 */
public class HelloWorldActivity extends JadexAndroidActivity
{

	/** UI Button */
	private Button startAgentButton;
	/** UI Button */
	private Button startPlatformButton;
	/** UI textView */
	private TextView textView;

	/**
	 * Constructor to set jadex platform parameters. 
	 */
	public HelloWorldActivity()
	{
		super();
		setPlatformKernels(JadexPlatformOptions.KERNEL_MICRO);
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// lookup all ui elements:

		startAgentButton = (Button) findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(buttonListener);

		startPlatformButton = (Button) findViewById(R.id.startPlatformButton);
		startPlatformButton.setOnClickListener(buttonListener);

		textView = (TextView) findViewById(R.id.infoTextView);
	}

	protected void onResume()
	{
		super.onResume();
		refreshButtons();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	// BEGIN -------- show control center in menu ---------

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem controlCenterMenuItem = menu.add(0, 0, 0, "Control Center");
		controlCenterMenuItem.setIcon(android.R.drawable.ic_menu_manage);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 0)
		{
			if (isPlatformRunning())
			{
				Intent i = new Intent(this, JadexAndroidControlCenter.class);
				i.putExtra("platformId", (BasicComponentIdentifier) platformId);
				startActivity(i);
			} else
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						Toast makeText = Toast.makeText(HelloWorldActivity.this, "No Platform running!", Toast.LENGTH_SHORT);
						makeText.show();
					}
				});
			}
		}
		return true;
	}

	// END -------- show control center in menu ---------

	/**
	 * Set correct button state/label.
	 */
	private void refreshButtons()
	{
		if (isPlatformRunning())
		{
			textView.setText(R.string.started);
			textView.append(platformId.toString());
			startAgentButton.setEnabled(true);
			startPlatformButton.setText("Stop Platform");
		} else
		{
			startAgentButton.setEnabled(false);
			startPlatformButton.setText("Start Platform");
			textView.setText(R.string.stopped);
		}
	}

	private OnClickListener buttonListener = new OnClickListener()
	{
		/** agent counter */
		private int num;

		public void onClick(View view)
		{
			if (view == startPlatformButton)
			{
				if (isPlatformRunning())
				{
					startPlatformButton.setEnabled(false);
					Thread thread = new Thread() {
						public void run() {
							stopPlatforms();
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
					startPlatform();
				}
			} else if (view == startAgentButton)
			{
				startAgentButton.setEnabled(false);
				num++;
				startMicroAgent("HelloWorldAgent " + num, MyAgent.class).addResultListener(agentCreatedResultListener);
			}
		}

	};

	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		textView.setText(R.string.starting);
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		platformId = result.getComponentIdentifier();
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				textView.setText(R.string.started);
				textView.append(platformId.toString());
				startPlatformButton.setEnabled(true);
				refreshButtons();
			}
		});
		
		
		// create handler for agent ui output
		registerEventReceiver(new EventReceiver<MyEvent>(MyEvent.class)
		{
	
			public void receiveEvent(final MyEvent event)
			{
				runOnUiThread(new Runnable()
				{
					
					public void run()
					{
						Toast.makeText(HelloWorldActivity.this, event.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	private IResultListener<IComponentIdentifier> agentCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{

		public void resultAvailable(final IComponentIdentifier cid)
		{
			runOnUiThread(new Runnable()
			{

				public void run()
				{
					textView.setText("Agent started: " + cid.toString());
					startAgentButton.setEnabled(true);
				}
			});
		}
	};

}