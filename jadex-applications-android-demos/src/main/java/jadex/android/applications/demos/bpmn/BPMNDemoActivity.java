package jadex.android.applications.demos.bpmn;

import java.util.Iterator;

import jadex.android.EventReceiver;
import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.bpmn.tasks.StartActivityEvent;
import jadex.android.commons.JadexPlatformOptions;
import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * This Activity shows a sample BPMN Workflow beeing executed using Jadex.
 */
@Reference
public class BPMNDemoActivity extends JadexAndroidActivity
{
	
	private Handler handler = new Handler();
	
	/** Constructor */
	public BPMNDemoActivity()
	{
		super();
		setPlatformAutostart(true);
		PlatformConfiguration config = getPlatformConfiguration();
		config.setDefaultTimeout(-1);
		config.getRootConfig().setKernels(RootComponentConfiguration.KERNEL.micro, RootComponentConfiguration.KERNEL.component, RootComponentConfiguration.KERNEL.bpmn);
		config.setPlatformName("bpmnDemoPlatform");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(jadex.android.applications.demos.R.layout.bpmn_demo);
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		registerEventReceiver(new EventReceiver<StartActivityEvent>(StartActivityEvent.class) {

			@Override
			public void receiveEvent(StartActivityEvent event) {
				final Intent intent = new Intent(BPMNDemoActivity.this, event.getActivityClass());
				// put all attributes into this intent
				Iterator<String> iter = event.getExtras().keySet().iterator();
				while(iter.hasNext())
				{
					String key = iter.next();
					intent.putExtra(key, event.getExtras().get(key));
				}
				
				// start the activity
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						BPMNDemoActivity.this.startActivity(intent);
					}
				});
			}
		});
		startBPMNAgent("SimpleWorkflow", "jadex/android/applications/demos/bpmn/SimpleWorkflow.bpmn2").addResultListener(bpmnCreatedResultListener);
		finish();
	}
	
	
	private IResultListener<IComponentIdentifier> bpmnCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{
		public void exceptionOccurred(Exception exception)
		{
			super.exceptionOccurred(exception);
		}
		
		public void resultAvailable(IComponentIdentifier arg0)
		{
			runOnUiThread(new Runnable()
			{

				public void run()
				{
				}
			});
		}
	};

}
