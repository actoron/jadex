package jadex.launch.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;

/**
 *  Test if the platform terminates itself.
 */
public class MultiPlatformsTest //extends TestCase 
{
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		for(int p=0; p<500; p++)
		{
			long	time	= System.currentTimeMillis();
			
			MultiPlatformsTest t = new MultiPlatformsTest();
			t.testMultiplePlatforms();
			
			time	= System.currentTimeMillis() - time;
			System.out.println("run "+p+" took "+time+" milliseconds.");
		}
		
//		Thread.sleep(3000000);
	}
	
	/**
	 *  Perform the test.
	 * @throws Exception 
	 */
	@Test
	public void	testMultiplePlatforms() throws Exception
	{
		
		Timer	memtimer	= new Timer(true);
		memtimer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				System.out.println("Memory: free="+SUtil.bytesToString(Runtime.getRuntime().freeMemory())
					+", max="+SUtil.bytesToString(Runtime.getRuntime().maxMemory())
					+", total="+SUtil.bytesToString(Runtime.getRuntime().totalMemory()));
			}
		}, 0, 30000);

//		Thread.sleep(3000000);

		
		int	number	= 15; // larger numbers cause timeout on www1.
		
		List<IFuture<IExternalAccess>>	futures	= new ArrayList<IFuture<IExternalAccess>>();
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
			{
				System.out.println("Starting platform "+i);
			}
			futures.add(Starter.createPlatform(new String[]{"-platformname", "testcases_"+i+"*",
				"-gui", "false", "-printpass", "false", "-cli", "false",
//				"-logging", "true",
//				"-awareness", "false",
//				"-componentfactory", "jadex.micro.MicroAgentFactory",
//				"-conf", "jadex.standalone.PlatformAgent",
//				"-awamechanisms", "\"Relay\"", 
//				"-awamechanisms", "\"Broadcast\"", // broadcast 3 times as slow!?
//				"-awamechanisms", "\"Multicast\"", 
				"-awamechanisms", "\"Relay, Multicast, Message\"", 
//				"-deftimeout", "60000",
				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"}));
		}
		
		IExternalAccess[]	platforms	= new IExternalAccess[number];
		long	timeout	= Starter.getScaledLocalDefaultTimeout(null, number);
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
			{
				System.out.println("Waiting for platform "+i);
			}
			try
			{
				platforms[i]	= futures.get(i).get(timeout);
			}
			catch(RuntimeException e)
			{
				System.out.println("failed: "+i+", "+e);
				throw e;
			}
		}
		
//		Thread.sleep(10000);
		
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
			{
				System.out.println("Killing platform "+i);
			}
			platforms[i].killComponent().get(timeout);
		}
		
		if(memtimer!=null)
		{
			memtimer.cancel();
			memtimer	= null;
		}
		
//		Thread.sleep(3000000);
	}
}
