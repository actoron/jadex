package de.unihamburg.vsis.jadexAndroid_test;

import de.unihamburg.vsis.jadexAndroid_test.chat.MeasureActivity;
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
	
	public ConfigurationItem[] configurations;

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
		setContentView(R.layout.main);
		exitButton = (Button) findViewById(R.id.main_exitButton);
		exitButton.setOnClickListener(this);
		startPlatformButton = (Button) findViewById(R.id.main_startPlatformButton);
		startPlatformButton.setOnClickListener(this);
		configurationSpinner = (Spinner) findViewById(R.id.configurationDropDown);
		
		ArrayAdapter<ConfigurationItem> adapter = new ArrayAdapter<ConfigurationItem>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		configurations = new ConfigurationItem[] {
				new ConfigurationItem("Performance Measurements", "performance"),
				new ConfigurationItem("Micro Agent Creation Test",
				"jadex/micro/benchmarks/AgentCreationAgent.class"),
				new ConfigurationItem("BPMN Creation Test",
				"jadex/bpmn/benchmarks/AgentCreation2.bpmn"),
				new ConfigurationItem("BDI Creation Test",
				"jadex/bdi/benchmarks/AgentCreation.agent.xml"),
				new ConfigurationItem("Awareness Notifier", "awareness"),
				new ConfigurationItem("Interactive Platform", "interactive")
		};

		for (ConfigurationItem item : configurations) {
			adapter.add(item);
		}
		
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
				} else if (conf.get_configFile().equals("performance")) {
					Intent i = new Intent(this, MeasureActivity.class);
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
