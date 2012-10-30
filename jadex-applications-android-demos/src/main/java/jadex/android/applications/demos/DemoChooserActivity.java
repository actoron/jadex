package jadex.android.applications.demos;

import jadex.android.applications.demos.benchmark.BenchmarkDemoActivity;
import jadex.android.applications.demos.bpmn.BPMNDemoActivity;
import jadex.android.applications.demos.event.EventDemoActivity;
import jadex.bridge.IComponentIdentifier;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This Activity lets the user choose a demo to run.
 */
public class DemoChooserActivity extends Activity
{
	
	//-------- attributes --------
	private Button launchBPMNButton;
	private Button launchBenchmarkButton;
	private Button launchEventButton;

	protected IComponentIdentifier lastComponentIdentifier;

	//-------- methods --------
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

	private OnClickListener buttonListener = new OnClickListener()
	{
		public void onClick(View view)
		{
			if (view == launchBPMNButton)
			{
				Intent i = new Intent(DemoChooserActivity.this, BPMNDemoActivity.class);
				startActivity(i);
			} else if (view == launchBenchmarkButton)
			{
				Intent i = new Intent(DemoChooserActivity.this, BenchmarkDemoActivity.class);
				startActivity(i);
			} else
			{
				Intent i = new Intent(DemoChooserActivity.this, EventDemoActivity.class);
				startActivity(i);
			}
		}
	};

}