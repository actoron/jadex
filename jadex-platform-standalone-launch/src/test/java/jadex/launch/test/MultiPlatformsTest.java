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
		int number	= 1000;
		long timeout	= -1; //10000;
		
		IFuture[]	futures	= new IFuture[number];
		for(int i=0; i<futures.length; i++)
		{
			System.out.println("Starting platform "+i);
			futures[i]	= Starter.createPlatform(new String[]{"-configname", "testcases"});
		}
		
		IExternalAccess[]	platforms	= new IExternalAccess[futures.length];
		ISuspendable	sus	= 	new ThreadSuspendable();
		for(int i=0; i<futures.length; i++)
		{
			System.out.println("Waiting for platform "+i);
			platforms[i]	= (IExternalAccess)futures[i].get(sus, timeout);
		}
		
		for(int i=0; i<futures.length; i++)
		{
			System.out.println("Killing platform "+i);
			platforms[i].killComponent().get(sus, timeout);
		}
	}
}
