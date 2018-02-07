package jadex.launch.test.servicecall;

import static org.junit.Assert.assertNotEquals;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Test if non functional properties are being reset after service call.
 *  
 *  Test cases: R = required service proxy, P = provided service proxy, S = service impl, RS = remote proxy of service
 *  
 *  R -> P -> S			normal case from component that has used a required service (service is local)
 *  R -> S				+ service is raw
 *  R -> RS -> P -> S	normal case from component that has used a required service (service is remote)
 *  R -> RS -> S		+ service is raw
 *  P -> S				non-component thread has searched for a service (service is local)
 *  S					+ service is raw
 *  RS -> P	-> S		non-component thread has searched for a service (service is remote)
 *  RS -> S				+ service is raw
 *
 *  (R) -> (RS) -> (P) -> S	maximal chain, all () parts are optional	
 */
public class ServiceCallTestNFClearTest
{
	private static final int	USER_TIMEOUT	= 2015;

	@Rule
	public TestName				name			= new TestName();

	private IExternalAccess		platform1;

	private IExternalAccess		platform2;

	private long				timeout;

	@Before
	public void setUp()
	{
		timeout = Starter.getLocalDefaultTimeout(null);

		String pid = SUtil.createPlainRandomId(name.getMethodName(), 3) + "-*";

		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimal();
//		config.setLogging(true);
//		config.setDefaultTimeout(-1);
		config.setPlatformName(pid);
		config.getExtendedPlatformConfiguration().setSaveOnExit(false);
		config.getExtendedPlatformConfiguration().setAutoShutdown(false);
		config.getExtendedPlatformConfiguration().setSecurity(true);
//		config.setAwaMechanisms(AWAMECHANISM.local);
//		config.setAwareness(true);
		config.setAwareness(false);
		config.getExtendedPlatformConfiguration().setTcpTransport(true);

		platform1 = Starter.createPlatform(config).get(timeout);

		platform2 = Starter.createPlatform(config).get(timeout);

		createProxies(platform1, platform2);

		CallAccess.resetNextInvocation();
	}
	
	@After
	public void tearDown()
	{
		platform1.killComponent().get();
		platform2.killComponent().get();
	}

	/**
	 * main thread -> raw provided service
	 */
//	@Test
	// Can never work as only user code is executed
	public void testMain_toProvidedRaw()
	{
		IExternalAccess exta = createServiceAgent(platform1, RawServiceAgent.class);
		IServiceCallService service = SServiceProvider.getService(exta, IServiceCallService.class).get(timeout);
		assertServiceCallResetsServiceInvocation(service);
	}

	/**
	 * main thread -> direct provided service
	 */
	@Test
	public void testMain_toProvidedDirect()
	{
		IExternalAccess exta = createServiceAgent(platform1, DirectServiceAgent.class);
		IServiceCallService service = SServiceProvider.getService(exta, IServiceCallService.class).get(timeout);
		assertServiceCallResetsServiceInvocation(service);
	}

	/**
	 * main thread -> decoupled provided service
	 */
	@Test
	public void testMain_toProvidedDecoupled()
	{
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IServiceCallService service = SServiceProvider.getService(exta, IServiceCallService.class).get(timeout);
		assertServiceCallResetsServiceInvocation(service);
	}

	// ----------------- Single Platform tests -------------------


	/**
	 * agent -> raw provided service
	 */
//	@Test
	// Can never work as only user code is executed
	public void testAgent_toProvidedRaw()
	{
		testProvided(platform1, RawServiceAgent.class);
	}

	/**
	 * agent -> direct provided service
	 */
	@Test
	public void testAgent_toProvidedDirect()
	{
		testProvided(platform1, DirectServiceAgent.class);
	}

	/**
	 * agent -> decoupled provided service
	 */
	@Test
	public void testAgent_toProvidedDecoupled()
	{
		testProvided(platform1, DecoupledServiceAgent.class);
	}

