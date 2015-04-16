package jadex.platform.service.dht;

import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import junit.framework.TestCase;

import org.junit.Before;

public class RingNodeTest extends TestCase
{
	private RingNode	ringNode;

	@Before
	public void setUp() {
		ringNode = new RingNode();
		ringNode.init(createId(0));
		ringNode.join(null).get();
	}
	
	public void testJoin() {
		RingNode node2 = new RingNode();
		node2.init(createId(128));
		node2.join(ringNode).get();

		stabilize(new RingNode[]{node2, ringNode});
		
//		try
//		{
//			Thread.sleep(1000);
//		}
//		catch(InterruptedException e)
//		{
//			e.printStackTrace();
//		}
		
		node2.stabilize().get();
		
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
		
		assertEquals(node2, ringNode.getFingers().get().get(0).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(1).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(2).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(3).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(4).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(5).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(6).getNode());
		assertEquals(node2, ringNode.getFingers().get().get(7).getNode());
		
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
		
		assertEquals(ringNode, node2.getFingerTable().fingers[0].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[1].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[2].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[3].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[4].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[5].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[6].getNode());
		assertEquals(ringNode, node2.getFingerTable().fingers[7].getNode());
		
	}
	
	public void testJoin2() {
		RingNode node2 = new RingNode();
		node2.init(createId(64));
		node2.join(ringNode);
		
		RingNode node3 = new RingNode();
		node3.init(createId(128));
		node3.join(ringNode);
		
		stabilize(new RingNode[]{ringNode, node2, node3});
	
//		ringNode.getFinger().fixFingers();
		
		assertEquals(ringNode, node2.getPredecessor().get());
		assertEquals(node2, node3.getPredecessor().get());
		assertEquals(node3, ringNode.getPredecessor().get());
		
		assertEquals(node2, ringNode.getSuccessor().get());
		assertEquals(node3, node2.getSuccessor().get());
		assertEquals(ringNode, node3.getSuccessor().get());
		
//		System.out.println(ringNode.getFinger());
//		System.out.println(node2.getFinger());
//		System.out.println(node3.getFinger());

	}
	
	public void testFindPredecessor() {
		RingNode node2 = new RingNode();
		node2.init(createId(128));
		node2.join(ringNode);
		
		for(int i = 0; i < 7; i++)
		{
			ID otherNode = ((ID)node2.getId().get()).subtractPowerOfTwo(i);
			IRingNode pre = node2.findPredecessor(otherNode);
			assertEquals(ringNode, pre);;
		}
		
		ID otherNode = ((ID)node2.getId().get()).subtractPowerOfTwo(7);
		IRingNode pre = node2.findPredecessor(otherNode);
		assertEquals(node2, pre);;
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
				nodes[i].stabilize().get();
				nodes[i].fixFingers().get();
				nodes[i].stabilize().get();
			}
		}
	}

	private IID getSuccessorId(RingNode querytarget, int id)
	{
		return querytarget.findSuccessor(createId(id)).get().getId().get();
	}


	private ID createId(int i)
	{
		return new ID(new byte[]{(byte) (i-128)});
	}
}
