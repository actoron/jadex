package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
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
//		"-logging", "true",
//		"-debugfutures", "true",
//		"-nostackcompaction", "true",
		"-platformname", "testcases",
		"-gui", "false",
		"-saveonexit", "false",
		"-welcome", "false",
		"-autoshutdown", "false",
		"-printpass", "false"
	};

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		PlatformsTest test = new PlatformsTest();
		for(int i=0; i<10000; i++)
		{
			System.out.println("Run: "+i);
			test.testPlatforms();
		}
	}
	
	/**
	 *  Test the platforms.
	 */
	public void	testPlatforms()
	{
		long timeout = 1000000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		long[] starttimes = new long[PLATFORMS.length/2+1];
		long[] shutdowntimes = new long[PLATFORMS.length/2+1];
		
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
			
			long start = System.currentTimeMillis();
			final IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(args).get(sus, timeout);
			starttimes[i] = System.currentTimeMillis()-start;
//			System.out.println("Started platform: "+i);
			
			final Future<Void>	fut	= new Future<Void>();
			IComponentManagementService cms = SServiceProvider.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
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
			
//			// Test CTRL-C shutdown behavior.
//			Timer	timer	= new Timer();
//			timer.schedule(new TimerTask()
//			{
//				public void run()
//				{
//					System.exit(0);
//				}
//			}, 3000);
			
			start = System.currentTimeMillis();
			platform.killComponent().get(sus, timeout);
			fut.get(sus, timeout);
			shutdowntimes[i] = System.currentTimeMillis()-start;
//			System.out.println("Stopped platform: "+i);
		}
		
//		System.out.println("Startup times: "+SUtil.arrayToString(starttimes));
//		System.out.println("Sutdown times: "+SUtil.arrayToString(shutdowntimes));
	}
}
