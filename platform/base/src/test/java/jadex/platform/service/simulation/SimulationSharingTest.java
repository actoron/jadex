package jadex.platform.service.simulation;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;

/**
 *  Test starting several platforms using the same simulation clock.
 */
public class SimulationSharingTest
{
	/**
	 *  Start counter agent on all platforms and check for correct interleaving
	 */
	@Test
	public void test()
	{
		// Test only makes sense when running in sim mode.
		if(STest.REALTIME)
			return;
		
		IPlatformConfiguration	config	= STest.createDefaultTestConfig(getClass());
		
		// Run test on first platform such that clock cannot advance
		IExternalAccess	p1	= Starter.createPlatform(config).get();
		p1.scheduleStep(ia ->
		{
			FutureBarrier<IExternalAccess> startfubar = new FutureBarrier<>();
			
			// Start local agent
			startfubar.addFuture(ia.createComponent(new CreationInfo().setFilename(CounterAgent.class.getName()+".class")));
			
			// Start other platforms
			for(int i=1; i<3; i++)
			{
				IPlatformConfiguration	config2	= config.clone();
				IFuture<IExternalAccess>	fp2	= Starter.createPlatform(config2);
				System.err.println("Waiting for platform: "+(i+1));
				IExternalAccess	p2	= fp2.get();
				System.err.println("Finished waiting for platform: "+(i+1));
				
				// Start agent on other platform
				startfubar.addFuture(p2.createComponent(
					new CreationInfo(Collections.singletonMap("offset", i)).setFilename(CounterAgent.class.getName()+".class")));
			}
			
			Collection<IExternalAccess> extas = startfubar.waitForResults().get();
			FutureBarrier<Map<String, Object>> fubar = new FutureBarrier<>();
			for (IExternalAccess exta : extas)
				fubar.addFuture(exta.waitForTermination());
			
			// Wait for all agents.
			return fubar.waitFor();
		}).get();
		
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8]", CounterAgent.LIST.toString());
	}
}
