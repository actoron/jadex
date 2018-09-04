package jadex.platform.service.simulation;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.simulation.ISimulationService;
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
		IExternalAccess	p1	= Starter.createPlatform(config).get();
		
		// Run test on first platform such that clock cannot advance
		p1.scheduleStep(ia ->
		{
			FutureBarrier<Collection<CMSStatusEvent>>	fubar	= new FutureBarrier<>();
			
			// Start two local agents
			fubar.addFuture(ia.createComponentWithResults(CounterAgent.class, null));
			fubar.addFuture(ia.createComponentWithResults(CounterAgent.class, null));
			
			// Start second platform
			IPlatformConfiguration config2	= config.clone();
//			config2.getExtendedPlatformConfiguration().setSimulation(false);
			IFuture<IExternalAccess>	fp2	= Starter.createPlatform(config2);
			
			// Stop local clock until second platform is started
			ISimulationService	simserv	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class));
			simserv.addAdvanceBlocker(fp2).get();
			
			// Start two agents on second platform
			fubar.addFuture(fp2.get().createComponentWithResults(CounterAgent.class,
				new CreationInfo(Collections.singletonMap("offset", 2))));
			fubar.addFuture(fp2.get().createComponentWithResults(CounterAgent.class,
				new CreationInfo(Collections.singletonMap("offset", 2))));
			
			// Wait for all agents.
			fubar.waitFor().get();
			
			return IFuture.DONE;
		}).get();
		
		assertEquals("[1, 1, 2, 2, 2, 2, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8, 8, 8, 9, 9, 10, 10, 10, 10]", CounterAgent.LIST.toString());
	}
}
