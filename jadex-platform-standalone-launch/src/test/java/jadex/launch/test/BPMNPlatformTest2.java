package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.util.Map;

import junit.framework.TestCase;

/**
 *  Test if the BPMN platform can be started.
 */
//Todo: Doesn't work on hudson server
//(race condition in init leads to micro factory not being found?)
public class BPMNPlatformTest2 extends TestCase
{
	public void	testBPMNPlatform()
	{
		long timeout	= 10000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "testcases",
			"-componentfactory", "jadex.bpmn.BpmnFactory",
			"-conf", "jadex.standalone.Platform.bpmn",
			"-niotransport", "false", "-gui", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false"}).get(sus, timeout);
		final Future<Void>	fut	= new Future<Void>();
		SServiceProvider.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(fut)
		{
			public void customResultAvailable(IComponentManagementService	cms)
			{
				cms.addComponentListener(platform.getComponentIdentifier(), new ICMSComponentListener()
				{
					public IFuture<Void> componentRemoved(IComponentDescription desc, Map<String, Object> results)
					{
						fut.setResult(null);
						return IFuture.DONE;
					}
					public IFuture<Void> componentAdded(IComponentDescription desc)
					{
						return IFuture.DONE;
					}
					public IFuture<Void> componentChanged(IComponentDescription desc)
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
