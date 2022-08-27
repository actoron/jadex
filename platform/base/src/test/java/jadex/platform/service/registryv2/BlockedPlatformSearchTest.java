package jadex.platform.service.registryv2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.registry.IRemoteRegistryService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Test that a search does not timeout even when there is an unresponsive platform.
 */
@Ignore	// TODO: can no longer use use comp.suspend() due to new semantic steps?
public class BlockedPlatformSearchTest extends AbstractInfrastructureTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	static
	{
		IPlatformConfiguration	baseconf	= STest.createDefaultTestConfig(BlockedPlatformSearchTest.class);
		baseconf.setValue("superpeerclient.awaonly", true);
		baseconf.setDefaultTimeout(Starter.getScaledDefaultTimeout(null, 0.5));
//		baseconf.setValue("superpeerclient.debugservices", "ITestService");
//		baseconf.getExtendedPlatformConfiguration().setDebugFutures(true);

		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(GlobalProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider");
	}
	
	//-------- test methods --------
	
	/**
	 *  Test searching with blocked provider.
	 */
	@Test
	public void testBlockedSearch()
	{
		STest.runSimLocked(CLIENTCONF, ia->
		{
			// Start platforms
			IExternalAccess	client	= ia.getExternalAccess();
			IExternalAccess	provider	= createPlatform(PROCONF);
			
			// Check that provider service can be found.
			assertEquals(1, client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get().size());
			assertNotNull(client.searchService(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get());

			// Block registry of provider
			IComponentIdentifier	registry	= ((IService)provider.searchService(
				new ServiceQuery<>(IRemoteRegistryService.class)).get()).getServiceId().getProviderId();
			provider.getExternalAccess(registry).suspendComponent().get();
			
			System.out.println("Starting searches: "+client.searchService(new ServiceQuery<>(IClockService.class)).get().getTime());
			IIntermediateFuture<ITestService>	multi	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
			IFuture<ITestService>	single	= client.searchService(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
			
			// Search multi (i.e. multiplicity 0..)
			Collection<ITestService>	results	= multi.get();
			assertTrue(""+results, results.isEmpty());
			
			
			// Search single (i.e. multiplicity 1)
			try
			{
				ITestService	result	= single.get();
				assertFalse("Expected ServiceNotFoundException but was: "+result, true);
			}
			catch(Exception e)
			{
				assertTrue(SUtil.getExceptionStacktrace(e), e instanceof ServiceNotFoundException);
			}
			
			System.out.println("Finished searches: "+client.searchService(new ServiceQuery<>(IClockService.class)).get().getTime());			
		});
	}
}
