package jadex.platform.service.dht;

import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgentFactory;
import junit.framework.TestCase;

import org.junit.Before;

public class RingNodeTest extends TestCase
{
	private RingNode	ringNode;

	@Before
	public void setUp() {
		ringNode = createRingNode();
		ringNode.init(createId(0));
		ringNode.join(null).get();
	}
	
	public void testJoin() {
		RingNode node2 = createRingNode();
		node2.init(createId(128));
		node2.join(ringNode).get();

		stabilize(new RingNode[]{node2, ringNode});
		
//		node2.stabilize().get();
		
		System.out.println(ringNode.getFingerTable());
		
		assertEquals(ringNode, node2.getPredecessor().get());
		assertEquals(node2, ringNode.getPredecessor().get());
		
		assertEquals(ringNode, node2.getSuccessor().get());
		assertEquals(node2, ringNode.getSuccessor().get());
		
		// ringNode table should be:
		// 0 (1) -> Node(128)
		// 1 (2) -> Node(128)
		// 2 (4) -> Node(128)
		// 3 (8) -> Node(128)
		// 4 (16) -> Node(128)
		// 5 (32) -> Node(128)
		// 6 (64) -> Node(128)
		// 7 (128) -> Node(128)
		
		assertEquals(node2, ringNode.getFingers().get().get(0).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(1).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(2).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(3).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(4).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(5).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(6).getNodeId());
		assertEquals(node2, ringNode.getFingers().get().get(7).getNodeId());
		
		// node2 table should be:
		// 0 (129) -> Node(0)
		// 1 (130) -> Node(0)
		// 2 (132) -> Node(0)
		// 3 (136) -> Node(0)
		// 4 (144) -> Node(0)
		// 5 (160) -> Node(0)
		// 6 (192) -> Node(0)
		// 7 (0)   -> Node(0)
		
		System.out.println(node2.getFingerTable());
		
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[0].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[1].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[2].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[3].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[4].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[5].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[6].getNodeId());
		assertEquals(ringNode.getId().get(), node2.getFingerTable().fingers[7].getNodeId());
		
	}
	
	IInternalAccess agent = new IInternalAccess()
	{
		
		@Override
		public IFuture<Map<String, Object>> killComponent(Exception e)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IFuture<Map<String, Object>> killComponent()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IParameterGuesser getParameterGuesser()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IModelInfo getModel()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Logger getLogger()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IValueFetcher getFetcher()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IExternalAccess getExternalAccess()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getConfiguration()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IComponentIdentifier getComponentIdentifier()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> T getComponentFeature(Class< ? extends T> type)
		{
			return (T) new IExecutionFeature()
			{
				
				@Override
				public IFuture<Void> waitForTick(IComponentStep<Void> run)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public IFuture<Void> waitForDelay(long delay)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public IFuture<Void> waitForDelay(long delay, boolean realtime)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public <T> IFuture<T> scheduleStep(final IComponentStep<T> step)
				{
					final Future<T> ret = new Future<T>();
					new Thread() {

						@Override
						public void run()
						{
							IFuture<T> execute = step.execute(null);
							execute.addResultListener(new DelegationResultListener<T>(ret));
						}
						
					}.start();
					return ret;
				}
				
				@Override
				public <T> IFuture<T> scheduleImmediate(IComponentStep<T> step)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean isComponentThread()
				{
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
				{
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
		
		@Override
		public IComponentDescription getComponentDescription()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public ClassLoader getClassLoader()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public IFuture<IComponentIdentifier[]> getChildren(String type)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T getComponentFeature0(Class<? extends T> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Exception getException() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	
	private RingNode createRingNode()
	{
		RingNode rn = new RingNode();
		rn.agent = this.agent;
		return rn; 
	}

	public void testJoin2() {
		RingNode node2 = createRingNode();
		node2.init(createId(64));
		node2.join(ringNode);
		
		RingNode node3 = createRingNode();
		node3.init(createId(128));
		node3.join(ringNode);
		
		stabilize(new RingNode[]{ringNode, node2, node3});
	
		assertEquals(ringNode, node2.getPredecessor().get());
		assertEquals(node2, node3.getPredecessor().get());
		assertEquals(node3, ringNode.getPredecessor().get());
		
		assertEquals(node2, ringNode.getSuccessor().get());
		assertEquals(node3, node2.getSuccessor().get());
		assertEquals(ringNode, node3.getSuccessor().get());
	}
	


	public void testFindPredecessor() {
		RingNode node2 = createRingNode();
		node2.init(createId(128));
		node2.join(ringNode);
		
		for(int i = 0; i < 7; i++)
		{
			ID otherNode = ((ID)node2.getId().get()).subtractPowerOfTwo(i);
			IFinger pre = node2.findPredecessor(otherNode).get();
			assertEquals(ringNode.getId().get(), pre);;
		}
		
		ID otherNode = ((ID)node2.getId().get()).subtractPowerOfTwo(7);
		IFinger pre = node2.findPredecessor(otherNode).get();
		assertEquals(node2.getId().get(), pre);;
	}
	
	public void testMoreNodes() {
		
		RingNode[] nodes = new RingNode[9];
		
		nodes[0] = ringNode;
		
		for(int i = 1; i < 9; i++)
		{
			RingNode rn = new RingNode();
			IID id = ringNode.getId().get().addPowerOfTwo(i-1);
			System.out.println("creating node with id " + id);
			rn.init(id);
			rn.join(ringNode);
			nodes[i] = rn;
		}
		
		stabilize(nodes);
		
//		for(int i = 0; i < nodes.length; i++)
//		{
//			System.out.println(nodes[i].getId().get());
//		}
		
//		ringNode.getFinger().fixFingers();
		System.out.println(ringNode.getFingerTable());
//		System.out.println(nodes[0].getFinger());
		
		// existing nodes: 0, 1, 2, 4, 8, 16, 32, 64, 128
		
		assertEquals(createId(0), getSuccessorId(ringNode, 0));
		assertEquals(createId(1), getSuccessorId(ringNode, 1));
		assertEquals(createId(2), getSuccessorId(ringNode, 2));
		assertEquals(createId(4), getSuccessorId(ringNode, 3));
		assertEquals(createId(4), getSuccessorId(ringNode, 4));
		assertEquals(createId(8), getSuccessorId(ringNode, 5));
		assertEquals(createId(8), getSuccessorId(ringNode, 8));
		assertEquals(createId(16), getSuccessorId(ringNode, 9));
		assertEquals(createId(16), getSuccessorId(ringNode, 16));
		assertEquals(createId(32), getSuccessorId(ringNode, 17));
		assertEquals(createId(32), getSuccessorId(ringNode, 32));
		assertEquals(createId(64), getSuccessorId(ringNode, 33));
		assertEquals(createId(64), getSuccessorId(ringNode, 64));
		assertEquals(createId(128), getSuccessorId(ringNode, 65));
		assertEquals(createId(128), getSuccessorId(ringNode, 128));
		assertEquals(createId(0), getSuccessorId(ringNode, 129));
		assertEquals(createId(0), getSuccessorId(ringNode, 0));
		
	}
	
	private void stabilize(RingNode[] nodes)
	{
		for(int j = 0; j < nodes.length; j++)
		{
			for(int i = 0; i < nodes.length; i++)
			{
				try {
					nodes[i].stabilize().get(-1);
					nodes[i].fixFingers().get(-1);
					nodes[i].stabilize().get(-1);
				} catch (TimeoutException e) {
					System.out.println("to for node " + nodes[i]);
				}
			}
		}
	}

	private IID getSuccessorId(RingNode querytarget, int id)
	{
		return querytarget.findSuccessor(createId(id)).get().getNodeId();
	}


	private ID createId(int i)
	{
		return new ID(new byte[]{(byte) (i-128)});
	}
}
