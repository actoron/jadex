package jadex.platform.service.dht;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;

/**
 * Finger entry. Contains a start IID, a nodeId >= startId and a
 * ServiceIdentifier of the IRingNode pointed at.
 */
public class Finger implements IFinger {
	
	/** ServiceIdentifier of the Node **/
	protected IServiceIdentifier sid;
	/** ID of the Node **/
	protected IID nodeId;
	/** Start key of this finger **/
	protected IID start;

	/**
	 * Constructor.
	 */
	public Finger() {
	}

	/**
	 * Constructor.
	 */
	public Finger(IServiceIdentifier sid, IID start, IID nodeId) {
		this.sid = sid;
		this.start = start;
		this.nodeId = nodeId;
	}

	/**
	 * Constructor.
	 */
	public Finger(IRingNode ringNode, IID start) {
		this.start = start;
		this.sid = ((IService) ringNode).getServiceIdentifier();
		this.nodeId = ringNode.getId().get();
	}

	/**
	 * Get the start IID Key of this Finger.
	 */
	public IID getStart() {
		return start;
	}

	/**
	 * Set the start IID Key of this Finger.
	 */
	public void setStart(IID start) {
		this.start = start;
	}

	/**
	 * Set the SID of this Finger.
	 */
	public void setSid(IServiceIdentifier sid) {
		this.sid = sid;
	}

	/**
	 * Get the SID of this Finger.
	 */
	public IServiceIdentifier getSid() {
		return sid;
	}

	/**
	 * Get the Node ID of this Finger.
	 */
	public IID getNodeId() {
		return nodeId;
	}

	/**
	 * Set the Node ID of this Finger.
	 */
	public void setNodeId(IID nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * Use nodeId and Sid of the given finger. (Keeps startId)
	 * 
	 * @param other
	 *            The other finger.
	 */
	public void set(IFinger other) {
		this.nodeId = other.getNodeId();
		this.sid = other.getSid();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Finger other = (Finger) obj;
		if (sid == null) {
			if (other.sid != null)
				return false;
		} else if (!sid.equals(other.sid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "start: " + start + ", node: " + nodeId + ", sid: " + sid;
	}

}
