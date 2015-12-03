package jadex.platform.service.dht;

import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IDistributedServiceRegistryService;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.ServiceRegistration;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import junit.framework.TestCase;

public class DistributedServiceRegistryTest extends TestCase
{
	@Rule 
	public TestName name = new TestName();

	private IDistributedServiceRegistryService	registry1;
//	private IDistributedKVStoreDebugService	registry2;
//	private IDistributedKVStoreDebugService	registry3;
//	private IDistributedKVStoreDebugService	registry4;
	
	private IRingNodeDebugService	ring1;
//	private IRingNodeDebugService 	ring2;
//	private IRingNodeDebugService 	ring3;
//	private IRingNodeDebugService 	ring4;

	private long	timeout;

	private IExternalAccess	platform1;
//	private IExternalAccess	platform2;
//	private IExternalAccess	platform3;
//	private IExternalAccess	platform4;



	
	@Before
	public void setUp() {
//		System.out.println("Default timeout is: " + timeout);
		timeout = Starter.getLocalDefaultTimeout(null);
		
		String	pid	= SUtil.createUniqueId(name.getMethodName(), 3)+"-*";
		
		platform1 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
			}).get(timeout);
				
//		platform2 = Starter.createPlatform(new String[]{"-platformname", pid,
//			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//		}).get(timeout);
			
//		platform3 = Starter.createPlatform(new String[]{"-platformname", pid,
//			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//		}).get(timeout);
//		
//		platform4 = Starter.createPlatform(new String[]{"-platformname", pid,
//			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//		}).get(timeout);
				
		
		// rn1 should join rn2
		
//		rn1 = SServiceProvider
//			.getService(platform1, IDebugRingNode.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		Tuple2<IDistributedServiceRegistryService, IRingNodeDebugService> t1 = createRingAgent(platform1);
		registry1 = t1.getFirstEntity();
		ring1 = t1.getSecondEntity();
		
//		Tuple2<IDistributedKVStoreDebugService, IRingNodeDebugService> t2 = createRingAgent(platform2);
//		registry2 = t2.getFirstEntity();
//		ring2 = t2.getSecondEntity();
//		
//		Tuple2<IDistributedKVStoreDebugService, IRingNodeDebugService> t3 = createRingAgent(platform3);
//		registry3 = t3.getFirstEntity();
//		ring3 = t3.getSecondEntity();
//		
//		Tuple2<IDistributedKVStoreDebugService, IRingNodeDebugService> t4 = createRingAgent(platform4);
//		registry4 = t4.getFirstEntity();
//		ring4 = t4.getSecondEntity();
//		
//		createProxies(platform1, platform2, platform3, platform4);
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

	private Tuple2<IDistributedServiceRegistryService, IRingNodeDebugService> createRingAgent(IExternalAccess platform)
	{
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		IComponentIdentifier identifier = createComponent(cms, DistributedServiceRegistryAgent.class);
		
//		IDebugRingNode declaredService = SServiceProvider.getDeclaredService(platform, IDebugRingNode.class).get();
//		System.out.println(declaredService);
		
		IDistributedServiceRegistryService registryService = SServiceProvider.getService(platform, identifier, IDistributedServiceRegistryService.class).get(timeout);
		
		IRingNodeDebugService iDebugRingNode = null; 
		
//		IComponentIdentifier[] iComponentIdentifiers = cms.getChildren(identifier).get();
//		for(IComponentIdentifier cid : iComponentIdentifiers)
//		{
//			if (cid.getLocalName().equals("RingNode")) {
				iDebugRingNode = SServiceProvider.getService(platform, identifier, IRingNodeDebugService.class).get(timeout);		
//			}
//		}
		
		iDebugRingNode.disableSchedules();
//		storeService.disableSchedules();
		return new Tuple2<IDistributedServiceRegistryService, IRingNodeDebugService>(registryService, iDebugRingNode);
	}

	private IComponentIdentifier createComponent(IComponentManagementService cms, Class<?> clazz)
	{
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
			}
		});
		
		// wait for creation
		IComponentIdentifier identifier = future.get(timeout);
		return identifier;
	}
	
	@Test
	public void testPublishLookup() {
		ServiceIdentifier sid = createSid("myName", "myType");
		
		registry1.publish("myType", sid).get();
		
		Collection<ServiceRegistration> collection = registry1.lookup("myType").get();
		ServiceRegistration next = collection.iterator().next();
		assertEquals(sid, next.getSid());
	}
	
	@Test
	public void testRenewLease() {
		ServiceIdentifier sid = createSid("myName", "myType");
		registry1.publish("myType", sid).get();
		
		Collection<ServiceRegistration> collection = registry1.lookup("myType").get();
		ServiceRegistration next = collection.iterator().next();
		long initialTimestamp = next.getTimestamp();
		

		// assure time has passed:
		try
		{
			Thread.sleep(1);
		}
		catch(InterruptedException e)
		{
		}
		registry1.publish("myType", sid).get();
		collection = registry1.lookup("myType").get();
		next = collection.iterator().next();
		long renewedTimestamp = next.getTimestamp();
		
		assertTrue(renewedTimestamp > initialTimestamp);
		assertEquals(sid, next.getSid());
	}
	
	private ServiceIdentifier createSid(String serviceName, String serviceType)
	{
		ServiceIdentifier sid = new ServiceIdentifier();
		sid.setServiceName(serviceName);
		sid.setServiceType(new ClassInfo(serviceType));
		sid.setProviderId(new ComponentIdentifier("ProviderName"));
		return sid;
	}
	
	private ID createId2(int firstByte)
	{
		return new ID(new byte[]{(byte)(firstByte - 128)});
	}
}
