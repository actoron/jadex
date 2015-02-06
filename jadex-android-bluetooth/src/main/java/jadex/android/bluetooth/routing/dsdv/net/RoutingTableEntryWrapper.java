package jadex.android.bluetooth.routing.dsdv.net;

import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.routing.dsdv.info.CurrentInfo;

/**
 * Represents one entry in the Routing table,
 * wraps the MessageProto Builder
 * 
 * @author 8kalinow
 */
public class RoutingTableEntryWrapper {

	private MessageProtos.RoutingTableEntry.Builder entryBuilder;

	/**
	 * Class constructor
	 */
	public RoutingTableEntryWrapper() {
		/**
		 * log when route is created for cleanup
		 */
		entryBuilder = jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry
				.newBuilder();
		entryBuilder.setRouteCreationTime(System.currentTimeMillis());
	}
	
	/**
	 * Constructor
	 * @param rte
	 */
	public RoutingTableEntryWrapper(RoutingTableEntry rte) {
		/**
		 * log when route is created for cleanup
		 */
		entryBuilder = rte.toBuilder();
//		entryBuilder = jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry
//				.newBuilder(rte);
	}

	/**
	 * Class constructor
	 * 
	 * @param dest
	 *            Address of the destination device
	 * @param nextHop2
	 *            how to get there
	 * @param hops
	 *            the number of hops required to get there
	 * @param seqNum
	 *            The sequence numbers are even if a link is present; else, an
	 *            odd number is used.
	 */
	public RoutingTableEntryWrapper(String dest, String nextHop2, int hops, int seqNum) {
		entryBuilder = jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry
				.newBuilder();
		entryBuilder.setDestination(dest);
		entryBuilder.setNextHop(nextHop2);
		entryBuilder.setNumHops(hops);
		entryBuilder.setSeqNum(seqNum);
		/**
		 * log when route is created for cleanup
		 */
		entryBuilder.setRouteCreationTime(System.currentTimeMillis());
	}

	/**
	 * Check whether a route has changed
	 * 
	 * @return whether a route has changed
	 */
	public boolean isRouteChanged() {
		return entryBuilder.getRouteChanged();
	}

	/**
	 * 
	 * @param value
	 *            has the route changed
	 */
	public void setRouteChanged(boolean value) {
		entryBuilder.setRouteChanged(value);
	}

	/**
	 * 
	 * @return the destination address
	 */
	public String getDestination() {
		return entryBuilder.getDestination();
	}

	/**
	 * 
	 * @param destination
	 *            the destination address for this routing table entry
	 */
	public void setDestination(String destination) {
		entryBuilder.setDestination(destination);
	}

	/**
	 * 
	 * @return the destinations next hop
	 */
	public String getNextHop() {
		return entryBuilder.getNextHop();
	}

	/**
	 * 
	 * @param nextHop
	 *            the destinations next hop
	 */
	public void setNextHop(String nextHop) {
		entryBuilder.setNextHop(nextHop);
	}

	/**
	 * 
	 * @return the number of hops required
	 */
	public int getNumHops() {
		return entryBuilder.getNumHops();
	}

	/**
	 * 
	 * @param numHops
	 *            sets the number of hops
	 */
	public void setNumHops(int numHops) {
		entryBuilder.setNumHops(numHops);
	}

	/**
	 * 
	 * @return the sequence number for this link
	 */
	public int getSeqNum() {
		return entryBuilder.getSeqNum();
	}

	/**
	 * 
	 * @param seqNum
	 *            sets the sequence number for this link
	 */
	public void setSeqNum(int seqNum) {
		entryBuilder.setSeqNum(seqNum);
	}

	/**
	 * Increment the sequence number
	 */
	public void increaseSeqNum() {
		entryBuilder.setSeqNum(CurrentInfo.incrementOwnSeqNum());
	}

	/**
	 * Marks a route as invalid by making it odd
	 */
	public void setRouteUnvalid() {
		if (entryBuilder.getSeqNum() % 2 == 0) // if is even num then mark as
												// invalid else do
			// nothing
			entryBuilder.setSeqNum(getSeqNum() + 1); // sets the sequence num as
														// unvalid
	}

	/**
	 * 
	 * @return the creation time for this link
	 */
	public long getRouteCreationTime() {
		return entryBuilder.getRouteCreationTime();
	}

	/**
	 * Updates the create time to NOW
	 */
	public void updateRouteCreationTime() {
		entryBuilder.setRouteCreationTime(System.currentTimeMillis()); // log
																		// when
																		// route
		// is created
		// for cleanup;
	}

	/**
	 * 
	 * @return whether this link is invalid
	 */
	public boolean isValid() {
		return entryBuilder.getSeqNum() % 2 == 0;

	}
	
	/**
	 * Builds the RoutingTableEntry
	 * @return {@link RoutingTableEntry}
	 */
	public RoutingTableEntry build() {
		return entryBuilder.build();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Dest: ").append(entryBuilder.getDestination())
				.append(" next: ").append(entryBuilder.getNextHop());
		sb.append(" Seq:").append(entryBuilder.getSeqNum()).append(" hop:")
				.append(entryBuilder.getNumHops());
		return sb.toString();
	}

}
