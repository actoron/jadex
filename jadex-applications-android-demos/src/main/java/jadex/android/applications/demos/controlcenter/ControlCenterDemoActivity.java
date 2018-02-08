package jadex.android.applications.demos.controlcenter;

import jadex.android.JadexAndroidActivity;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import android.content.Intent;

@Reference
public class ControlCenterDemoActivity extends JadexAndroidActivity
{
	public ControlCenterDemoActivity()
	{
		setPlatformAutostart(true);
//		setPlatformKernels(JadexPlatformOptions.DEFAULT_KERNELS);
		getPlatformConfiguration().setAwareness(true);
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		Intent i = new Intent(this, JadexAndroidControlCenter.class);
		i.putExtra("platformId", (BasicComponentIdentifier) platformId);
		startActivity(i);
	}
	
	
}
