package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.util.Map;

import junit.framework.TestCase;

/**
 *  Test if the platforms can be started and terminate correctly.
 */
public class PlatformsTest extends TestCase
{
	// The platforms to test as pairs of componentfactory and model.
	String[]	PLATFORMS	= new String[]
	{
		"jadex.component.ComponentComponentFactory", "jadex.standalone.Platform.component.xml",
		"jadex.micro.MicroAgentFactory", "jadex.standalone.PlatformAgent",
		"jadex.bpmn.BpmnFactory", "jadex.standalone.Platform.bpmn"
	};
	// Base arguments used for every platform.
	String[]	BASEARGS	= new String[]
    {
		"-platformname", "testcases",
		"-gui", "false",
		"-saveonexit", "false",
		"-welcome", "false",
		"-autoshutdown", "false"
	};

	public void	testBPMNPlatform()
	{
		long timeout	= 10000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		
		for(int i=0; i<=PLATFORMS.length/2; i++)
		{
			String[]	args	= BASEARGS;
			if(i>0)	// First run with standard platform
			{
				args	= (String[])SUtil.joinArrays(args, new String[]
				{
					"-componentfactory", PLATFORMS[(i-1)*2],
					"-conf", PLATFORMS[(i-1)*2+1]					
				});
			}
			
			final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(args).get(sus, timeout);
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
			
//			// Test CTRL-C shutdown behavior.
//			Timer	timer	= new Timer();
//			timer.schedule(new TimerTask()
//			{
//				public void run()
//				{
//					System.exit(0);
//				}
//			}, 3000);
			
			platform.killComponent().get(sus, timeout);

			fut.get(sus, timeout);
		}
	}
}
