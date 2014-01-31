package jadex.android.clientappdemo;

import jadex.android.service.JadexPlatformService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class PlatformService extends JadexPlatformService
{
	private PlatformListener listener;
	private IComponentIdentifier platformId;
	private Handler uiHandler;
	
	public interface PlatformListener
	{
		public void platformStarted();
		public void platformStarting();
	}

	public PlatformService()
	{
		setPlatformAutostart(false);
		setPlatformKernels(KERNEL_MICRO, KERNEL_COMPONENT, KERNEL_BDI);
		setPlatformName("ClientAppDemo");
		setSharedPlatform(true);
		uiHandler = new Handler();
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return new PlatformBinder();
	}
	
	public class PlatformBinder extends Binder {
		

		public IFuture<IExternalAccess> startPlatform() {
			return PlatformService.this.startJadexPlatform();
		}
		
		public IFuture<IComponentIdentifier> startAgent() {
			return PlatformService.this.startComponent(platformId, "HelloWorldAgent", "jadex/android/clientappdemo/agent/HelloWorld.agent.xml");
		}
		
		public void setPlatformListener (PlatformListener l) {
			listener = l;
		}
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		super.onPlatformStarted(platform);
		this.platformId = platform.getComponentIdentifier();
		listener.platformStarted();
	}
	
	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		listener.platformStarting();
	}
	
	/**
	 * Post a runnable to be executed on the UI Thread.
	 * @param runnable
	 */
	public void post(Runnable runnable)
	{
		uiHandler.post(runnable);
	}

}
