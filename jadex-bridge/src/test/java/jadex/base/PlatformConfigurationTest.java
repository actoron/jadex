package jadex.base;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlatformConfigurationTest {

    private PlatformConfiguration config;

    @Before
    public void setUp() {
        config = PlatformConfiguration.getDefault();
    }

    @Test
    public void testInitialValue() {
        boolean gui = config.getGui();

        assertTrue(gui);

    }

    @Test
    public void testSetAndGet() {
        config.setRsPublish(true);

        boolean rsPublish = config.getRsPublish();
        assertTrue(rsPublish);
    }


    @Test
    public void testParse() {
        PlatformConfiguration config = PlatformConfiguration.processArgs(new String[]{"-gui", "false", "-rspublish", "true"});
        assertTrue(config.getRsPublish());
        assertFalse(config.getGui());
    }

    @Test
    public void testKernels() {
        RootComponentConfiguration.KERNEL[] kernels = config.getRootConfig().getKernels();
        assertTrue(kernels.length > 0);
    }

}
