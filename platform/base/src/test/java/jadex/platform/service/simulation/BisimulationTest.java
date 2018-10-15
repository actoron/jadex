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
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Test starting two platforms using the same simulation clock.
 */
public class BisimulationTest
{
	/**
	 *  Start counter agent on both platforms and check for correct interleaving
	 */
	@Test
	public void test()
	{
		IPlatformConfiguration	config	= STest.getDefaultTestConfig();
		config.setValue("bisimulation", true);
		config.setValue("tcp", false);
		config.setLogging(true);
		
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
				IPlatformConfiguration config2	= config.clone();
				config2.setLogging(true);
				IFuture<IExternalAccess>	fp2	= Starter.createPlatform(config2);
				IExternalAccess	p2	= fp2.get();
				
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
