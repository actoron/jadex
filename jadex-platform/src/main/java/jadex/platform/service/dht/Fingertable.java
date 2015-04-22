package jadex.platform.service.dht;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.dht.IFinger;
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
	private IFinger selfFinger;
	protected Finger[]	fingers;
	private IFinger	predecessor;

	private RingNode	local;
	
	public Fingertable(IServiceIdentifier selfSid, IID selfId, RingNode local)
	{
		this.local = local;
		selfFinger = new Finger(selfSid, null, selfId);
		this.fingers = new Finger[selfId.getLength()];
		for(int i = 0; i < fingers.length; i++)
		{
			fingers[i] = new Finger(selfSid, selfId.addPowerOfTwo(i), selfId);
		}
	}
	
	public IFinger getSelf() {
		return selfFinger;
	}

	public IFinger getSuccessor()
	{
		IFinger result = fingers[0].getSid() == null ? selfFinger : fingers[0];
//		System.out.println("returning successor: " + result);
		return result;
	}
	
	public void setSuccessor(IFinger node)
	{
		fingers[0].set(node);
	}
	
	public IFuture<IFinger> getClosestPrecedingFinger(IID key)
	{
		if (key == null) {
			NullPointerException e = new NullPointerException(
					"ID to determine the closest preceding node may not be "
							+ "null!");
			throw e;
		}
		
		Future<IFinger> ret = new Future<IFinger>();
		for(int i = this.fingers.length -1; i >= 0; i--) 
		{
			IFinger finger = fingers[i];
			if (finger.getNodeId().isInInterval(selfFinger.getNodeId(), key))
			{
				ret.setResult(finger);
				break;
			}
		}
		if (!ret.isDone()) {
			log("No closest preceding node for id " + key + ", returning null");
			ret.setResult(null);
		}
		return ret;
	}

	public IFinger getPredecessor()
	{
		return predecessor;
	}

	public void setPredecessor(IFinger predecessor)
	{
		this.predecessor = predecessor;
	}
	
	
	
	/** Helper methods **/
	
	private void log(String message) {
		System.out.println(selfFinger.getNodeId() + ": " + message);
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
		int idLength = selfFinger.getNodeId().getLength();
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(idLength, new DelegationResultListener<Void>(future));
		for (int i = 0; i < idLength; i++) {
			final Finger finger = fingers[i];
			local.findSuccessor(finger.getStart()).addResultListener(new DefaultResultListener<IFinger>()
			{

				@Override
				public void resultAvailable(IFinger result)
				{
					finger.set(result);
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
		str += "Table for: " + selfFinger.getNodeId() + " (predecessor: " + predecessor + ")" + "\n";
		str += "index \t start \t node\n";
		str += "----------------------\n";
		for(int i = 0; i < fingers.length; i++)
		{
//			if (fingers[i] != null) {
				str += i;
				IID node = fingers[i].getNodeId();
				str += " \t " + fingers[i].getStart() + "\t" + node;
				str += "\n";
//			}
		}
		return str;
	}

	public void setInvalid(IFinger rn)
	{
		for(int i = 0; i < fingers.length; i++)
		{
			if (rn.equals(fingers[i])) {
				fingers[i].set(selfFinger);
				log("resetting finger " + i + " to " + selfFinger);
			} else {
				if (rn.getNodeId().equals(fingers[i].getNodeId())) {
					System.out.println("should have been replaced: " + i);
				}
			}
		}
		
		if (rn.equals(predecessor)) {
			log("Resetting predecessor");
			predecessor = selfFinger;
		}
	}
}
