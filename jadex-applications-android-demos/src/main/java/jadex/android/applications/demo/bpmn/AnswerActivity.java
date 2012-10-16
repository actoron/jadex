package jadex.android.applications.demo.bpmn;

import jadex.android.JadexAndroidActivity;
import jadex.android.application.demo.R;
import android.os.Bundle;
import android.widget.TextView;

public class AnswerActivity extends JadexAndroidActivity {
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer);
		
		final TextView textView = (TextView)findViewById(R.id.answerTextView);
		textView.setText(getIntent().getSerializableExtra("answer").toString());
	}
}