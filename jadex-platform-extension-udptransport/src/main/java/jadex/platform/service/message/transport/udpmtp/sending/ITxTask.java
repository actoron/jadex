package jadex.platform.service.message.transport.udpmtp.sending;

import java.net.InetSocketAddress;

/**
 *  Interface for a scheduled transmission.
 *
 */
public interface ITxTask
{
	/**
	 *  Gets the resolved receiver.
	 *
	 *  @return The resolved receiver.
	 */
	public InetSocketAddress getResolvedReceiver();
	
	/**
	 *  Gets all packets in this task.
	 *  
	 *  @return All packets in this task.
	 */
	public byte[][] getPackets();
	
	/**
	 *  Returns the packet IDs of all the packets
	 *  that should be transmitted.
	 *  Returning null requests transmitting all packets.
	 *  
	 *  @return The IDs of packets that should be transmitted, null for all packets.
	 */
	public short[] getTxPacketIds();
	
	/**
	 *  Gets the priority of the transmission.
	 *
	 *  @return The priority.
	 */
	public int getPriority();
	
	/**
	 *  Notification about a transmission failure.
	 *  
	 *  @param reason The reason for the failure.
	 */
	public void transmissionFailed(String reason);
}
