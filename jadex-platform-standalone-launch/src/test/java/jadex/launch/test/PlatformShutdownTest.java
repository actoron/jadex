package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import junit.framework.TestCase;

/**
 *  Test if the platform terminates itself.
 */
public class PlatformShutdownTest extends TestCase
{
	public void	testPlatformShutdown()
	{
		long timeout	= 10000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-configname", "testcases"}).get(sus, timeout);
		platform.killComponent().get(sus, timeout);
	}
}
