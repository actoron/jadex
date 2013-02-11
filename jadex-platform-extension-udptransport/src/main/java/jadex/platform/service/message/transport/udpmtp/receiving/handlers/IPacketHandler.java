package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import java.net.InetSocketAddress;

/**
 *  Handler for specific packet types.
 *
 */
public interface IPacketHandler
{
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype);
	
	/**
	 *  Returns if the handler is done and should be removed.
	 *  
	 *  @return True, if the handler is done.
	 */
	public boolean isDone();
	
	/**
	 *  Handles the packet. The packet is read-only.
	 *  Handlers MUST NOT modify the packet.
	 *  
	 *  @param sender The sender of the packet.
	 *  @param packettype The packet type.
	 *  @param packet The raw packet.
	 */
	public void handlePacket(InetSocketAddress sender, byte packettype, byte[] packet);
}
