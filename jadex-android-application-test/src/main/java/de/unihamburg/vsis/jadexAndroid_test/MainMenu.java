package de.unihamburg.vsis.jadexAndroid_test;

import android.app.Activity;
import android.app.AlertDialog;
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
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
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
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.configurations, android.R.layout.simple_dropdown_item_1line);
        configurationSpinner.setAdapter(adapter);
    }

	public void onClick(View arg0) {
		 if (arg0 == exitButton) {
			 this.finish();
		 } else if (arg0 == startPlatformButton) {
			 if ("Micro Agent Creation Test".equals(configurationSpinner.getSelectedItem())) {
				 Intent i = new Intent(this, Logger.class);
				 i.putExtra("component", "jadex/micro/benchmarks/AgentCreationAgent.class");
				 startActivity(i);
			 } else if ("BPMN Creation Test".equals(configurationSpinner.getSelectedItem())) {
				 Intent i = new Intent(MainMenu.this, Logger.class);
				 i.putExtra("component", "jadex/bpmn/benchmarks/AgentCreation.bpmn");
				 MainMenu.this.startActivity(i);
			 } else {
				 Toast.makeText(this, "Please choose a configuration first", Toast.LENGTH_SHORT);
			 }
		 }
	}

}

