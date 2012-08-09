package jadex.android.application.demo;

import jadex.android.IEventReceiver;
import jadex.android.JadexAndroidActivity;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
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
	private Button pingButton;

	private int num;

	private Button startPlatformButton;
	protected IComponentIdentifier lastComponentIdentifier;
	
	private IComponentIdentifier platformID;

	private TextView textView;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		startPlatformButton = (Button) findViewById(R.id.startPlatformButton);
		startPlatformButton.setOnClickListener(buttonListener);

		startAgentButton = (Button) findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(buttonListener);

		startBPMNButton = (Button) findViewById(R.id.startBpmnButton);
		startBPMNButton.setOnClickListener(buttonListener);

		pingButton = (Button) findViewById(R.id.pingButton);
		pingButton.setOnClickListener(buttonListener);
		
		textView = (TextView) findViewById(R.id.infoTextView);

		registerEventReceiver(ShowToastEvent.TYPE,
				new IEventReceiver<ShowToastEvent>() {

					public void receiveEvent(final ShowToastEvent event) {
						runOnUiThread(new Runnable() {

							public void run() {
								Toast makeText = Toast.makeText(
										JadexAndroidHelloWorldActivity.this,
										event.getMessage(), Toast.LENGTH_SHORT);
								makeText.show();
							}
						});
					}

					public Class<ShowToastEvent> getEventClass() {
						return ShowToastEvent.class;
					}

				});
	}

	protected void onResume() {
		super.onResume();
		refreshButtons();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Control Center");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			if (isJadexPlatformRunning()) {
				Intent i = new Intent(this, JadexAndroidControlCenter.class);
				i.putExtra("platformId", (ComponentIdentifier) platformID);
				startActivity(i);
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast makeText = Toast.makeText(
								JadexAndroidHelloWorldActivity.this,
								"No Platform running!", Toast.LENGTH_SHORT);
						makeText.show();
					}
				});
			}
		}
		return true;
	}

	private void refreshButtons() {
		if (isJadexPlatformRunning()) {
			startPlatformButton.setText("Stop Platform");
			startAgentButton.setEnabled(true);
			startBPMNButton.setEnabled(true);
			pingButton.setEnabled(true);
		} else {
			startPlatformButton.setText("Start Platform");
			startAgentButton.setEnabled(false);
			startBPMNButton.setEnabled(false);
			pingButton.setEnabled(false);
		}
		startPlatformButton.setEnabled(true);
	}

	private OnClickListener buttonListener = new OnClickListener() {

		public void onClick(View view) {
			if (view == startPlatformButton) {
				if (isJadexPlatformRunning()) {
					new Thread(new Runnable()
					{
						public void run()
						{
							getJadexContext().shutdownJadexPlatform(platformID);
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									textView.setText("Platform stopped.");
									refreshButtons();
								}
							});
						}
					}).start();
				} else {
					startPlatformButton.setEnabled(false);
					textView.setText("Starting Jadex Platform...");
					getJadexContext().startJadexPlatform().addResultListener(
							platformResultListener);
				}
			} else if (view == startAgentButton) {
				startAgentButton.setEnabled(false);
				num++;
				startMicroAgent("HelloWorldAgent " + num, AndroidAgent.class)
						.addResultListener(agentCreatedResultListener);

			} else if (view == startBPMNButton) {
				startBPMNButton.setEnabled(false);
				num++;
				startBPMNAgent("SimpleWorkflow " + num,
						"jadex/android/application/demo/bpmn/SimpleWorkflow.bpmn")
						.addResultListener(bpmnCreatedResultListener);
			} else if (view == pingButton) {
				HashMap<String, Object> msg = new HashMap<String,Object>();
//				msg.put(SFipa.FIPA_MESSAGE_TYPE.getReceiverIdentifier(), lastComponentIdentifier);
				msg.put(SFipa.CONTENT, "ping");
//				msg.put(SFipa.PERFORMATIVE, SFipa.INFORM);
				
				sendMessage(msg, lastComponentIdentifier).addResultListener(new DefaultResultListener<Void>() {
					public void resultAvailable(Void result) {
					}
					
					public void exceptionOccurred(Exception exception) {
						exception.printStackTrace();
					};
				});
			}
		}
	};

	private IResultListener<IExternalAccess> platformResultListener = new DefaultResultListener<IExternalAccess>() {

		public void resultAvailable(IExternalAccess result) {
			platformID = result.getComponentIdentifier();
			runOnUiThread(new Runnable() {
				public void run() {
					textView.setText("Platform started: " + platformID);
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
					startAgentButton.setEnabled(true);
					
				}
			});
			lastComponentIdentifier = arg0;
		}
	};

	private IResultListener<IComponentIdentifier> bpmnCreatedResultListener = new DefaultResultListener<IComponentIdentifier>() {

		public void resultAvailable(IComponentIdentifier arg0) {
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("BPMN component started: " + num);
					startBPMNButton.setEnabled(true);
				}
			});
		}
	};

}