	/**
	 * agent -> raw required service -> provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredRaw_toProvidedDecoupled_local()
	{
		testRequiredToProvided(platform1, platform1, DecoupledServiceAgent.class, ServiceCallAgent.class, "raw", false);
	}

	/**
	 * agent -> direct required service -> provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredDirect_toProvidedDecoupled_local()
	{
		testRequiredToProvided(platform1, platform1, DecoupledServiceAgent.class, ServiceCallAgent.class, "direct", false);
	}

	/**
	 * agent -> decoupled required service -> provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredDecoupled_toProvidedDecoupled_local()
	{
		testRequiredToProvided(platform1, platform1, DecoupledServiceAgent.class, ServiceCallAgent.class, "decoupled", false);
	}

	// ----------------- Remote Platform tests -------------------

	/**
	 * agent -> raw required service -> Remote -> raw provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredRaw_toProvidedRaw_remote()
	{
		testRequiredToProvided(platform1, platform2, RawServiceAgent.class, ServiceCallAgent.class, "raw", false);
	}

	/**
	 * agent -> direct required service -> Remote -> raw provided service ->
	 * impl
	 */
	@Test
	public void testAgent_toRequiredDirect_toProvidedRaw_remote()
	{
		testRequiredToProvided(platform1, platform2, RawServiceAgent.class, ServiceCallAgent.class, "direct", false);
	}

	/**
	 * agent -> decoupled required service -> Remote -> raw provided service ->
	 * impl
	 */
	@Test
	public void testAgent_toRequiredDecoupled_toProvidedRaw_remote()
	{
		testRequiredToProvided(platform1, platform2, RawServiceAgent.class, ServiceCallAgent.class, "decoupled", false);
	}

	/**
	 * agent -> raw required service -> Remote -> provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredRaw_toProvidedDecoupled_remote()
	{
		testRequiredToProvided(platform1, platform2, DecoupledServiceAgent.class, ServiceCallAgent.class, "raw", false);
	}

	/**
	 * agent -> direct required service -> remote -> provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredDirect_toProvidedDecoupled_remote()
	{
		testRequiredToProvided(platform1, platform2, DecoupledServiceAgent.class, ServiceCallAgent.class, "direct", false);
	}

	/**
	 * agent -> decoupled required service -> remote -> provided service -> impl
	 */
	@Test
	public void testAgent_toRequiredDecoupled_toProvidedDecoupled_remote()
	{
		testRequiredToProvided(platform1, platform2, DecoupledServiceAgent.class, ServiceCallAgent.class, "decoupled", false);
	}

	private void testProvided(IExternalAccess p1, Class< ? > provider)
	{
		testRequiredToProvided(p1, null, provider, null, null, true);
	}

	private void testRequiredToProvided(IExternalAccess p1, IExternalAccess p2, Class< ? > provider, Class< ? > consumer, final String requiredOrProvidedServiceName, final boolean useProvided)
	{
		IExternalAccess exta = createServiceAgent(p1, provider);
		if(consumer != null)
		{
			exta = createServiceAgent(p2, consumer);
		}

		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				IServiceCallService service;
				if(useProvided)
				{
					// if (requiredOrProvidedServiceName != null) {
					// service = (IServiceCallService)
					// ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(requiredOrProvidedServiceName);
					// } else {
					service = ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IServiceCallService.class);
					// }
				}
				else
				{
//					service = (IServiceCallService)ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService(requiredOrProvidedServiceName).get();
					service = SServiceProvider.waitForService(ia, new IResultCommand<IFuture<IServiceCallService>, Void>()
					{
						public jadex.commons.future.IFuture<IServiceCallService> execute(Void args) 
						{
							return ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService(requiredOrProvidedServiceName);
						}
					}, 7, 1500).get();
						
				}
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}

	private void assertServiceCallResetsServiceInvocation(IServiceCallService service)
	{
		ServiceCall.getOrCreateNextInvocation().setTimeout(USER_TIMEOUT);
		service.call().get();
		long timeout2 = -1;
		ServiceCall nextInvocation = ServiceCall.getOrCreateNextInvocation();
		if(nextInvocation.hasUserTimeout())
		{
			timeout2 = nextInvocation.getTimeout();
			assertNotEquals(2015, timeout2);
		}
	}

	private IExternalAccess createServiceAgent(IExternalAccess platform, Class< ? > clazz)
	{
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);

		final Future<IComponentIdentifier> future = new Future<IComponentIdentifier>();
		cms.createComponent(clazz.getName() + ".class", null).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
		{
			@Override
			public void firstResultAvailable(IComponentIdentifier result)
			{
				future.setResult(result);
			}

			@Override
			public void secondResultAvailable(Map<String, Object> result)
			{
			}

			@Override
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});

		// wait for creation
		IComponentIdentifier identifier = future.get();

		IExternalAccess ret = cms.getExternalAccess(identifier).get();
	
		return ret;
	}

	private void createProxies(IExternalAccess... platforms)
	{
		for(int i = 0; i < platforms.length; i++)
		{
			for(int j = 0; j < platforms.length; j++)
			{
				if(i!=j)
				{
					Starter.createProxy(platforms[i], platforms[j]).get(timeout);
				}
			}
		}
	}
}
