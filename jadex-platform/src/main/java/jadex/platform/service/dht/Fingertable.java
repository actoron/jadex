package jadex.platform.service.dht;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * The Fingertable is a list of Fingers that point at other RingNodes to
 * increase lookup times. Because the start keys increase exponentially, lookup
 * of a specific node is logarithmic. Each entry contains a node which has an ID
 * >= startID of the entry.
 */
public class Fingertable {

	/** Finger entry of the local RingNode **/
	private Finger selfFinger;
	/** All other fingers **/
	private Finger[] fingers;
	/** Predecessor of the local RingNode **/
	private IFinger predecessor;
	/** The local ringnode **/
	
	private FingerTableListener listener;
	
	/**
	 * Receives events regarding changes in the fingertable. 
	 */
	public interface FingerTableListener
	{
		public void fingerChanged(int index, IFinger oldFinger, IFinger newFinger);
		public void successorChanged(IFinger oldFinger, IFinger newFinger);
		public void predecessorChanged(IFinger oldFinger, IFinger newFinger);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param selfSid
	 *            The local SID
	 * @param selfId
	 *            The local ID
	 * @param local
	 *            the Local RingNode
	 */
	public Fingertable(IServiceIdentifier selfSid, IID selfId, FingerTableListener listener) {
		selfFinger = new Finger(selfSid, null, selfId);
		// use n finger entries, where n is the key/hash length.
		this.fingers = new Finger[selfId.getLength()];
		// initialize fingers with start keys increasing by powers of two.
		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = new Finger(selfSid, selfId.addPowerOfTwo(i), selfId);
		}
		this.listener = listener;
	}
	
	/**
	 * Sets the listener for this fingertable.
	 * @param listener
	 */
//	public void setListener(FingerTableListener listener) {
//		this.listener = listener;
//	}
	
	/**
	 * Return the local finger entry.
	 * 
	 * @return {@link IFinger}
	 */
	public IFinger getSelf() {
		return selfFinger;
	}

	/**
	 * Return the Successor of the local node.
	 * 
	 * @return {@link IFinger}
	 */
	public Finger getSuccessor() {
		Finger result = fingers[0].getSid() == null ? selfFinger : fingers[0];
		return result;
	}

	/**
	 * Set the successor entry.
	 * 
	 * @param node
	 *            new successor.
	 */
	public void setSuccessor(IFinger node) {
		Finger oldSuccessor = getSuccessor().clone();
		getSuccessor().set(node);
		if (!SUtil.equals(oldSuccessor, node)) {
			listener.successorChanged(oldSuccessor, node);
		}
	}
	
	/**
	 * Return the finger at the given index.
	 * @param i index
	 * @return {@link Finger}
	 */
	public Finger getFinger(int i)
	{
		return fingers[i];
	}
	
	/**
	 * Return all fingers.
	 * @return Array of Fingers
	 */
	public Finger[] getFingers() {
		return fingers;
	}

	/**
	 * Return the finger that preceeds the given ID and is closest to it in the
	 * local finger table.
	 * 
	 * @param key
	 *            the ID
	 * @return {@link IFinger} The finger that is closest preceeding the given
	 *         key.
	 */
	public IFuture<IFinger> getClosestPrecedingFinger(IID key) {
		if (key == null) {
			NullPointerException e = new NullPointerException(
					"ID to determine the closest preceding node may not be "
							+ "null!");
			throw e;
		}

		Future<IFinger> ret = new Future<IFinger>();
		for (int i = this.fingers.length - 1; i >= 0; i--) {
			IFinger finger = fingers[i];
			if (finger.getNodeId().isInInterval(selfFinger.getNodeId(), key)) {
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

	/**
	 * Returns the predecessor of the local node.
	 * 
	 * @return {@link IFinger}
	 */
	public IFinger getPredecessor() {
		return predecessor;
	}

	/**
	 * Sets the predecessor of the local node.
	 * 
	 * @param predecessor
	 *            The new predecessor.
	 */
	public void setPredecessor(IFinger predecessor) {
		IFinger oldFinger = this.predecessor;
		this.predecessor = predecessor;
		if (!SUtil.equals(oldFinger, predecessor)) {
			listener.predecessorChanged(oldFinger, predecessor);
		}
	}

	// public void init(IRingNode nDash)
	// {
	// fingers[0].node = nDash.findSuccessor(self.getId().get()).get();
	// predecessor = getSuccessor().getPredecessor().get();
	//
	// getSuccessor().setPredecessor(self);
	//
	// for (int i = 0; i < fingers.length-1; i++) {
	// if (fingers[i+1].start.isInInterval(self.getId().get(),
	// fingers[i].node.getId().get(), true, false)) {
	// fingers[i+1].node = fingers[i].node;
	// } else {
	// fingers[i+1].node = nDash.findSuccessor(fingers[i+1].start).get();
	// }
	// }
	// }

	/**
	 * Marks a finger entry as invalid by setting its node entry to the local
	 * node.
	 * 
	 * @param rn
	 */
	public void setInvalid(IFinger rn) {
		for (int i = 0; i < fingers.length; i++) {
			if (rn.equals(fingers[i])) {
				log("resetting finger " + i + " to " + selfFinger);
				Finger oldFinger = fingers[i].clone();
				fingers[i].set(selfFinger);
				if (i == 0) {
					listener.successorChanged(oldFinger, selfFinger);
				} else {
					listener.fingerChanged(i, oldFinger, selfFinger);
				}
			}
		}

		if (rn.equals(predecessor)) {
			log("Resetting predecessor");
			setPredecessor(selfFinger);
		}
	}

	/** Helper methods **/

	private void log(String message) {
		System.out.println(selfFinger.getNodeId() + ": " + message);
	}
	
	@Override
	public String toString() {
		String str = new String();
		str += "======================\n";
		str += "Table for: " + selfFinger.getNodeId() + " (predecessor: "
				+ predecessor + ")" + "\n";
		str += "index \t start \t node\n";
		str += "----------------------\n";
		for (int i = 0; i < fingers.length; i++) {
			// if (fingers[i] != null) {
			str += i;
			IID node = fingers[i].getNodeId();
			str += " \t " + fingers[i].getStart() + "\t" + node;
			str += "\n";
			// }
		}
		return str;
	}

}
