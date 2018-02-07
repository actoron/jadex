package jadex.base;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class PlatformConfigurationTest
{
	private IPlatformConfiguration config;

	@Before
	public void setUp()
	{
		config = PlatformConfigurationHandler.getDefault();
	}

	@Test
	public void testInitialValue()
	{
		boolean gui = config.getGui();

		assertTrue(gui);

	}

	@Test
	public void testSetAndGet()
	{
		config.getExtendedPlatformConfiguration().setRsPublish(true);

		boolean rsPublish = config.getExtendedPlatformConfiguration().getRsPublish();
		assertTrue(rsPublish);
	}


//	@Test
//	public void testParse()
//	{
//		IPlatformConfiguration config = Starter.processArgs(new String[]{"-gui", "false", "-rspublish", "true"});
//		assertTrue(config.getRsPublish());
//		assertFalse(config.getGui());
//	}

	@Test
	public void testKernels()
	{
//		IRootComponentConfiguration.KERNEL[] kernels = config.getRootConfig().getKernels();
		String[] kernels = config.getKernels();
		assertTrue(kernels.length > 0);
	}

}
