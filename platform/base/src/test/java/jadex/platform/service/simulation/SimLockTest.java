package jadex.platform.service.simulation;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Ignore;
import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimer;

/**
 *  Test that platforms behave nicely under simlock and non-simlock conditions.
 */
public class SimLockTest
{
	/**
	 *  Test that a platform performs no polling (i.e. no clock advancements when no user components are running).
	 */
	@Test
	@Ignore
	public void	testNoPolling()
	{
		// Check no polling for simlocked execution
		STest.runSimLocked(STest.createDefaultTestConfig(getClass()), ia ->
		{
			assertArrayEquals("run simlocked", new ITimer[0], ia.getProvidedService(IClockService.class).getTimers());
		});
		
		// Check no polling for default test config
		Starter.createPlatform(STest.createDefaultTestConfig(getClass()))
			.get().scheduleStep(ia ->
		{
			assertArrayEquals("test config", new ITimer[0], ia.getProvidedService(IClockService.class).getTimers());
			
			return ia.killComponent();
		}).get();
	}
}
