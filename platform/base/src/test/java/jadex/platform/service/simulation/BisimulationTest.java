package jadex.platform.service.simulation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.simulation.ISimulationService;
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
		IExternalAccess	p1	= Starter.createPlatform(config).get();
		
		// Run test on first platform such that clock cannot advance
		p1.scheduleStep(ia ->
		{
			// Start local agent
			IFuture<?>	c1	= ia.createComponentWithResults(CounterAgent.class, null);
			
			// Start second platform
			IPlatformConfiguration config2	= config.clone();
//			config2.getExtendedPlatformConfiguration().setSimulation(false);
//			config2.setValue("bisimulation", "true");
			IFuture<IExternalAccess>	fp2	= Starter.createPlatform(config2);
			
			// Stop local clock until second platform is started
			ISimulationService	simserv	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class));
			simserv.addAdvanceBlocker(fp2).get();
			
			// Start agent on second platform
			IFuture<?>	c2	= fp2.get().createComponentWithResults(CounterAgent.class, null);
			
			// Wait for both agents.
			c1.get();
			c2.get();
			
			return IFuture.DONE;
		}).get();
		
		assertEquals("[1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10]", CounterAgent.LIST.toString());
	}
}
