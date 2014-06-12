package jadex.android.applications.demos.bpmn;

import jadex.android.JadexAndroidActivity;
import jadex.android.commons.JadexPlatformOptions;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.os.Bundle;

/**
 * This Activity shows a sample BPMN Workflow beeing executed using Jadex.
 */
@Reference
public class BPMNDemoActivity extends JadexAndroidActivity
{
	/** Constructor */
	public BPMNDemoActivity()
	{
		super();
		setPlatformAutostart(true);
		setPlatformOptions("-deftimeout -1");
		setPlatformKernels(JadexPlatformOptions.KERNEL_MICRO, JadexPlatformOptions.KERNEL_COMPONENT, JadexPlatformOptions.KERNEL_BPMN);
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
		startBPMNAgent("SimpleWorkflow", "jadex/android/applications/demos/bpmn/SimpleWorkflow.bpmn2").addResultListener(bpmnCreatedResultListener);
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
