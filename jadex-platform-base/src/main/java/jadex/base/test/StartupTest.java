package jadex.base.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

/**
 *  Try to reproduce startup test.
 */
public class StartupTest
{
	public static void main(String[] args)
	{
		for(int i=1; ;i++)
		{
			System.out.println("Starting platform "+i);
			IFuture	fut	= Starter.createPlatform(new String[]{"-configname", "\"all_kernels (rms)\""});
			IExternalAccess	platform	= (IExternalAccess)fut.get(new ThreadSuspendable());
			platform.killComponent().get(new ThreadSuspendable());
		}
	}
}
