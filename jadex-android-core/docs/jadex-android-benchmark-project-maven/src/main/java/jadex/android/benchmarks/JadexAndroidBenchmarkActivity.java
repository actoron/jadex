package jadex.android.benchmarks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *  Activity (screen) for the jadex android benchmark app.
 *  Allows launching the different benchmarks.
 */
public class JadexAndroidBenchmarkActivity extends Activity
{
	//-------- attributes --------
	
	/** Button to start the first message performance test. */
	private Button startMB1;
	
	/** Button to start the second message performance test. */
	private Button startMB2;
	
	/** Button to start the third message performance test. */
	private Button startMB3;
	
	/** The text view for showing results. */
	private TextView textView;
	
	//-------- methods --------

	/**
	 *  Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		startMB1 = (Button)findViewById(R.id.startMB1);
		startMB2 = (Button)findViewById(R.id.startMB2);
		startMB3 = (Button)findViewById(R.id.startMB3);
		startMB1.setOnClickListener(buttonListener);
		startMB2.setOnClickListener(buttonListener);
		startMB3.setOnClickListener(buttonListener);
		
		textView = (TextView) findViewById(R.id.infoTextView);
	}
	
	//-------- helper methods --------

	/**
	 *  The button listener starts a benchmarks and re-enables buttons
	 *  when the benchmark has finished.
	 */
	private OnClickListener buttonListener = new OnClickListener()
	{
		public void onClick(final View view)
		{
			startMB1.setEnabled(false);
			startMB2.setEnabled(false);
			startMB3.setEnabled(false);
			String	text	= textView.getText().toString();
			if(view==startMB1)
			{
				text	+= "\nRunning receiving benchmark...";
			}
			else if(view==startMB2)
			{
				text	+= "\nRunning sending benchmark...";
			}
			else if(view==startMB3)
			{
				text	+= "\nRunning single con sending benchmark...";
			}
			textView.setText(text);
			
			final String	ftext	= text;
			new Thread(new Runnable()
			{
				public void run()
				{
					String	text	= ftext;
					try
					{
						if(view==startMB1)
						{
							text	+= "\n"+ReceivingBenchmark.runBenchmark();
						}
						else if(view==startMB2)
						{
							text	+= "\n"+SendingBenchmark.runBenchmark();
						}
						else if(view==startMB3)
						{
							text	+= "\n"+SingleConSendingBenchmark.runBenchmark();
						}
					}
					catch(Exception e)
					{
						text	+= "\nBenchmark failed: "+e;
					}

					final String	ftext	= text;
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							textView.setText(ftext);
							startMB1.setEnabled(true);
							startMB2.setEnabled(true);
							startMB3.setEnabled(true);
						}
					});
				}
			}).start();
		}
	};
}