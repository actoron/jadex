package jadex.android.applications.demos;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.bpmn.BPMNDemoActivity;
import jadex.android.applications.demos.event.EventActivity;
import jadex.bridge.IComponentIdentifier;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DemoChooserActivity extends Activity
{

	private Button launchBPMNButton;
	private Button launchBenchmarkButton;
	private Button launchEventButton;

	protected IComponentIdentifier lastComponentIdentifier;

	private TextView textView;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		launchBPMNButton = (Button) findViewById(R.id.main_launchBPMNDemoButton);
		launchBPMNButton.setOnClickListener(buttonListener);

		launchBenchmarkButton = (Button) findViewById(R.id.main_launchBenchmarkDemoButton);
		launchBenchmarkButton.setOnClickListener(buttonListener);
		
		launchEventButton = (Button) findViewById(R.id.main_launchEventDemo);
		launchEventButton.setOnClickListener(buttonListener);
		

	}

	// public boolean onCreateOptionsMenu(Menu menu)
	// {
	// menu.add(0, 0, 0, "Control Center");
	// return true;
	// }

	// public boolean onOptionsItemSelected(MenuItem item)
	// {
	// if (item.getItemId() == 0)
	// {
	// if (isJadexPlatformRunning())
	// {
	// Intent i = new Intent(this, JadexAndroidControlCenter.class);
	// i.putExtra("platformId", (ComponentIdentifier) platformId);
	// startActivity(i);
	// } else
	// {
	// runOnUiThread(new Runnable()
	// {
	// public void run()
	// {
	// Toast makeText = Toast.makeText(DemoChooserActivity.this,
	// "No Platform running!", Toast.LENGTH_SHORT);
	// makeText.show();
	// }
	// });
	// }
	// }
	// return true;
	// }

	private OnClickListener buttonListener = new OnClickListener()
	{

		public void onClick(View view)
		{
			if (view == launchBPMNButton)
			{
				Intent i = new Intent(DemoChooserActivity.this, BPMNDemoActivity.class);
//				i.putExtra(JadexAndroidActivity.EXTRA_PLATFORM_AUTOSTART, true);
				startActivity(i);

			} else if (view == launchBenchmarkButton)
			{
	
			} else {
				Intent i = new Intent(DemoChooserActivity.this, EventActivity.class);
//				i.putExtra(JadexAndroidActivity.EXTRA_PLATFORM_AUTOSTART, true);
				startActivity(i);
			}
		}
	};

}