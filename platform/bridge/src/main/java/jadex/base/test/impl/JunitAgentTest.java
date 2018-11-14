package jadex.base.test.impl;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
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
//      Logger.getLogger("ComponentTest").log(Level.INFO, "Trying to guess TestAgent name...");
        String className = this.getClass().getName();
        this.comp = extendWithClassIfNeeded(className);
        this.config = STest.getDefaultTestConfig(getClass());
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
        this.config = STest.getDefaultTestConfig(getClass());
    }


    /**
     * Set platform config.
     * @param config
     */
    public void setConfig(IPlatformConfiguration config) 
    {
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
        IExternalAccess platform = Starter.createPlatform(getConfig()).get();
        setPlatform(platform);
        super.runBare();
        platform.killComponent();
//        .addResultListener(new IResultListener<Map<String,Object>>()
//		{
//			@Override
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("exor: "+exception);
//			}
//			
//			@Override
//			public void resultAvailable(Map<String, Object> result)
//			{
//				System.out.println("resa");
//			}
//		});
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
