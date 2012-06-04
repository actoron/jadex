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
	public void	testMultiplePlatforms()
	{
		int number	= 30;	// larger numbers lead to out of mem on hudson 32bit.
		long timeout	= 30000;
		
		List<IFuture<IExternalAccess>>	futures	= new ArrayList<IFuture<IExternalAccess>>();
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
				System.out.println("Starting platform "+i);
			futures.add(Starter.createPlatform(new String[]{"-platformname", "testcases_"+i,
				"-gui", "false", "-printpass", "false",
//				"-logging", "true",
				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"}));
		}
		
		IExternalAccess[]	platforms	= new IExternalAccess[number];
		ISuspendable	sus	= 	new ThreadSuspendable();
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
				System.out.println("Waiting for platform "+i);
//			try
//			{
				platforms[i]	= futures.get(i).get(sus, timeout);
//			}
//			catch(RuntimeException e)
//			{
//				System.out.println("failed: "+i);
//				throw e;
//			}
		}
		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
		
		for(int i=0; i<number; i++)
		{
			if(i%10==0)
				System.out.println("Killing platform "+i);
			platforms[i].killComponent().get(sus, timeout);
		}
	}
}
