package jadex.launch.test.servicecall;

import static org.junit.Assert.assertNotEquals;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ServiceCallTestNFClearTest {

	
	private static final int USER_TIMEOUT = 2015;

	@Rule 
	public TestName name = new TestName();
	
	private IExternalAccess platform1;

	private IExternalAccess platform2;

	private long timeout;
	
	@Before
	public void setUp() {
		timeout = Starter.getLocalDefaultTimeout(null);
		
		String	pid	= SUtil.createUniqueId(name.getMethodName(), 3)+"-*";
		
		platform1 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
			}).get(timeout);
		
		platform2 = Starter.createPlatform(new String[]{"-platformname", pid,
				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
			}).get(timeout);
		
		createProxies(platform1, platform2);
	}
	
	/**
	 * main thread -> decoupled provided service
	 */
	@Test
	public void testInvocationMainToProvided_decoupled() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		
		IServiceCallService service = SServiceProvider.getService(exta, IServiceCallService.class).get(timeout);
		assertServiceCallResetsServiceInvocation(service);
	}
	
	// ----------------- Single Platform tests -------------------
	
	/**
	 * agent -> decoupled provided service
	 */
	@Test
	public void testInvocationAgentToProvided_decoupled() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);

		exta.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IServiceCallService.class);
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> raw provided service
	 */
	@Test
	public void testInvocationAgentToProvided_raw() {
		IExternalAccess exta = createServiceAgent(platform1, RawServiceAgent.class);

		exta.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IServiceCallService.class);
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> direct provided service
	 */
	@Test
	public void testInvocationAgentToProvided_direct() {
		IExternalAccess exta = createServiceAgent(platform1, DirectServiceAgent.class);

		exta.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IServiceCallService.class);
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> raw required service -> provided service -> impl
	 */
	@Test
	public void testInvocationAgentToRequired_raw() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IExternalAccess exta2 = createServiceAgent(platform2, ServiceCallAgent.class);

		exta2.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = (IServiceCallService) ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("raw").get();
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> raw required service -> provided service -> impl
	 */
	@Test
	public void testInvocationAgentToRequired_direct() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IExternalAccess exta2 = createServiceAgent(platform2, ServiceCallAgent.class);

		exta2.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = (IServiceCallService) ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("direct").get();
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> decoupled required service -> provided service -> impl
	 */
	@Test
	public void testInvocationAgentToRequired_decoupled() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IExternalAccess exta2 = createServiceAgent(platform2, ServiceCallAgent.class);

		exta2.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = (IServiceCallService) ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("decoupled").get();
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	
	// ----------------- Remote Platform tests -------------------
	
	/**
	 * agent -> raw required service -> Remote -> provided service -> impl
	 */
	@Test
	public void testInvocationAgentToRemoteRequired_raw() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IExternalAccess exta2 = createServiceAgent(platform2, ServiceCallAgent.class);

		exta2.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = (IServiceCallService) ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("raw").get();
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> raw required service -> remote -> provided service -> impl
	 */
	@Test
	public void testInvocationAgentToRemoteRequired_direct() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IExternalAccess exta2 = createServiceAgent(platform2, ServiceCallAgent.class);

		exta2.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = (IServiceCallService) ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("direct").get();
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	/**
	 * agent -> decoupled required service -> remote -> provided service -> impl
	 */
	@Test
	public void testInvocationAgentToRemoteRequired_decoupled() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		IExternalAccess exta2 = createServiceAgent(platform2, ServiceCallAgent.class);

		exta2.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = (IServiceCallService) ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("decoupled").get();
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	private void assertServiceCallResetsServiceInvocation(IServiceCallService service) {
		ServiceCall.getOrCreateNextInvocation().setTimeout(USER_TIMEOUT);
		service.call().get();
		long timeout2 = -1;
		ServiceCall nextInvocation = ServiceCall.getOrCreateNextInvocation();
		if (nextInvocation.hasUserTimeout()) {
			timeout2 = nextInvocation.getTimeout();
			assertNotEquals(2015, timeout2);
		}
	}
	
	private IExternalAccess createServiceAgent(IExternalAccess platform, Class<?> clazz)
	{
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		final Future<IComponentIdentifier> future = new Future<IComponentIdentifier>();
		cms.createComponent(clazz.getName() + ".class", null).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String,Object>>()
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
		
		return cms.getExternalAccess(identifier).get();
	}
	
	private void createProxies(IExternalAccess ... platforms)
	{
		for(int i = 0; i < platforms.length; i++)
		{
			for(int j = 0; j < platforms.length; j++)
			{
				Starter.createProxy(platforms[i], platforms[j]).get(timeout);
			}
		}
	}
}
