package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 *  Test if the platform terminates itself.
 */
public class MultiPlatformsTest extends TestCase 
{
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		MultiPlatformsTest t = new MultiPlatformsTest();
		t.testMultiplePlatforms();
	}
	
	/**
	 *  Perform the test.
	 * @throws Exception 
	 */
	public void	testMultiplePlatforms() throws Exception
	{
//		for(int p=0; p<100; p++)
		{
//			long	time	= System.currentTimeMillis();
		int	number	= 25;	// larger numbers cause timeout on toaster.
		long	timeout	= 180000;	// time required by toaster.
		
		List<IFuture<IExternalAccess>>	futures	= new ArrayList<IFuture<IExternalAccess>>();
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
			{
				System.out.println("Starting platform "+i);
			}
			futures.add(Starter.createPlatform(new String[]{"-platformname", "testcases_"+i+"*",
				"-gui", "false", "-printpass", "false", "-cli", "false",
				"-deftimeout", ""+timeout,
//				"-logging", "true",
//				"-awareness", "false",
//				"-componentfactory", "jadex.micro.MicroAgentFactory",
//				"-conf", "jadex.standalone.PlatformAgent",
//				"-awamechanisms", "\"Relay\"", 
//				"-awamechanisms", "\"Broadcast\"", // broadcast 3 times as slow!?
//				"-awamechanisms", "\"Multicast\"", 
				"-awamechanisms", "\"Relay, Multicast, Message\"", 
				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"}));
		}
		
		IExternalAccess[]	platforms	= new IExternalAccess[number];
		ISuspendable	sus	= 	new ThreadSuspendable();
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
			{
				System.out.println("Waiting for platform "+i);
			}
			try
			{
				platforms[i]	= futures.get(i).get(sus, timeout);
			}
			catch(RuntimeException e)
			{
				System.out.println("failed: "+i+e);
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
			platforms[i].killComponent().get(sus, timeout);
		}
		
		
//			time	= System.currentTimeMillis() - time;
//			System.out.println("run "+p+" took "+time+" milliseconds.");
		}
		
//		Thread.sleep(300000);
	}
}
