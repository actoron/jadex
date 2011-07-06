package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.util.Map;

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
		final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "testcases",
			"-gui", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"}).get(sus, timeout);
		final Future	fut	= new Future();
		SServiceProvider.getService(platform.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.addComponentListener(platform.getComponentIdentifier(), new ICMSComponentListener()
				{
					public IFuture componentRemoved(IComponentDescription desc, Map results)
					{
						fut.setResult(null);
						return IFuture.DONE;
					}
					public IFuture componentAdded(IComponentDescription desc)
					{
						return IFuture.DONE;
					}
					public IFuture componentChanged(IComponentDescription desc)
					{
						return IFuture.DONE;
					}
				});
			}
		});
		
//		// Test CTRL-C shutdown behavior.
//		Timer	timer	= new Timer();
//		timer.schedule(new TimerTask()
//		{
//			public void run()
//			{
//				System.exit(0);
//			}
//		}, 3000);
		
		platform.killComponent().get(sus, timeout);

		fut.get(sus, timeout);
	}
}
