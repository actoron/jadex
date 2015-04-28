package jadex.launch.test.servicecall;

import java.util.Map;

import javax.validation.constraints.AssertTrue;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IDebugRingNode;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.dht.RingAgent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.junit.Assert.*;

public class ServiceCallTestNFClearTest {

	
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
	}
	
	/**
	 * testcase -> remote invocation handler
	 */
	@Test
	public void testReset_AfterRemoteInvocationOnly() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);
		
		IServiceCallService service = SServiceProvider.getService(exta, IServiceCallService.class).get(timeout);
		assertServiceCallResetsServiceInvocation(service);
	}
	
	/**
	 * agent -> provided service
	 */
	@Test
	public void testReset_AfterLocalProvidedInvocation() {
		IExternalAccess exta = createServiceAgent(platform1, DecoupledServiceAgent.class);

		exta.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				IServiceCallService service = SServiceProvider.getService(ia, IServiceCallService.class).get(timeout);
				assertServiceCallResetsServiceInvocation(service);
				return Future.DONE;
			}
		}).get();
	}
	
	private void assertServiceCallResetsServiceInvocation(IServiceCallService service) {
		ServiceCall.getOrCreateNextInvocation().setTimeout(2015);
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
}
