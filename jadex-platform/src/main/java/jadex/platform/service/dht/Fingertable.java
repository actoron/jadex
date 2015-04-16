package jadex.platform.service.dht;

import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

public class Fingertable 
{
	private RingNode self;
	
	private IID selfId;
	
	protected Finger[]	fingers;
	
	private IRingNode	predecessor;
	
	public Fingertable(RingNode self)
	{
		this.self = self;
		IID id = self.getId().get();
		selfId = id;
		this.fingers = new Finger[id.getLength()];
		for(int i = 0; i < fingers.length; i++)
		{
			fingers[i] = new Finger(self, id.addPowerOfTwo(i));
		}
	}

	public IRingNode getSuccessor()
	{
		return fingers[0].getNode() == null ? self : fingers[0].getNode();
	}
	
	public void setSuccessor(IRingNode node)
	{
		fingers[0].setNode(node);
	}
	
	public IFuture<IRingNode> getClosestPrecedingFinger(IID key)
	{
		if (key == null) {
			NullPointerException e = new NullPointerException(
					"ID to determine the closest preceding node may not be "
							+ "null!");
			throw e;
		}
		
		Future<IRingNode> ret = new Future<IRingNode>();
		for(int i = this.fingers.length -1; i >= 0; i--) 
		{
			IRingNode fingerNode = fingers[i].getNode();
			if (fingerNode.getId().get().isInInterval(selfId, key))
			{
				ret.setResult(fingerNode);
				break;
			}
		}
		if (!ret.isDone()) {
			log("No closest preceding node for id " + key + ", returning null");
			ret.setResult(null);
		}
		return ret;
	}

	public IRingNode getPredecessor()
	{
		return predecessor;
	}

	public void setPredecessor(IRingNode predecessor)
	{
		this.predecessor = predecessor;
	}
	
	
	
	/** Helper methods **/
	
	private void log(String message) {
		System.out.println(selfId + ": " + message);
	}

//	public void init(IRingNode nDash)
//	{
//		fingers[0].node = nDash.findSuccessor(self.getId().get()).get();
//		predecessor = getSuccessor().getPredecessor().get();
//		
//		getSuccessor().setPredecessor(self);
//		
//		for (int i = 0; i < fingers.length-1; i++) {
//			if (fingers[i+1].start.isInInterval(self.getId().get(), fingers[i].node.getId().get(), true, false)) {
//				fingers[i+1].node = fingers[i].node;
//			} else {
//				fingers[i+1].node = nDash.findSuccessor(fingers[i+1].start).get();
//			}
//		}
//	}
	
	public IFuture<Void> fixFingers() {
		Future<Void> future = new Future<Void>();
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(selfId.getLength(), new DelegationResultListener<Void>(future));
		for (int i = 0; i < selfId.getLength(); i++) {
			final Finger finger = fingers[i];
			self.findSuccessor(finger.getStart()).addResultListener(new DefaultResultListener<IRingNode>()
			{

				@Override
				public void resultAvailable(IRingNode result)
				{
					finger.setNode(result);
					counter.resultAvailable(null);
				}
			});
		}
		return future;
	}
	
	abstract class MyResultListener<T> implements IResultListener<T> {

		private IRingNode	rn;

		public MyResultListener(IRingNode rn)
		{
			this.rn = rn;
		}
		
		@Override
		public void exceptionOccurred(Exception exception)
		{
			exception.printStackTrace();
//			final ProxyHolder proxyHolder = proxies.get(id);
//			if (proxyHolder != null && proxyHolder.spaceObject != null) {
//				System.out.println("Removing proxy: " + proxyHolder.spaceObject);
//				proxies.remove(proxyHolder);
//				EnvironmentService.getSpace(agent).addResultListener(new DefaultResultListener<IEnvironmentSpace>()
//				{
//
//					@Override
//					public void resultAvailable(IEnvironmentSpace space)
//					{
//						space.destroySpaceObject(proxyHolder.spaceObject.getId());
//					}
//				});
//			}
		}
		
	}

//	public void updateOthers()
//	{
//		for (int i = 0; i< fingers.length; i++) {
////			// find last node p whose ith finger might be me
//			ID preId = self.getId().get().subtractPowerOfTwo(i);
//			IRingNode p = self.findPredecessor(preId);
//			log("telling " + p + " about me (" + self + ") at pos " + i);
//			p.updateFingerTable(self, i);
//		}
//	}

//	public void updateFingerTable(IRingNode s, int i)
//	{
//		if (fingers[i].node != null) {
//			ID sId = ID.get(s);
//			ID end = ID.get(fingers[i].node);
//			ID selfId = self.getId().get();
//			if (sId.isInInterval(selfId, end, false, false) ) {
////				if (fingers[i].node != self) {
//					log("Updated finger entry " + i + " - " + fingers[i] + " with node: " + s);
////				}
//				fingers[i].node = s;
//				IRingNode p = predecessor;
//				p.updateFingerTable(s, i);
//			}
//		}
//	}
	
	@Override
	public String toString()
	{
		String str = new String();
		str += "======================\n";
		str += "Table for: " + self.getId().get() + " (predecessor: " + predecessor + ")" + "\n";
		str += "index \t start \t node\n";
		str += "----------------------\n";
		for(int i = 0; i < fingers.length; i++)
		{
//			if (fingers[i] != null) {
				str += i;
				IRingNode node = fingers[i].getNode();
				str += " \t " + fingers[i].getStart() + "\t" + (node != null ? ID.get(node) : null);
				str += "\n";
//			}
		}
		return str;
	}
}
