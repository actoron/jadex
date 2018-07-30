package jadex.base.test.util;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Base64;
import jadex.commons.SUtil;

/**
 * 
 */
public class STest {

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
    
    public static IPlatformConfiguration getDefaultTestConfig() 
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
        // Do not use multi factory as it is much too slow now :(
//		config.setValue("kernel_multi", true);
//		config.setValue("kernel_micro", false);
		config.setValue("kernel_component", true);
		config.setValue("kernel_application", true);
		config.setValue("kernel_bpmn", true);
		config.setValue("kernel_bdix", true);
		config.setValue("kernel_bdi", true);
		config.setNetworkNames(new String[] { testnetwork_name });
		config.setNetworkSecrets(new String[] { testnetwork_pass });
		
		// Enable new awareness
		config.setSuperpeerClient(true);
		config.setValue("superpeerclient.awaonly", true);
//		config.addComponent("jadex.platform.service.pawareness.PassiveAwarenessMulticastAgent.class");
		config.addComponent("jadex.platform.service.pawareness.PassiveAwarenessIntraVMAgent.class");
		config.setAwareness(false);	// disable old awareness
		
        config.getExtendedPlatformConfiguration().setSecurity(true);
        config.getExtendedPlatformConfiguration().setTcpTransport(true);
        config.getExtendedPlatformConfiguration().setSimul(true); // start simulation component
        config.getExtendedPlatformConfiguration().setSimulation(true);
        
        config.setValue("settings.readonly", true);
        
//        config.setLogging(true);
                
        return config;
    }

    public static IComponentManagementService getCMS(IExternalAccess platform) 
    {
        return platform.searchService( new ServiceQuery<>(IComponentManagementService.class)).get();
    }

    public static void terminatePlatform(IExternalAccess platform) 
    {
        platform.killComponent().get();
    }
}
