package jadex.platform.service.simulation;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;

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
		
		// Run test on first platform such that clock cannot advance
		IExternalAccess	p1	= Starter.createPlatform(config).get();
		p1.scheduleStep(ia ->
		{
			FutureBarrier<Collection<CMSStatusEvent>>	fubar	= new FutureBarrier<>();
			
			// Start local agent
			fubar.addFuture(ia.createComponentWithResults(CounterAgent.class, null));
			
			// Start other platforms
			for(int i=1; i<3; i++)
			{
				IPlatformConfiguration config2	= config.clone();
				config2.getExtendedPlatformConfiguration().setSimulation(false);
				IFuture<IExternalAccess>	fp2	= Starter.createPlatform(config2);
				
				// Start agent on other platform
				fubar.addFuture(fp2.get().createComponentWithResults(CounterAgent.class,
					new CreationInfo(Collections.singletonMap("offset", i))));
			}
			
			// Wait for all agents.
			return fubar.waitFor();
		}).get();
		
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8]", CounterAgent.LIST.toString());
	}
}
