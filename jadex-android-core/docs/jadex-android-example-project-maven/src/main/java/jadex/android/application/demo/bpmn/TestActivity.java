package jadex.android.application.demo.bpmn;

import jadex.android.JadexAndroidActivity;
import jadex.android.application.demo.R;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends JadexAndroidActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		TextView textView = (TextView)findViewById(R.id.testTextView);
		textView.setText(getIntent().getSerializableExtra("text")+"");
	}
}