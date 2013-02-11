package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import java.net.InetSocketAddress;

/**
 *  Packet handler subject to a timeout.
 *
 */
public abstract class AbstractTimeoutPacketHandler implements IPacketHandler
{
	/** Finished flag. */
	protected boolean finished = false;
	
	/**
	 *  Returns if the handler is done and should be removed.
	 *  
	 *  @return True, if the handler is done.
	 */
	public synchronized boolean isDone()
	{
		return finished;
	}
	
	/**
	 *  Handles the packet. The packet is read-only.
	 *  Handlers MUST NOT modify the packet.
	 *  
	 *  @param sender The sender of the packet.
	 *  @param packettype The packet type.
	 *  @param packet The raw packet.
	 */
	public synchronized final void handlePacket(InetSocketAddress sender, byte packettype,
			byte[] packet)
	{
		if (!finished)
		{
			doHandlePacket(sender, packettype, packet);
		}
	}
	
	/**
	 *  Cancels the handler.
	 */
	public synchronized void cancel()
	{
		finished = true;
	}
	
	/**
	 *  Handles the packet for implementing classes. The packet is read-only.
	 *  Handlers MUST NOT modify the packet.
	 *  
	 *  @param sender The sender of the packet.
	 *  @param packettype The packet type.
	 *  @param packet The raw packet.
	 */
	public abstract void doHandlePacket(InetSocketAddress sender, byte packettype, byte[] packet);
}
