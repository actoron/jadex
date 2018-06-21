package jadex.android.applications.demos.bpmn;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.android.applications.demos.bpmn.tasks.ShowActivityWithResultTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QuestionActivity extends JadexAndroidActivity {
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		
		TextView textView = (TextView)findViewById(R.id.questionTextView);
		textView.setText(getIntent().getSerializableExtra("question")+"");
		
		final String[] choices = (String[])getIntent().getSerializableExtra("choices");
		final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.questionButtonGroup);
		for (int i=0; i < choices.length; i++)
		{
			RadioButton radio = new RadioButton(this);
			radio.setText(choices[i]);
			radio.setId(i); // set id to be the index of the array (used below)
			radioGroup.addView(radio);
		}
		((RadioButton)radioGroup.getChildAt(0)).setChecked(true); // make sure a radio button initially is selected
		
		final Button button = (Button)findViewById(R.id.questionButton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				String answer = choices[radioGroup.getCheckedRadioButtonId()];
				
				// get the original intent and append the given answer. 
				// "Extras" in this intent are forwarded as attributes to the next BPMN task
				Intent i = getIntent();
				i.putExtra("answer", answer);
				
				// inform calling task, that we are finished with this activity and give back the control flow
				ShowActivityWithResultTask.finish(i);
				finish();
			}
		});
	}
}