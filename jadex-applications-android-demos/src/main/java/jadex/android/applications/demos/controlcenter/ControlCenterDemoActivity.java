package jadex.android.applications.demos.controlcenter;

import android.content.Intent;
import jadex.android.JadexAndroidActivity;
import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IExternalAccess;

public class ControlCenterDemoActivity extends JadexAndroidActivity
{
	public ControlCenterDemoActivity()
	{
		setPlatformAutostart(true);
		setPlatformKernels(JadexPlatformManager.DEFAULT_KERNELS);
		setPlatformOptions("-awareness true");
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		Intent i = new Intent(this, JadexAndroidControlCenter.class);
		i.putExtra("platformId", (ComponentIdentifier) platformId);
		startActivity(i);
	}
	
	
}
