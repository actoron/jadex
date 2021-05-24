package jadex.platform.service.simulation;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import jadex.base.test.util.STest;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimer;

/**
 *  Test that a platform performs no polling (i.e. no clock advancements when no user components are running).
 */
public class NoPollingTest
{
	/**
	 *  Check no polling for local test config.
	 */
	@Test
	public void	testNoLocalPolling()
	{
		STest.runSimLocked(STest.getLocalTestConfig(getClass()), ia ->
		{
			assertArrayEquals("test config", new ITimer[0], ia.getProvidedService(IClockService.class).getTimers());
		});
	}

	/**
	 *  Check no polling for default (remote) config without super peer client.
	 */
	@Test
	public void	testNoPollingWhenNoSPC()
	{
		STest.runSimLocked(STest.createDefaultTestConfig(getClass())
			.setSuperpeerClient(false), ia ->
		{
			assertArrayEquals("run simlocked", new ITimer[0], ia.getProvidedService(IClockService.class).getTimers());
		});
	}
}
