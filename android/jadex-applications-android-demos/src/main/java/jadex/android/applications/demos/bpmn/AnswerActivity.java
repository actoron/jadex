package jadex.android.applications.demos.bpmn;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AnswerActivity extends Activity {
	
	public AnswerActivity()
	{
//		setPlatformAutostart(true);
	}
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer);
		
		final TextView textView = (TextView)findViewById(R.id.answerTextView);
		textView.setText(getIntent().getSerializableExtra("answer").toString());
	}
}