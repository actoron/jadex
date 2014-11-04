package jadex.android.applications.demos;

import jadex.android.applications.demos.bdi.BDIDemoActivity;
import jadex.android.applications.demos.bdiv3.BDIV3DemoActivity;
import jadex.android.applications.demos.benchmark.BenchmarkDemoActivity;
import jadex.android.applications.demos.bpmn.BPMNDemoActivity;
import jadex.android.applications.demos.controlcenter.ControlCenterDemoActivity;
import jadex.android.applications.demos.event.EventDemoActivity;
import jadex.android.applications.demos.rest.RestDemoActivity;
import jadex.android.standalone.JadexClientLauncherActivity;
import jadex.android.standalone.clientapp.ClientAppFragment;
import jadex.bridge.IComponentIdentifier;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This Activity lets the user choose a demo to run.
 */
public class DemoChooserActivity extends ClientAppFragment
{
	
	//-------- attributes --------
	private Button launchBPMNButton;
	private Button launchBenchmarkButton;
	private Button launchEventButton;
	private Button launchBDIButton;
	private Button launchBDIV3Button;
	private Button launchRestButton;
	private Button launchControlCenterButton;

	protected IComponentIdentifier lastComponentIdentifier;

	//-------- methods --------
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main, container, false);

		launchBPMNButton = (Button) view.findViewById(R.id.main_launchBPMNDemoButton);
		launchBPMNButton.setOnClickListener(buttonListener);
		
		launchBDIButton = (Button) view.findViewById(R.id.main_launchBDIDemoButton);
		launchBDIButton.setOnClickListener(buttonListener);
		
		launchBDIV3Button = (Button) view.findViewById(R.id.main_launchBDIV3DemoButton);
		launchBDIV3Button.setOnClickListener(buttonListener);

		launchBenchmarkButton = (Button) view.findViewById(R.id.main_launchBenchmarkDemoButton);
		launchBenchmarkButton.setOnClickListener(buttonListener);

		launchEventButton = (Button) view.findViewById(R.id.main_launchEventDemoButton);
		launchEventButton.setOnClickListener(buttonListener);
		
		launchRestButton = (Button) view.findViewById(R.id.main_launchRestDemoButton);
		launchRestButton.setOnClickListener(buttonListener);
		
		launchControlCenterButton = (Button) view.findViewById(R.id.main_launchControlCenterButton);
		launchControlCenterButton.setOnClickListener(buttonListener);
		
		return view;
	}
	
	

	private OnClickListener buttonListener = new OnClickListener()
	{
		public void onClick(View view)
		{
			if (view == launchBPMNButton)
			{
				Intent i = new Intent(getContext(), BPMNDemoActivity.class);
				startActivity(i);
			} 
			else if (view == launchBDIButton)
			{
				Intent i = new Intent(getContext(), BDIDemoActivity.class);
				startActivity(i);
			}
			else if (view == launchBDIV3Button)
			{
				Intent i = new Intent(getContext(), BDIV3DemoActivity.class);
				startActivity(i);
			}
			else if (view == launchBenchmarkButton)
			{
				Intent i = new Intent(getContext(), BenchmarkDemoActivity.class);
				startActivity(i);
			}
			else if (view == launchRestButton) 
			{
				Intent i = new Intent(getContext(), RestDemoActivity.class);
				startActivity(i);
			}
			else if (view == launchEventButton)
			{
				Intent i = new Intent(getContext(), EventDemoActivity.class);
				startActivity(i);
			}
			else if (view == launchControlCenterButton)
			{
				Intent i = new Intent(getContext(), ControlCenterDemoActivity.class);
				startActivity(i);
			}
		}
	};

}