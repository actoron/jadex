package jadex.base.test.util;

import java.util.concurrent.atomic.AtomicInteger;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.bridge.IExternalAccess;
import jadex.commons.Base64;
import jadex.commons.SUtil;

/**
 * 
 */
public class STest 
{
    // one time network pass for this vm.
    public static final String	testnetwork_name	= "test";
    //public static final String	testnetwork_pass	= SUtil.createUniqueId();
    public static final String	testnetwork_pass;
    static
    {
    	byte[] key = new byte[32];
    	SUtil.getSecureRandom().nextBytes(key);
    	testnetwork_pass = "key:" + new String(Base64.encodeNoPadding(key), SUtil.UTF8);
    }
    
    /** Counter for unique platform numbers. */
	static AtomicInteger	platno	= new AtomicInteger(0);

    /**
     *  Get the test configuration using a unique platform name derived from the test class.
     *  @param test	The test class.
     *  @return The default configuration with a unique platform name.
     */
    public static IPlatformConfiguration getLocalTestConfig(Class<?> test)
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
		config.setPlatformName(test.getName()+"-"+platno.getAndIncrement());

        // Do not use multi factory as it is much too slow now :(
//		config.setValue("kernel_multi", true);
//		config.setValue("kernel_micro", false);
		config.setValue("kernel_component", true);
		config.setValue("kernel_application", true);
		config.setValue("kernel_bpmn", true);
		config.setValue("kernel_bdix", true);
		config.setValue("kernel_bdi", true);
		
        config.getExtendedPlatformConfiguration().setSimul(true); // start simulation component
        config.getExtendedPlatformConfiguration().setSimulation(true);
//        config.setValue("bisimulation", true);
        
        config.setValue("settings.readonly", true);
        
//        config.setLogging(true);
//        config.getExtendedPlatformConfiguration().setDebugFutures(true);
//		config.setWelcome(true);
		
		return config;
    }
    
    /**
     *  Get the test configuration using a unique platform name derived from the test class.
     *  @param test	The test class.
     *  @return The default configuration with a unique platform name.
     */
    public static IPlatformConfiguration getDefaultTestConfig(Class<?> test)
    {
    	IPlatformConfiguration config = getLocalTestConfig(test);
    	
		// Enable intravm awareness, transport and security
		config.setSuperpeerClient(true);
		config.setValue("passiveawarenessintravm", true);
        config.setValue("intravm", true);
        config.getExtendedPlatformConfiguration().setSecurity(true);
		config.setNetworkNames(new String[] { testnetwork_name });
		config.setNetworkSecrets(new String[] { testnetwork_pass });
		
        return config;
    }
    
    public static void terminatePlatform(IExternalAccess platform) 
    {
        platform.killComponent().get();
    }
}
