package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.Starter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelloAndroidActivity extends Activity implements OnClickListener {

    private static String TAG = "jadexAndroid-test";
	private Button exitButton;
	private Button startPlatformButton;

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
        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);
        startPlatformButton = (Button) findViewById(R.id.startPlatformButton);
        startPlatformButton.setOnClickListener(this);
    }

	public void onClick(View arg0) {
		 if (arg0 == exitButton) {
			 this.finish();
		 } else if (arg0 == startPlatformButton) {
			 System.out.println("Creation Jadex Platform...");
			 Startup.createPlatform();
		 }
	}

}

