package jadex.android.applications.demos.event;

import jadex.android.IEventReceiver;
import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends JadexAndroidActivity
{
	private Button pingAgentButton;
	private TextView descriptionTextView;
	protected IComponentIdentifier agentIdentifier;

	public EventActivity()
	{
		super();
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO);
		setPlatformName("eventDemoPlatform");
		setPlatformAutostart(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_demo);

		pingAgentButton = (Button) findViewById(R.id.event_pingAgentButton);
		descriptionTextView = (TextView) findViewById(R.id.event_demoDescription);

		pingAgentButton.setEnabled(false);
		pingAgentButton.setOnClickListener(onPingButtonClickListener);
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);

		registerEventReceiver(ShowToastEvent.TYPE, new IEventReceiver<ShowToastEvent>()
		{

			public void receiveEvent(final ShowToastEvent event)
			{
				runOnUiThread(new Runnable()
				{

					public void run()
					{
						Toast makeText = Toast.makeText(EventActivity.this, event.getMessage(), Toast.LENGTH_SHORT);
						makeText.show();
					}
				});
			}

			public Class<ShowToastEvent> getEventClass()
			{
				return ShowToastEvent.class;
			}

		});

		startMicroAgent("EventAgent", AndroidAgent.class).addResultListener(agentCreatedResultListener);
	}

	private IResultListener<IComponentIdentifier> agentCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{

		public void resultAvailable(IComponentIdentifier result)
		{
			agentIdentifier = result;
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					pingAgentButton.setEnabled(true);
				}
			});

		}
	};

	OnClickListener onPingButtonClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			HashMap<String, Object> msg = new HashMap<String, Object>();
			msg.put(SFipa.CONTENT, "ping");
			sendMessage(msg, agentIdentifier).addResultListener(new DefaultResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}

				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				};
			});
		}
	};

}
