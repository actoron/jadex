package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Reference;

/**
 * Events thrown by RingNodeService to inform the application layer using the
 * ring about events.
 */
@Reference(local=true, remote=false)
public class RingNodeEvent
{
	/** Type specification of this event. **/
	public final EventType	type;

	/** Event Types **/
	public enum EventType
	{
		/**
		 * Thrown when the ring is (re-)joined. newFinger will hold the new
		 * successor.
		 */
		JOIN,
		/**
		 * Thrown when the ring is parted. oldFinger will hold the old
		 * successor.
		 */
		PART,
		/**
		 * Thrown when predecessor changes. oldFinger and newFinger will be set
		 * to the old/new predecessor.
		 */
		PREDECESSOR_CHANGE,
		/**
		 * Thrown when successor changes. oldFinger and newFinger will be set
		 * to the old/new successor.
		 */
		SUCCESSOR_CHANGE,
		/**
		 * Thrown when a fingertable entry changes. oldFinger and newFigner and
		 * indexes will hold the old/new finger entry.
		 * Only thrown if index != 0, in this case SUCCESSOR_CHANGE is used.
		 */
		FINGERTABLE_CHANGE
	}

	/**
	 * IID of the RingNode Component throwing the event.
	 */
	public final IID		myNodeId;

	/**
	 * Index of the finger entry that has changed.
	 */
	public final int		fingerIndex;

	/**
	 * Old finger entry before the change.
	 */
	public final IFinger	oldFinger;

	/**
	 * New finger entry after the change.
	 */
	public final IFinger	newFinger;

	/**
	 * Constructor.
	 * @param myNodeId
	 * @param type
	 * @param fingerIndex
	 * @param oldFinger
	 * @param newFinger
	 */
	public RingNodeEvent(IID myNodeId, EventType type, int fingerIndex, IFinger oldFinger, IFinger newFinger)
	{
		super();
		this.type = type;
		this.myNodeId = myNodeId;
		this.fingerIndex = fingerIndex;
		this.oldFinger = oldFinger;
		this.newFinger = newFinger;
	}

	/**
	 * Create a new RingNodeEvent with type JOIN.
	 * @param myNodeId own node id
	 * @param newFinger new finger after the change.
	 * @return RingNodeEvent
	 */
	public static RingNodeEvent join(IID myNodeId, IFinger newFinger)
	{
		return new RingNodeEvent(myNodeId, EventType.JOIN, 0, null, newFinger);
	}

	/**
	 * Create a new RingNodeEvent with type PART.
	 * @param myNodeId own node id
	 * @param oldFinger old finger.
	 * @return RingNodeEvent
	 */
	public static RingNodeEvent part(IID myNodeId, IFinger oldFinger)
	{
		return new RingNodeEvent(myNodeId, EventType.PART, 0, oldFinger, null);
	}

	/**
	 * Create a new RingNodeEvent with type FINGERTABLE_CHANGE.	 
	 * @param myNodeId own node id
	 * @param oldFinger old successor
	 * @param newFinger new successor 
	 * @return RingNodeEvent
	 */
	public static RingNodeEvent successorChange(IID myNodeId, IFinger oldFinger, IFinger newFinger)
	{
		return new RingNodeEvent(myNodeId, EventType.SUCCESSOR_CHANGE, 0, oldFinger, newFinger);
	}

	/**
	 * Create a new RingNodeEvent with type PREDECESSOR_CHANGE.	 
	 * @param myNodeId own node id
	 * @param oldFinger old predecessor
	 * @param newFinger new predecessor 
	 * @return RingNodeEvent
	 */
	public static RingNodeEvent predecessorChange(IID myNodeId, IFinger oldFinger, IFinger newFinger)
	{
		return new RingNodeEvent(myNodeId, EventType.PREDECESSOR_CHANGE, -1, oldFinger, newFinger);
	}

	/**
	 * Create a new RingNodeEvent with type FINGERTABLE_CHANGE.	 
	 * @param myNodeId own node id
	 * @param fingerIndex index of the changed finger
	 * @param oldFinger old finger
	 * @param newFinger new finger 
	 * @return RingNodeEvent
	 */
	public static RingNodeEvent fingerChange(IID myNodeId, int fingerIndex, IFinger oldFinger, IFinger newFinger)
	{
		return new RingNodeEvent(myNodeId, EventType.FINGERTABLE_CHANGE, fingerIndex, oldFinger, newFinger);
	}

	@Override
	public String toString()
	{
		return "RingNodeEvent [type=" + type + ", myNodeId=" + myNodeId + ", fingerIndex=" + fingerIndex + ", oldFinger=" + oldFinger + ", newFinger=" + newFinger + "]";
	}

}
