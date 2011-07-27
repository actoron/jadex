package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import junit.framework.TestCase;

/**
 *  Test if the platform terminates itself.
 */
public class MultiPlatformsTest extends TestCase
{
	public void	testMultiplePlatforms()
	{
		int number	= 50;
		long timeout	= 10000;
		
		IFuture[]	futures	= new IFuture[number];
		for(int i=0; i<futures.length; i++)
		{
			if(i%10==0)
				System.out.println("Starting platform "+i);
			futures[i]	= Starter.createPlatform(new String[]{"-platformname", "testcases",
				"-gui", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"});
		}
		
		IExternalAccess[]	platforms	= new IExternalAccess[futures.length];
		ISuspendable	sus	= 	new ThreadSuspendable();
		for(int i=0; i<futures.length; i++)
		{
			if(i%10==0)
				System.out.println("Waiting for platform "+i);
			platforms[i]	= (IExternalAccess)futures[i].get(sus, timeout);
		}
		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
		
		for(int i=0; i<futures.length; i++)
		{
			if(i%10==0)
				System.out.println("Killing platform "+i);
			platforms[i].killComponent().get(sus, timeout);
		}
	}
}
