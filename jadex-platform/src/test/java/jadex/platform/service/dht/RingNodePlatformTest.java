package jadex.platform.service.dht;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.commons.SUtil;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import junit.framework.TestCase;

public class RingNodePlatformTest extends TestCase
{
	@Rule 
	public TestName name = new TestName();

	private IRingNodeDebugService	rn1;
	private IRingNodeDebugService	rn2;
	private IRingNodeDebugService	rn3;

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
			"-niotcptransport", "true", "-relaytransport", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//			"-component", "jadex/platform/service/dht/RingAgent.class"
			}).get(timeout);
				
		platform2 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-niotcptransport", "true", "-relaytransport", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
	//					"-relaytransport", "false",
	//					"-deftimeout", Long.toString(timeout),
	//					"-logging", "true",
		}).get(timeout);
			
		platform3 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-niotcptransport", "true", "-relaytransport", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
	//					"-relaytransport", "false",
	//					"-deftimeout", Long.toString(timeout),
	//					"-logging", "true",
		}).get(timeout);
				
		
		// rn1 should join rn2
		
//		rn1 = SServiceProvider
//			.getService(platform1, IDebugRingNode.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		rn1 = createRingAgent(platform1);
		rn2 = createRingAgent(platform2);
		rn3 = createRingAgent(platform3);
		
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

	private IRingNodeDebugService createRingAgent(IExternalAccess platform)
	{
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		final Future<IComponentIdentifier> future = new Future<IComponentIdentifier>();
		cms.createComponent(RingNodeAgent.class.getName() + ".class", null).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String,Object>>()
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
		
		IRingNodeDebugService iDebugRingNode = SServiceProvider.getService(platform, identifier, IRingNodeDebugService.class).get(timeout);
		iDebugRingNode.disableSchedules();
		return iDebugRingNode;
	}
	
	@Test
	public void testJoin() {
		rn2.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2}).get();
//		rn2.stabilize().get();
//		rn1.stabilize().get();
//		System.out.println(rn1.getFingerTableString().get());
//		System.out.println(rn2.getFingerTableString().get());
		assertCircle(rn1, rn2);
	}
	
	@Test
	public void testJoin3() throws InterruptedException {
		rn2.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
		rn3.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
//		System.out.println(rn1.getFingerTableString().get());
		assertCircle(rn1, rn2, rn3);
	}
	
	@Test
	public void testKillPlatform() {
		rn2.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
		rn3.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
		
//		System.out.println(rn1.getFingerTableString().get());
//		System.out.println(rn2.getFingerTableString().get());
		
//		long timeout2 = ServiceCall.getOrCreateNextInvocation().getTimeout();
//		System.out.println("timeout is: " + timeout2);
		IID iid = rn3.getId().get();
//		Finger.killedId = iid;
		System.out.println("killing platform of node: " + iid);
		platform3.killComponent().get();
		
//		try
//		{
//			Thread.sleep(1000);
//		}
//		catch(InterruptedException e)
//		{
//		}
		
		System.out.println("platform killed");
		
//		timeout2 = ServiceCall.getOrCreateNextInvocation().getTimeout();
//		System.out.println("timeout is: " + timeout2);
		try {
			stabilize(new IRingNodeDebugService[]{rn1, rn2}).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			stabilize(new IRingNodeDebugService[]{rn1, rn2}).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		stabilize(new IDebugRingNode[]{rn1, rn2});
		
		System.out.println(rn1.getFingerTableString().get());
		System.out.println(rn2.getFingerTableString().get());

		assertCircle(rn1, rn2);
	}
	
	
	@Test
	public void testFindSuccessor() throws InterruptedException {
		rn2.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
		rn3.join(rn1).get();
		stabilize2(new IRingNodeDebugService[]{rn1, rn2, rn3}).get();
		
//		System.out.println("RN1: " + rn1.getId().get());
//		System.out.println("RN2: " + rn2.getId().get());
//		System.out.println("RN3: " + rn3.getId().get());
//		assertCircle(rn1, rn2, rn3);
		
		IFinger suc1_2 = rn1.findSuccessor(rn2.getId().get()).get();
		IFinger suc1_3 = rn1.findSuccessor(rn3.getId().get()).get();
		assertEquals(rn2.getId().get(), suc1_2.getNodeId());
		assertEquals(rn3.getId().get(), suc1_3.getNodeId());
		
		IFinger suc2_1 = rn2.findSuccessor(rn1.getId().get()).get();
		IFinger suc2_3 = rn2.findSuccessor(rn3.getId().get()).get();
		assertEquals(rn1.getId().get(), suc2_1.getNodeId());
		assertEquals(rn3.getId().get(), suc2_3.getNodeId());
		
		IFinger suc3_1 = rn3.findSuccessor(rn1.getId().get()).get();
		IFinger suc3_2 = rn3.findSuccessor(rn2.getId().get()).get();
		assertEquals(rn1.getId().get(), suc3_1.getNodeId());
		assertEquals(rn2.getId().get(), suc3_2.getNodeId());
	}
	
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
//				resultAvailable(null);
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
