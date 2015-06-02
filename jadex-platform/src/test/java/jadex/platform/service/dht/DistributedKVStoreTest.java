package jadex.platform.service.dht;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class DistributedKVStoreTest extends TestCase
{
	@Rule 
	public TestName name = new TestName();

	private IDistributedKVStoreService	store1;
	private IDistributedKVStoreService	store2;
	private IDistributedKVStoreService	store3;
	
	private IRingNodeDebugService	ring1;
	private IRingNodeDebugService 	ring2;
	private IRingNodeDebugService 	ring3;

	private long	timeout;

	private IExternalAccess	platform3;

	private IExternalAccess	platform2;

	private IExternalAccess	platform1;

	
	@Before
	public void setUp() {
		
//		System.out.println("Default timeout is: " + timeout);
		timeout = Starter.getLocalDefaultTimeout(null);
		
		String	pid	= SUtil.createUniqueId(name.getMethodName(), 3)+"-*";
		
		platform1 = Starter.createPlatform(new String[]{"-platformname", pid,
//					"-relaytransport", "false",
//					"-deftimeout", Long.toString(timeout),
//					"-logging", "true",
			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//			"-component", "jadex/platform/service/dht/RingAgent.class"
			}).get(timeout);
				
		platform2 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
	//					"-relaytransport", "false",
	//					"-deftimeout", Long.toString(timeout),
	//					"-logging", "true",
		}).get(timeout);
			
		platform3 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-dht true -saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
	//					"-relaytransport", "false",
	//					"-deftimeout", Long.toString(timeout),
	//					"-logging", "true",
		}).get(timeout);
				
		
		// rn1 should join rn2
		
//		rn1 = SServiceProvider
//			.getService(platform1, IDebugRingNode.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		Tuple2<IDistributedKVStoreService, IRingNodeDebugService> t1 = createRingAgent(platform1);
		store1 = t1.getFirstEntity();
		ring1 = t1.getSecondEntity();
		
		Tuple2<IDistributedKVStoreService, IRingNodeDebugService> t2 = createRingAgent(platform2);
		store2 = t2.getFirstEntity();
		ring2 = t2.getSecondEntity();
		
		Tuple2<IDistributedKVStoreService, IRingNodeDebugService> t3 = createRingAgent(platform3);
		store3 = t3.getFirstEntity();
		ring3 = t3.getSecondEntity();
		
		createProxies(platform1, platform2, platform3);
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

	private Tuple2<IDistributedKVStoreService, IRingNodeDebugService> createRingAgent(IExternalAccess platform)
	{
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		final Future<IComponentIdentifier> future = new Future<IComponentIdentifier>();
		cms.createComponent(DistributedKVStoreAgent.class.getName() + ".class", null).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String,Object>>()
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
		
//		IDebugRingNode declaredService = SServiceProvider.getDeclaredService(platform, IDebugRingNode.class).get();
//		System.out.println(declaredService);
		
		IDistributedKVStoreService storeService = SServiceProvider.getService(platform, identifier, IDistributedKVStoreService.class).get(timeout);
		
		IRingNodeDebugService iDebugRingNode = null; 
		
//		IComponentIdentifier[] iComponentIdentifiers = cms.getChildren(identifier).get();
//		for(IComponentIdentifier cid : iComponentIdentifiers)
//		{
//			if (cid.getLocalName().equals("RingNode")) {
				iDebugRingNode = SServiceProvider.getService(platform, identifier, IRingNodeDebugService.class).get(timeout);		
//			}
//		}
		
		iDebugRingNode.disableSchedules();
		return new Tuple2<IDistributedKVStoreService, IRingNodeDebugService>(storeService, iDebugRingNode);
	}
	
	@Test
	public void testSave() {
		store1.storeLocal("test", "testValue");
		assertEquals("testValue", store1.lookup("test").get());
	}
	
//	@Test
//	public void testJoin() {
//		store2.join(store1).get();
//		stabilize2(new IRingNodeDebugService[]{store1, store2}).get();
//	}
	
