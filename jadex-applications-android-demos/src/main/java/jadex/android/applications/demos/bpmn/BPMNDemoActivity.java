package jadex.android.applications.demos.bpmn;

import jadex.android.JadexAndroidActivity;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.os.Bundle;

public class BPMNDemoActivity extends JadexAndroidActivity
{
	
	public BPMNDemoActivity()
	{
		super();
		setPlatformAutostart(true);
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO, JadexPlatformManager.KERNEL_COMPONENT, JadexPlatformManager.KERNEL_BPMN);
		setPlatformName("bpmnDemoPlatform");
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
		startBPMNAgent("SimpleWorkflow", "jadex/android/applications/demos/bpmn/SimpleWorkflow.bpmn").addResultListener(bpmnCreatedResultListener);
	}
	
	
	private IResultListener<IComponentIdentifier> bpmnCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{

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
