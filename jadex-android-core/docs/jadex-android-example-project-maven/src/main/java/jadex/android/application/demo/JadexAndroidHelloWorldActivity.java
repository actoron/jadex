package jadex.android.application.demo;

import jadex.android.IEventReceiver;
import jadex.android.JadexAndroidActivity;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JadexAndroidHelloWorldActivity extends JadexAndroidActivity {
	
	private Button startAgentButton;
	private Button startBPMNButton;
	
	private int num;
	
	private Button startPlatformButton;

	private TextView textView;
	
	private static Handler handler;
	
//	public static Handler getHandler() {
//		return handler;
//	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		startPlatformButton = (Button) findViewById(R.id.startPlatformButton);
		startPlatformButton.setOnClickListener(buttonListener);
		
		startAgentButton = (Button) findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(buttonListener);
		startAgentButton.setEnabled(false);
		
		startBPMNButton = (Button) findViewById(R.id.startBpmnButton);
		startBPMNButton.setOnClickListener(buttonListener);
		startBPMNButton.setEnabled(false);
		
		textView = (TextView) findViewById(R.id.infoTextView);
		
//		handler = new Handler() {
//			@Override
//			public void handleMessage(final Message msg) {
//				runOnUiThread(new Runnable() {
//
//					public void run() {
//						Toast makeText = Toast.makeText(JadexAndroidHelloWorldActivity.this,
//								msg.getData().getString("text"), Toast.LENGTH_SHORT);
//						makeText.show();
//					}
//				});
//			}
//		};
		
		registerEventReceiver(ShowToastEvent.TYPE, new IEventReceiver<ShowToastEvent>() {

			@Override
			public void receiveEvent(final ShowToastEvent event) {
				runOnUiThread(new Runnable() {

					public void run() {
						Toast makeText = Toast.makeText(JadexAndroidHelloWorldActivity.this,
								event.getMessage(), Toast.LENGTH_SHORT);
						makeText.show();
					}
				});
			}

			@Override
			public Class<ShowToastEvent> getEventClass() {
				return ShowToastEvent.class;
			}

		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshButtons();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0, "Control Center");
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			Intent i = new Intent(this, JadexAndroidControlCenter.class);
			startActivity(i);
		}
		return true;
	}
	
	private void refreshButtons() {
		if (isJadexPlatformRunning()) {
			startPlatformButton.setText("Stop Platform");
			startAgentButton.setEnabled(true);
			startBPMNButton.setEnabled(true);
		} else {
			startPlatformButton.setText("Start Platform");
			startAgentButton.setEnabled(false);
			startBPMNButton.setEnabled(false);
		}
		startPlatformButton.setEnabled(true);
	}
	
	private OnClickListener buttonListener = new OnClickListener() {

		public void onClick(View view) {
			if (view == startPlatformButton) {
				if (isJadexPlatformRunning()) {
					shutdownJadexPlatform();
					textView.setText("Platform stopped.");
					refreshButtons();
				} else {
					startPlatformButton.setEnabled(false);
					textView.setText("Starting Jadex Platform...");
					startJadexPlatform().addResultListener(platformResultListener);
				}
			} else if (view == startAgentButton) {
				startAgentButton.setEnabled(false);
				startMicroAgent("HelloWorldAgent " + num, AndroidAgent.class).addResultListener(agentCreatedResultListener);
				
			} else if (view == startBPMNButton) {
				startBPMNButton.setEnabled(false);
				startBPMNAgent("SimpleWorkflow " + num, "jadex/android/application/demo/bpmn/SimpleWorkflow.bpmn").addResultListener(bpmnCreatedResultListener);
			}
		}
	};

	private IResultListener<IExternalAccess> platformResultListener = new DefaultResultListener<IExternalAccess>() {

		public void resultAvailable(IExternalAccess result) {
			runOnUiThread(new Runnable() {
				public void run() {
					
					textView.setText("Platform started");
					refreshButtons();
				}
			});
		}
	};

	private IResultListener<IComponentIdentifier> agentCreatedResultListener = new DefaultResultListener<IComponentIdentifier>() {

		public void resultAvailable(IComponentIdentifier arg0) {
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("Agents started: " + num);
					num++;
					startAgentButton.setEnabled(true);
				}
			});
		}
	};
	
	private IResultListener<IComponentIdentifier> bpmnCreatedResultListener = new DefaultResultListener<IComponentIdentifier>() {

		public void resultAvailable(IComponentIdentifier arg0) {
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("BPMN component started: " + num);
					num++;
					startBPMNButton.setEnabled(true);
				}
			});
		}
	};
	
}