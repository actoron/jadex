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
public class MicroCreationTest extends TestCase
{
	public void	testMicroCreation()
	{
		long timeout	= 300000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-configname", "creation_test_micro"}).get(sus, timeout);
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
		
		fut.get(sus, timeout);
	}
}
