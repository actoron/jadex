package jadex.android.benchmarks;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.util.UUID;

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
						"-componentfactory", "jadex.micro.MicroAgentFactory",
						"-conf", "jadex.standalone.PlatformAgent",
						"-logging", "true",
						"-platformname", "and_" + createRandomPlattformID(),
						"-extensions", "null",
						"-wspublish", "false",
						"-rspublish", "false",
						"-kernels", "\"micro\"",
//						"-kernels", "\"component, micro\"",
	//					"-tcptransport", "false",
						"-niotcptransport", "false",
	//					"-relaytransport", "true",
//						"-relayaddress", "\""+SRelay.ADDRESS_SCHEME+"grisougarfield.dyndns.org:52339/relay/\"",
	//					"-relayaddress", "\""+SRelay.DEFAULT_ADDRESS+"\"",
	//					"-relayaddress", "\""+SRelay.ADDRESS_SCHEME+"134.100.11.200:8080/jadex-platform-relay-web/\"",					
						"-saveonexit", "false",
						"-gui", "false",
						"-autoshutdown", "false",
						"-binarymessages", "true",
						"-android", "true"
	//					"-awamechanisms", "new String[]{\"Relay\"}",
	//					"-awareness", "false",
	//					"-usepass", "false",
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
