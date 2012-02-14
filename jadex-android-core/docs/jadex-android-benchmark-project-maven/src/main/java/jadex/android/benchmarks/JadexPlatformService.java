package jadex.android.benchmarks;

import java.util.UUID;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 *  Android service for running the Jadex platform.
 */
public class JadexPlatformService	extends Service	implements IJadexPlatformService
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
			public IFuture<IExternalAccess>	getPlatform()
			{
				return JadexPlatformService.this.getPlatform();
			}
		};
	}
	
	/**
	 *  Cleanup the service.
	 */
	public void onDestroy()
	{
		if(platform!=null)
		{
			try
			{
				ThreadSuspendable	sus	= new ThreadSuspendable();
				long timeout	= 30000;
				platform.get(sus, timeout).killComponent().get(sus, timeout);
			}
			catch(TimeoutException e)
			{
				System.err.println("Timeout when shutting down platform: "+e);
			}
		}
	}
	
	//-------- IJadexPlatformService interface --------
	
	/**
	 *  Get the jadex platform.
	 */
	public IFuture<IExternalAccess>	getPlatform()
	{
		boolean	create;
		synchronized(this)	// Todo: can be called concurrently in android?
		{
			create	= platform==null;
			if(create)
			{
				platform	= new Future<IExternalAccess>();
			}
		}
		
		if(create)
		{
			// Start the platform
			System.out.println("Starting Jadex Platform...");
			new Thread(new Runnable()
			{
				public void run()
				{
					Starter.createPlatform(new String[]
					{
						"-logging_level", "java.util.logging.Level.INFO",
						"-platformname", "and_" + createRandomPlattformID(),
						"-extensions", "null",
						"-wspublish", "false",
						"-rspublish", "false",
						"-kernels", "\"component, micro\"",
	//					"-tcptransport", "false",
	//					"-niotcptransport", "false",
	//					"-relaytransport", "true",
	//					"-relayaddress", "\""+SRelay.DEFAULT_ADDRESS+"\"",
	//					"-relayaddress", "\""+SRelay.ADDRESS_SCHEME+"134.100.11.200:8080/jadex-platform-relay-web/\"",					
						"-saveonexit", "false",
						"-gui", "false",
						"-autoshutdown", "false",
	//					"-awamechanisms", "new String[]{\"Relay\"}",
	//					"-awareness", "false",
	//					"-usepass", "false"
					}).addResultListener(new DelegationResultListener<IExternalAccess>(platform));
				}
			}).start();
		}
		
		return platform;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Generate a unique platform name.
	 */
	protected String createRandomPlattformID()
	{
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString().substring(0, 5);
	}
}
