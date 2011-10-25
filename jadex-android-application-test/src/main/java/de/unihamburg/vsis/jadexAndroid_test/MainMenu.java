package de.unihamburg.vsis.jadexAndroid_test;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainMenu extends Activity implements OnClickListener {

	private static String TAG = "jadexAndroid-test";
	private Button exitButton;
	private Button startPlatformButton;
	private Spinner configurationSpinner;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
		exitButton = (Button) findViewById(R.id.main_exitButton);
		exitButton.setOnClickListener(this);
		startPlatformButton = (Button) findViewById(R.id.main_startPlatformButton);
		startPlatformButton.setOnClickListener(this);
		configurationSpinner = (Spinner) findViewById(R.id.configurationDropDown);

		// ArrayAdapter<CharSequence> adapter =
		// ArrayAdapter.createFromResource(this, R.array.configurations,
		// android.R.layout.simple_spinner_item);
		ArrayAdapter<ConfigurationItem> adapter = new ArrayAdapter<ConfigurationItem>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		adapter.add(new ConfigurationItem("Micro Agent Creation Test",
				"jadex/micro/benchmarks/AgentCreationAgent.class"));
		adapter.add(new ConfigurationItem("BPMN Creation Test",
				"jadex/bpmn/benchmarks/AgentCreation2.bpmn"));

		adapter.add(new ConfigurationItem("BDI Creation Test",
				"jadex/bdi/benchmarks/AgentCreation.agent.xml"));
		
		adapter.add(new ConfigurationItem("Awareness Notifier", "awareness"));
		
		adapter.add(new ConfigurationItem("Interactive Platform", "interactive"));

		configurationSpinner.setAdapter(adapter);

		configurationSpinner.setSelection(-1);
	}

	public void onClick(View arg0) {
		if (arg0 == exitButton) {
			this.finish();
		} else if (arg0 == startPlatformButton) {
			Object selectedItem = configurationSpinner.getSelectedItem();
			if (selectedItem != null) {
				ConfigurationItem conf = (ConfigurationItem) selectedItem;
				if (conf.get_configFile().equals("interactive")) {
					Intent i = new Intent(this, AgentActivity.class);
					MainMenu.this.startActivity(i);
				}
				else if (conf.get_configFile().equals("awareness")) {
					Intent i = new Intent(this, AwarenessActivity.class);
					MainMenu.this.startActivity(i);
				}
				else {
					Intent i = new Intent(this, Logger.class);
					i.putExtra("component", conf.get_configFile());
					MainMenu.this.startActivity(i);
				}
			}
		} else {
			Toast.makeText(this, "Please choose a configuration first",
					Toast.LENGTH_SHORT);
		}
	}

}
