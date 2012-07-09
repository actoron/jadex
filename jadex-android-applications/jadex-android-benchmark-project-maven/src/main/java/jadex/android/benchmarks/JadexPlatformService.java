package jadex.android.benchmarks;

import jadex.android.JadexAndroidContext;
import jadex.android.JadexAndroidService;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 *  Android service for running the Jadex platform.
 */
public class JadexPlatformService	extends JadexAndroidService	
{
	//-------- attributes --------
	
	/** The platform. */
	protected Future<IExternalAccess>	platform;
	
	//-------- Android methods --------
	
	/**
	 *  Called when an activity binds to the service.
	 */
	public IBinder onBind(Intent intent)
	{
		abstract class MyBinder	extends Binder implements IJadexPlatformService{}
		return new MyBinder()
		{
			public synchronized IFuture<IExternalAccess>	getPlatform()
			{
				Future<IExternalAccess> ret = new Future<IExternalAccess>();
				if (isJadexPlatformRunning()) {
					ret.setResult(getJadexContext().getExternalPlatformAccess());
					return ret;
				} else {
					return startPlatform();
				}
			}
		};
	}
	
	/**
	 *  Cleanup the service.
	 */
	public boolean onUnbind(Intent intent)
	{
		if(platform!=null)
		{
			try
			{
				System.out.println("Starting platform shutdown");
				ThreadSuspendable	sus	= new ThreadSuspendable();
				long start	= System.currentTimeMillis();
				long timeout	= 4500;	// Android issues hard kill (ANR) after 5 secs!
				IExternalAccess	ea = platform.get(sus, timeout);
				ea.killComponent().get(sus, start+timeout-System.currentTimeMillis());
				System.out.println("Platform shutdown completed");
			}
			catch(TimeoutException e)
			{
				System.err.println("Timeout when shutting down platform: "+e);
			}
		}
		return false;
	}
	
	//-------- IJadexPlatformService interface --------
	
	/**
	 *  Get the jadex platform.
	 */
	private IFuture<IExternalAccess>	startPlatform()
	{
		// Start the platform
		System.out.println("Starting Jadex Platform...");

		String options = "-componentfactory jadex.micro.MicroAgentFactory" + " -conf jadex.standalone.PlatformAgent" + " -niotcptransport false"
				+ " -saveonexit false";
		return getJadexContext().startJadexPlatform(new String[]
		{ JadexAndroidContext.KERNEL_MICRO }, null, options);
	}

}
