package jadex.platform.service.registryv2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registry.IRemoteRegistryService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

/**
 *  Test that a search does not timeout even when there is an unresponsive platform.
 */
public class BlockedPlatformSearchTest extends AbstractInfrastructureTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	static
	{
		IPlatformConfiguration	baseconf	= STest.getRealtimeTestConfig(BlockedPlatformSearchTest.class);
		baseconf.setValue("superpeerclient.awaonly", true);
		baseconf.setDefaultTimeout(Starter.getScaledDefaultTimeout(null, WAITFACTOR*2));
//		baseconf.setValue("superpeerclient.debugservices", true);
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
		// Start platforms
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		IExternalAccess	provider	= createPlatform(PROCONF);

		// Block registry of provider
		Future<Void>	block	= new Future<>();
		Future<Void>	blocking	= new Future<>();
		IComponentIdentifier	registry	= ((IService)provider.searchService(
			new ServiceQuery<>(IRemoteRegistryService.class)).get()).getServiceId().getProviderId();
		provider.getExternalAccess(registry).scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Blocking registry "+System.currentTimeMillis());
				ISuspendable	sus	= ISuspendable.SUSPENDABLE.get();
				ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
				blocking.setResult(null);	// notify that blocking is about to take place
				block.get(Timeout.NONE);
				ISuspendable.SUSPENDABLE.set(sus);
				System.out.println("Registry unblocked "+System.currentTimeMillis());
				return IFuture.DONE;
			}
		});
		
		try
		{
			blocking.get();
			System.out.println("Starting searches: "+System.currentTimeMillis());			
			IIntermediateFuture<ITestService>	multi	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
			IFuture<ITestService>	single	= client.searchService(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
			
			// Search multi (i.e. multiplicity 0..)
			Collection<ITestService>	results	= multi.get();
			assertTrue(results.isEmpty());
			System.out.println("Search multi: "+results);
			
			
			// Search single (i.e. multiplicity 1)
			try
			{
				ITestService	result	= single.get();
				System.out.println("Search single: "+result);
				assertFalse("Search should throw ServiceNotFoundException", true);
			}
			catch(Exception e)
			{
				System.out.println("Search single: "+e);
				assertTrue(SUtil.getExceptionStacktrace(e), e instanceof ServiceNotFoundException);
			}
		}
		finally
		{
			// Release provider platform before test shutdown
			block.setResult(null);
		}
	}
}
