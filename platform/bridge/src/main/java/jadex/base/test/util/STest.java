package jadex.base.test.util;

import java.util.concurrent.atomic.AtomicInteger;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.commons.Base64;
import jadex.commons.SUtil;

/**
 * 
 */
public class STest 
{

    public static IExternalAccess createPlatform() 
    {
        return createPlatform(getDefaultTestConfig());
    }

    public static IExternalAccess createPlatform(IPlatformConfiguration config) 
    {
        IExternalAccess access = Starter.createPlatform(config).get();
        return access;
    }

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
     *  Get the test configuration using a unique platform name derived from the caller class.
     */
    public static IPlatformConfiguration getLocalTestConfig()
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
        
        // Set platform name based on caller class / code line
        boolean	found	= false;
    	for(StackTraceElement stack: Thread.currentThread().getStackTrace())
    	{
    		// If STest -> skip and set found to true
    		if(stack.getClassName().equals(STest.class.getName()))
    		{
    			found	= true;
    		}
    		
    		// If found previously but not in current stack element(!) -> use stack element as name (i.e. class that called some STest method)
    		else if(found)
    		{
    			config.setPlatformName(stack.getClassName()+":"+stack.getLineNumber()+"-"+platno.getAndIncrement());
    			break;
    		}
    	}

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
     *  Get the test configuration using a unique platform name derived from the test objects class.
     *  @param test	The test instance.
     *  @return	The configuration.
     */
    public static IPlatformConfiguration getDefaultTestConfig()
    {
    	IPlatformConfiguration config = getLocalTestConfig();
    	
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
