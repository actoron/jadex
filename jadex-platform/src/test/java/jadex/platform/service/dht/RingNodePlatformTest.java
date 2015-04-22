package jadex.platform.service.dht;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IDebugRingNode;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.SUtil;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class RingNodePlatformTest extends TestCase
{
	@Rule 
	public TestName name = new TestName();

	private IDebugRingNode	rn1;
	private IDebugRingNode	rn2;
	private IDebugRingNode	rn3;

	private long	timeout;

	private IExternalAccess	platform3;

	private IExternalAccess	platform2;

	private IExternalAccess	platform1;

	
	@Before
	public void setUp() {
		timeout = BasicService.getLocalDefaultTimeout();
		String	pid	= SUtil.createUniqueId(name.getMethodName(), 3)+"-*";
		
		platform1 = Starter.createPlatform(new String[]{"-platformname", pid,
//					"-relaytransport", "false",
//					"-deftimeout", Long.toString(timeout),
//					"-logging", "true",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//			"-component", "jadex/platform/service/dht/RingAgent.class"
			}).get(timeout);
				
		platform2 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
	//					"-relaytransport", "false",
	//					"-deftimeout", Long.toString(timeout),
	//					"-logging", "true",
		}).get(timeout);
			
		platform3 = Starter.createPlatform(new String[]{"-platformname", pid,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
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

	private IDebugRingNode createRingAgent(IExternalAccess platform)
	{
		IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		
		final Future<IComponentIdentifier> future = new Future<IComponentIdentifier>();
		cms.createComponent(RingAgent.class.getName() + ".class", null).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String,Object>>()
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
		
		IDebugRingNode iDebugRingNode = SServiceProvider.getService(platform, identifier, IDebugRingNode.class).get(timeout);
		iDebugRingNode.disableSchedules();
		return iDebugRingNode;
	}
	
	@Test
	public void testJoin() {
		rn2.join(rn1).get();
		stabilize(new IDebugRingNode[]{rn1, rn2});
		System.out.println(rn1);
		System.out.println(rn2);
		assertCircle(rn1, rn2);
	}
	
	@Test
	public void testJoin3() {
		rn2.join(rn1).get();
		rn3.join(rn1).get();
		stabilize(new IDebugRingNode[]{rn1, rn2, rn3});
		assertCircle(rn1, rn2, rn3);
	}
	
	@Test
	public void testLeave3() {
		rn2.join(rn1).get();
		rn3.join(rn1).get();
		stabilize(new IDebugRingNode[]{rn1, rn2, rn3});
		System.out.println("killing platform of " + rn3.getId().get());
//		platform3.killComponent().get();
		platform3.killComponent().get();
		
		stabilize(new IDebugRingNode[]{rn1, rn2});
//		stabilize(new IDebugRingNode[]{rn1, rn2});
		System.out.println(rn1.getFingerTableString().get());
		System.out.println(rn2.getFingerTableString().get());

		assertCircle(rn1, rn2);
	}
	
	private void assertCircle(IDebugRingNode ... rns)
	{
		IRingNode[] circleContents = new IRingNode[rns.length];
		IRingNode suc = rns[0];
		for(int i = 0; i < rns.length; i++)
		{
 			IFuture<IFinger> successor = suc.getSuccessor();
			IFinger sucsuc = successor.get();
			suc = getService(rns, sucsuc.getSid());
			if (suc == null) {
				throw new RuntimeException("got null service");
			}
			circleContents[i] = suc;
		}
		
		System.out.print("circle is: ");
		for(int i = 0; i < rns.length; i++)
		{
			boolean found = false;
			IID wanted = rns[i].getId().get(timeout);
			for(int j = 0; j < circleContents.length; j++)
			{
				IID iid = circleContents[j].getId().get(timeout);
				if (iid.equals(wanted)) {
					found = true;
					System.out.print(iid + ", ");
				}
			}
			assertTrue("Node " + wanted + " has to be in the circle",found);
		}
		System.out.println();
		
		assertEquals(rns[0].getId().get(), suc.getId().get());
	}

	private IDebugRingNode getService(IDebugRingNode[] rns, IServiceIdentifier sid)
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

	private void stabilize(IDebugRingNode[] nodes)
	{
		for(int j = 0; j < nodes.length; j++)
		{
			for(int i = 0; i < nodes.length; i++)
			{
				try {
					nodes[i].stabilize().get(10000);
					nodes[i].fixFingers().get(10000);
					nodes[i].stabilize().get(10000);
				} catch (TimeoutException e) {
					System.out.println("to for node " + nodes[i].getId().get());
				}
			}
		}
	}
}
