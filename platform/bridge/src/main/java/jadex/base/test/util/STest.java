package jadex.base.test.util;

import java.util.concurrent.atomic.AtomicInteger;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.test.impl.SharedClockService;
import jadex.base.test.impl.SharedExecutionService;
import jadex.base.test.impl.SharedServiceFactory;
import jadex.base.test.impl.SharedSimulationService;
import jadex.base.test.impl.SharedThreadPoolService;
import jadex.commons.Base64;
import jadex.commons.SUtil;

/**
 *  Static config class for tests.
 */
public class STest 
{
    /**
     *  Get local (no communication) test configuration using a generated unique platform name derived from the test name.
     *  Uses simulation for speed.
     *  @param name	The test name used for deriving a platform name.
     */
    public static IPlatformConfiguration getLocalTestConfig(String name)
    {
        // Derive simple name from test name string
    	if(name.indexOf('.')!=-1)
    		name	= name.substring(name.lastIndexOf('.')+1);
    	if(name.indexOf('/')!=-1)
    		name	= name.substring(name.lastIndexOf('/')+1);    		
    	if(name.indexOf('\\')!=-1)
    		name	= name.substring(name.lastIndexOf('\\')+1);    		
    	
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
		config.setPlatformName(name);
		config.setValue("uniquename", true);	// Unique number will be appended to platform name

        // Do not use multi factory as it is much too slow now :(
//		config.setValue("kernel_multi", true);
//		config.setValue("kernel_micro", false);
		config.setValue("kernel_component", true);
		config.setValue("kernel_application", true);
		config.setValue("kernel_bpmn", true);
		config.setValue("kernel_bdix", true);
		config.setValue("kernel_bdi", true);
		
        config.getExtendedPlatformConfiguration().setSimul(true); // Start simulation component
        config.getExtendedPlatformConfiguration().setSimulation(true);	// Set simulation clock and sync execution
        
		//config.setDefaultTimeout(-1);
        
		// Avoid problems due to old platform config files
        config.setValue("settings.readonly", true);
		config.setValue("rescan", true);
        
//        config.setLogging(true);
//        config.getExtendedPlatformConfiguration().setDebugFutures(true);
//		config.setWelcome(true);
		
		return config;
    }
    
    /**
     *  Get a local (no communication) test configuration using a unique platform name derived from the test name.
     *  Attention: The name is unique and the config can not be reused for multiple platforms!
     *  @param test	The test class.
     *  @return The default configuration with a unique platform name.
     */
    public static IPlatformConfiguration getLocalTestConfig(Class<?> test)
    {
    	return getLocalTestConfig(test.getName());
    }

    protected static final AtomicInteger	NETNO	= new AtomicInteger(0);
    
    /**
     *  Create a test configuration to be used for platforms that should be able to communicate via intravm means.
     *  Only platforms created from the same (base) configuration will see each other, i.e., this method should
     *  only be used once for each test case and different configs for a single test should be derived via .clone() from a single base conf.
     *  @param test	The test class for generating platform names.
     */
    public static IPlatformConfiguration createDefaultTestConfig(Class<?> test)
    {
    	IPlatformConfiguration config = getLocalTestConfig(test);
    	
		// Enable intravm awareness, transport and security
    	try
    	{
    		Object awadata	= Class.forName("jadex.platform.service.awareness.IntraVMAwarenessAgent$AwarenessData").getConstructor().newInstance();
    		config.setSuperpeerClient(true);
    		config.setValue("intravmawareness", true);
    		config.setValue("intravmawareness.data", awadata);
            config.setValue("intravm", true);
            config.setValue("security.handshaketimeoutscale", 0.2);
            config.getExtendedPlatformConfiguration().setSecurity(true);
    	}
    	catch(Exception e)
    	{
    		throw SUtil.throwUnchecked(e);
    	}
        
        // Create and set a one time network/pass for this config.
        String	testnetwork_name	= "testnet"+NETNO.incrementAndGet();
        String	testnetwork_pass;
    	byte[] key = new byte[32];
    	SUtil.getSecureRandom().nextBytes(key);
    	testnetwork_pass = "key:" + new String(Base64.encodeNoPadding(key), SUtil.UTF8);
		config.setNetworkNames(new String[] { testnetwork_name });
		config.setNetworkSecrets(new String[] { testnetwork_pass });
		
//		config.setLogging(true);
//		config.setDefaultTimeout(300000);
		
		// Shared services for all platforms to enable distributed simulation
		config.setValue("threadpoolfactory", new SharedServiceFactory<>(SharedThreadPoolService::new));
		config.setValue("exefactory", new SharedServiceFactory<>(SharedExecutionService::new));
		config.setValue("clockfactory", new SharedServiceFactory<>(SharedClockService::new));
		config.setValue("simulation.simfactory", new SharedServiceFactory<>(SharedSimulationService::new));
		config.setValue(IPlatformConfiguration.REALTIMETIMEOUT, false);	// Force simulation time also for network timeouts
		
        return config;
    }
    
    /**
     *  Create a default (remote) test configuration with simulation disabled.
     */
    public static IPlatformConfiguration createRealtimeTestConfig(Class<?> test)
    {
    	return createDefaultTestConfig(test)
    		.getExtendedPlatformConfiguration().setSimul(false)
			.getExtendedPlatformConfiguration().setSimulation(false)
			.setValue(IPlatformConfiguration.REALTIMETIMEOUT, null);	// Un-force simulation time also for network timeouts
    }
    
//    /**
//     *  Start a platform and run the code on a component thread.
//     *  Preferred way for running test methods to avoid simulation clock busy cycling
//     *  while (external) test thread is active. 
//     */
//    public static void	runSimLocked(IPlatformConfiguration conf, Consumer<IInternalAccess> code)
//    {
//    	Future<Void>	run	= new Future<>();
//    	IPlatformConfiguration	runconf	= conf.clone();
//    	Starter.createPlatform(runconf).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(run)
//		{			
//			@Override
//			public void customResultAvailable(IExternalAccess result)
//			{
//				result.scheduleStep(ia ->
//				{
//					code.accept(ia);
//					return IFuture.DONE;
//				}).addResultListener(new DelegationResultListener<Void>(run)
//				{
//					public void customResultAvailable(Void v)
//					{
//						result.killComponent().get();
//						run.setResult(null);
//					}
//				});
//			}
//		});
//    	run.get();
//    }
}
