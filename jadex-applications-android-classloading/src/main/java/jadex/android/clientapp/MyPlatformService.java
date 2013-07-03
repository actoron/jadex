package jadex.android.clientapp;

import jadex.android.commons.JadexPlatformOptions;
import jadex.android.service.JadexPlatformService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class MyPlatformService extends JadexPlatformService
{
	private PlatformListener listener;
	private IComponentIdentifier platformId;
	private Handler handler;
	
	public interface PlatformListener
	{
		public void platformStarted();
		public void platformStarting();
	}

	public MyPlatformService()
	{
		setPlatformAutostart(false);
		setPlatformKernels(JadexPlatformOptions.KERNEL_MICRO, JadexPlatformOptions.KERNEL_COMPONENT, JadexPlatformOptions.KERNEL_BDI);
		setPlatformName("Sokrates");
		handler = new Handler();
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return new PlatformBinder();
	}
	
	public class PlatformBinder extends Binder {
		

		public IFuture<IExternalAccess> startPlatform() {
			return MyPlatformService.this.startPlatform();
		}
		
		public IFuture<IComponentIdentifier> startAgent() {
			return MyPlatformService.this.startComponent(platformId, "Component", "jadex/android/clientapp/bditest/HelloWorld.agent.xml");
		}
		
		public IFuture<IComponentIdentifier> startSokrates() {
			return MyPlatformService.this.startComponent(platformId, "Sokrates", "jadex/bdi/examples/puzzle/Sokrates.agent.xml");
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
	
	public void post(Runnable runnable)
	{
		handler.post(runnable);
	}

}
