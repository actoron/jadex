package jadex.base.test.impl;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;

/**
 * Junit compatible test class to be extended either by agents that provide test results
 * or by separate classes that provide a name of an agent that provides test results using the constructor.
 */
public abstract class JunitAgentTest extends ComponentTestLazyPlatform 
{

    private IPlatformConfiguration config;

    /**
     * Empty constructor, will try to use this.getClass().getName() as Agent under test.
     */
    public JunitAgentTest() 
    {
//        Logger.getLogger("ComponentTest").log(Level.INFO, "Trying to guess TestAgent name...");
        String className = this.getClass().getName();
        this.comp = extendWithClassIfNeeded(className);
        this.config = STest.getDefaultTestConfig();
    }

    /**
     * Constructor.
     * @param clazz class (agent) to test
     */
    public JunitAgentTest(Class<?> clazz) 
    {
        this(clazz.getName() + ".class");
    }

    /**
     * Constructor.
     * @param component class (agent) to test
     */
    public JunitAgentTest(String component) 
    {
        super(extendWithClassIfNeeded(component), null);
        this.config = STest.getDefaultTestConfig();
    }


    /**
     * Set platform config.
     * @param config
     */
    public void setConfig(IPlatformConfiguration config) 
    {
        if (cms == null) 
            throw new IllegalStateException("Platform already started.");
        this.config = config;
    }

    /**
     * Returns the platform config.
     * Can be overridden to apply special settings. 
     */
    public IPlatformConfiguration getConfig() 
    {
        return config;
    }

    @Override
    public void runBare() 
    {
        IExternalAccess platform = STest.createPlatform(getConfig());
        cms = STest.getCMS(platform);
        setPlatform(platform, cms);
        super.runBare();
        platform.killComponent();
    }

    @Test
    public void testComponent() 
    {
        fail("dummy test");
    }

    /**
     * Extens an agent name with ".class", if needed.
     * @param component
     * @return
     */
    private static String extendWithClassIfNeeded(String component) 
    {
        return component.endsWith(".class") ? component : component + ".class";
    }
}
