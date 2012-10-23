package jadex.android.exampleproject;

import jadex.android.JadexAndroidActivity;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.android.service.IJadexPlatformBinder;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	/** Handler to allow agents to create Toasts */
	public static Handler uiHandler;

	/**
	 * Constructor to set jadex platform parameters. 
	 */
	public HelloWorldActivity()
	{
		super();
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO);
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
		
		// create handler for agent ui output
		uiHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				Toast.makeText(HelloWorldActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
			}
			
		};
	}

	protected void onResume()
	{
		super.onResume();
		refreshButtons();
	}

	// BEGIN -------- show control center in menu ---------

	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 0, 0, "Control Center");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 0)
		{
			if (isJadexPlatformRunning())
			{
				Intent i = new Intent(this, JadexAndroidControlCenter.class);
				i.putExtra("platformId", (ComponentIdentifier) platformId);
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
		if (isJadexPlatformRunning())
		{
			textView.setText(R.string.started);
			textView.append(platformId.toString());
			startAgentButton.setEnabled(true);
			startPlatformButton.setText("Stop Platform");
		} else
		{
			startAgentButton.setEnabled(false);
			startPlatformButton.setText("Start Platform");
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
				if (isJadexPlatformRunning())
				{
					startPlatformButton.setEnabled(false);
					stopPlatforms();
					textView.setText(R.string.stopped);
					startPlatformButton.setEnabled(true);
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
				startMicroAgent("HelloWorldAgent " + num, AndroidAgent.class).addResultListener(agentCreatedResultListener);
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