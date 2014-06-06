package jadex.android.clientappdemo;

import jadex.android.EventReceiver;
import jadex.android.clientappdemo.microagent.IAgentInterface;
import jadex.android.clientappdemo.microagent.MyAgent;
import jadex.android.clientappdemo.microagent.MyEvent;
import jadex.android.service.JadexPlatformService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class PlatformService extends JadexPlatformService
{
	private PlatformListener listener;
	private Handler uiHandler;
	private boolean	platformStarted;

	public PlatformService()
	{
		setPlatformAutostart(true);
		// setPlatformKernels(KERNEL_MICRO, KERNEL_COMPONENT);
		// setPlatformName("ClientAppDemo");
		setSharedPlatform(true);
		uiHandler = new Handler();

		registerEventReceiver(new EventReceiver<MyEvent>(MyEvent.class)
		{
			@Override
			public void receiveEvent(final MyEvent event)
			{
				uiHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new PlatformBinder();
	}

	public class PlatformBinder extends Binder
	{

		public IFuture<IComponentIdentifier> startAgent()
		{
			return PlatformService.this.startComponent("MyAgent", MyAgent.class);
			// return PlatformService.this.startComponent(platformId,
			// "HelloWorldAgent",
			// "jadex/android/clientappdemo/agent/HelloWorld.agent.xml");
		}

		public void callAgent(final String message)
		{
			IFuture<IAgentInterface> fut = getService(IAgentInterface.class);
			fut.addResultListener(new DefaultResultListener<IAgentInterface>()
			{

				public void resultAvailable(IAgentInterface result)
				{
					result.callAgent(message);
				}
			});
		}

		public void setPlatformListener(PlatformListener l)
		{
			listener = l;
			l.platformStarting(); // because it's autostart.
			if (platformStarted) {
				l.platformStarted();
			}
		}
	}

	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		super.onPlatformStarted(platform);
		this.platformStarted = true; // cache if no listener present
		if (listener != null) {
			listener.platformStarted();
		}
	}

	// ----------- Optional, just for displaying status information -----------
	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		if (listener != null) {
			listener.platformStarting();
		}
	}

	public interface PlatformListener
	{
		public void platformStarted();
		public void platformStarting();
	}
}
