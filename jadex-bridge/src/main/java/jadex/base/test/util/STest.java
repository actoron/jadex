package jadex.base.test.util;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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

//    // one time network pass for this vm.
//    private static final String	testnetwork	= SUtil.createUniqueId();
    
    public static IPlatformConfiguration getDefaultTestConfig() 
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
		config.setKernels(IPlatformConfiguration.KERNEL_MULTI);
//		config.setNetworkName("test");
//		config.setNetworkPass(testnetwork);
		
        config.getExtendedPlatformConfiguration().setSecurity(true);
        config.getExtendedPlatformConfiguration().setTcpTransport(true);
        config.getExtendedPlatformConfiguration().setSimul(true); // start simulation component
        config.getExtendedPlatformConfiguration().setSimulation(true);
                
        return config;
    }

    public static IComponentManagementService getCMS(IExternalAccess platform) 
    {
        IFuture<IComponentManagementService> fut = platform.scheduleStep(new IComponentStep<IComponentManagementService>() {
            @Override
            public IFuture<IComponentManagementService> execute(IInternalAccess ia) 
            {
                IComponentManagementService cms = SServiceProvider.getLocalService(ia, IComponentManagementService.class);
                return new Future<IComponentManagementService>(cms);
            }
        });
        IComponentManagementService cms = fut.get();
        return cms;
    }

    public static void terminatePlatform(IExternalAccess platform) 
    {
        platform.killComponent().get();
    }
}
