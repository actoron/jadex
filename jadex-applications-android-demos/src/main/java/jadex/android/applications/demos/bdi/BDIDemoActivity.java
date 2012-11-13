package jadex.android.applications.demos.bdi;

import jadex.android.JadexAndroidActivity;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.os.Bundle;

/**
 * This Activity shows a sample BPMN Workflow beeing executed using Jadex.
 */
public class BDIDemoActivity extends JadexAndroidActivity
{
	/** Constructor */
	public BDIDemoActivity()
	{
		super();
		setPlatformAutostart(true);
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO, JadexPlatformManager.KERNEL_COMPONENT, JadexPlatformManager.KERNEL_BDI);
		setPlatformName("bdiDemoPlatform");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(jadex.android.applications.demos.R.layout.bdi_demo);
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		startBDIAgent("HelloWorldAgent", "jadex/android/applications/demos/bdi/HelloWorld.agent.xml").addResultListener(bdiCreatedResultListener);
	}
	
	
	private IResultListener<IComponentIdentifier> bdiCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{
		public void resultAvailable(final IComponentIdentifier bdiId)
		{
			getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
			{

				@Override
				public void resultAvailable(IComponentManagementService result)
				{
				}
			});
		}
	};

}
