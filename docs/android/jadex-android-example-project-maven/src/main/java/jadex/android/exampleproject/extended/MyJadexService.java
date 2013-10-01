package jadex.android.exampleproject.extended;

import jadex.android.commons.JadexPlatformOptions;
import jadex.android.service.JadexPlatformService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyJadexService extends JadexPlatformService
{
	// BEGIN: nested classes
	public interface MyPlatformListener
	{
		void onPlatformStarting();
		void onPlatformStarted();
		void onHelloWorldAgentStarted(String name);
	}

	public class MyServiceInterface extends Binder
	{

		public boolean isJadexPlatformRunning()
		{
			return MyJadexService.this.isPlatformRunning();
		}

		public void setPlatformListener(MyPlatformListener l)
		{
			listener = l;
		}

		public String getPlatformId()
		{
			return MyJadexService.this.getPlatformId().toString();
		}

		public void startPlatform()
		{
			MyJadexService.this.startPlatform();
		}

		public void startHelloWorldAgent()
		{
			num++;
			MyJadexService.this.startHelloWorldAgent();
		}

		public void stopPlatforms()
		{
			MyJadexService.this.stopPlatforms();
		}

	}

	// END: nested classes

	/** agent counter */
	private int num;

	/** listener for the activity **/
	public MyPlatformListener listener;

	public MyJadexService()
	{
		setPlatformAutostart(false);
		setPlatformKernels(JadexPlatformOptions.KERNEL_MICRO);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new MyServiceInterface();
	}

	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		if (listener != null)
		{
			listener.onPlatformStarting();
		}
	}

	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		super.onPlatformStarted(platform);
		if (listener != null)
		{
			listener.onPlatformStarted();
		}
	}

	public void startHelloWorldAgent()
	{
		startComponent(getPlatformId(), "HelloWorldAgent " + num, AndroidAgent.class).addResultListener(new DefaultResultListener<IComponentIdentifier>()
		{

			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				if (listener != null) {
					listener.onHelloWorldAgentStarted(result.toString());
				}
			}
		});
	}
}