//	@Test
//	public void testKillPlatform() {
//		store2.join(store1).get();
//		stabilize2(new IRingNodeDebugService[]{store1, store2, store3}).get();
//		store3.join(store1).get();
//		stabilize2(new IRingNodeDebugService[]{store1, store2, store3}).get();
//		stabilize2(new IRingNodeDebugService[]{store1, store2, store3}).get();
//		
//		IID iid = store3.getId().get();
//		System.out.println("killing platform of node: " + iid);
//		platform3.killComponent().get();
//		
//		System.out.println("platform killed");
//		
//		stabilize(new IRingNodeDebugService[]{store1, store2}).get();
//		
//		System.out.println(store1.getFingerTableString().get());
//		System.out.println(store2.getFingerTableString().get());
//
//		assertCircle(store1, store2);
//	}
	
	
	
	// -----------------------------
	// --------- HELPER ------------
	// -----------------------------
	
	private void assertCircle(IRingNodeDebugService ... rns)
	{
		IRingNodeService[] circleContents = new IRingNodeService[rns.length];
		IRingNodeService suc = rns[0];
		System.out.print("circle is: ");
		System.out.print(suc.getId().get(timeout) + ", ");
		for(int i = 0; i < rns.length; i++)
		{
 			IFuture<IFinger> successor = suc.getSuccessor();
			IFinger sucsuc = successor.get(timeout);
			suc = getService(rns, sucsuc.getSid());
			System.out.print(suc.getId().get(timeout) + ", ");
			if (suc == null) {
				throw new RuntimeException("got null service");
			}
			circleContents[i] = suc;
		}
		
		for(int i = 0; i < rns.length; i++)
		{
			boolean found = false;
			IID wanted = rns[i].getId().get(timeout);
			for(int j = 0; j < circleContents.length; j++)
			{
				IID iid = circleContents[j].getId().get(timeout);
				if (iid.equals(wanted)) {
					found = true;
				}
			}
			assertTrue("Node " + wanted + " has to be in the circle",found);
		}
		System.out.println();
		
		assertEquals(rns[0].getId().get(), suc.getId().get());
	}

	private IRingNodeDebugService getService(IRingNodeDebugService[] rns, IServiceIdentifier sid)
	{
		for(int i = 0; i < rns.length; i++)
		{
			IService s = (IService)rns[i];
			IComponentIdentifier providerId = s.getServiceIdentifier().getProviderId();
			IComponentIdentifier providerId2 = sid.getProviderId();
			if (providerId.equals(providerId2)) {
				return rns[i];
			}
		}
		return null;
		
	}
	
	private void fixFingers(IRingNodeDebugService[] nodes) {
		for(int j = 0; j < nodes.length; j++)
		{
			for(int i = 0; i < nodes.length; i++)
			{
				try {
//					nodes[i].stabilize().get(10000);
					nodes[i].fixFingers().get(10000);
//					nodes[i].stabilize().get(10000);
				} catch (TimeoutException e) {
					System.out.println("to for node " + nodes[i].getId().get());
//					e.printStackTrace();
				}
			}
		}
	}

	private IFuture<Void> stabilize2(IRingNodeDebugService[] nodes) {
		stabilize(nodes).get();
		return stabilize(nodes);
	}
	
	private IFuture<Void> stabilize(IRingNodeDebugService[] nodes)
	{
		final Future<Void> future = new Future<Void>();

		CounterResultListener<Void> rejoinListener = new CounterResultListener<Void>((nodes.length), new DelegationResultListener<Void>(future))
		{

			@Override
			public void resultAvailable(Void result)
			{
				super.resultAvailable(result);
				System.out.println("count: " + cnt);
			}

			@Override
			public void exceptionOccurredIfUndone(Exception exception)
			{
//				if(exception instanceof UnjoinedException)
//				{
//					System.out.println("initiate re-join");
//					rn2.join(rn1);
//				} else {
					exception.printStackTrace();
//				}
			}
		};

//		for(int j = 0; j < nodes.length; j++)
//		{
			for(int i = 0; i < nodes.length; i++)
			{
				try
				{
//					ServiceCall.getOrCreateNextInvocation().setTimeout(5000);
					nodes[i].stabilize().addResultListener(rejoinListener);
					// stabilize.addResultListener(rejoinListener);
					// nodes[i].fixFingers().get(10000);
					// nodes[i].stabilize().get(10000);
				}
				catch(TimeoutException e)
				{
					System.out.println("to for node " + nodes[i]);
//					e.printStackTrace();
				}
			}
//		}
		return future;
	}
}
