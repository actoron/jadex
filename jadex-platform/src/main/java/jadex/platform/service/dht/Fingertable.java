package jadex.platform.service.dht;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
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
	protected IFinger selfFinger;
	/** All other fingers **/
	protected Finger[] fingers;
	/** Predecessor of the local RingNode **/
	protected IFinger predecessor;
	/** The local ringnode **/
	protected RingNodeService local;

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
	public Fingertable(IServiceIdentifier selfSid, IID selfId, RingNodeService local) {
		this.local = local;
		selfFinger = new Finger(selfSid, null, selfId);
		// use n finger entries, where n is the key/hash length.
		this.fingers = new Finger[selfId.getLength()];
		// initialize fingers with start keys increasing by powers of two.
		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = new Finger(selfSid, selfId.addPowerOfTwo(i), selfId);
		}
	}

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
	public IFinger getSuccessor() {
		IFinger result = fingers[0].getSid() == null ? selfFinger : fingers[0];
		return result;
	}

	/**
	 * Set the successor entry.
	 * 
	 * @param node
	 *            new successor.
	 */
	public void setSuccessor(IFinger node) {
		fingers[0].set(node);
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
		this.predecessor = predecessor;
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
	 * Run the fixfingers algorithm. This implementation iterates over all
	 * fingers and checks if there is a better candidate.
	 * 
	 * @return
	 */
	public IFuture<Void> fixFingers() {
		Future<Void> future = new Future<Void>();
		int idLength = selfFinger.getNodeId().getLength();
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(
				idLength, new DelegationResultListener<Void>(future));
		for (int i = 0; i < idLength; i++) {
			final Finger finger = fingers[i];
			local.findSuccessor(finger.getStart()).addResultListener(
					new DefaultResultListener<IFinger>() {

						@Override
						public void resultAvailable(IFinger result) {
							finger.set(result);
							counter.resultAvailable(null);
						}
					});
		}
		return future;
	}

	/**
	 * Marks a finger entry as invalid by setting its node entry to the local
	 * node.
	 * 
	 * @param rn
	 */
	public void setInvalid(IFinger rn) {
		for (int i = 0; i < fingers.length; i++) {
			if (rn.equals(fingers[i])) {
				fingers[i].set(selfFinger);
				log("resetting finger " + i + " to " + selfFinger);
				// } else {
				// if (rn.getNodeId().equals(fingers[i].getNodeId())) {
				// System.out.println("should have been replaced: " + i);
				// }
			}
		}

		if (rn.equals(predecessor)) {
			log("Resetting predecessor");
			predecessor = selfFinger;
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
