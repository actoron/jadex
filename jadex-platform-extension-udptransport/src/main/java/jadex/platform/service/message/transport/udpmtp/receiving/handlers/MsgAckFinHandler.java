package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.ITxTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  Handler for message acknowledgment and fin packets.
 *
 */
public class MsgAckFinHandler implements IPacketHandler
{
	/** Incoming Message pool. */
	protected Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** Queue for scheduled transmissions */
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/** Currently used message IDs. */
	protected Set<Integer> usedids;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param incomingsendermessages Incoming Messages pool.
	 *  @param inflightmessages The messages in flight.
	 *  @param txqueue Queue for scheduled transmissions.
	 *  @param usedids Currently used message IDs.
	 */
	public MsgAckFinHandler(Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages,
							Map<Integer, TxMessage> inflightmessages,
							PriorityBlockingQueue<ITxTask> txqueue,
							Set<Integer> usedids)
	{
		this.incomingmessages = incomingmessages;
		this.inflightmessages = inflightmessages;
		this.txqueue = txqueue;
		this.usedids = usedids;
	}
	
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype)
	{
		return SPacketDefs.MSG_ACK == packettype || SPacketDefs.MSG_FIN == packettype;
	}
	
	/**
	 *  Returns if the handler is done and should be removed.
	 *  
	 *  @return True, if the handler is done.
	 */
	public boolean isDone()
	{
		return false;
	}
	
	/**
	 *  Handles the packet
	 *  
	 *  @param sender The sender of the packet.
	 *  @param packettype The packet type.
	 *  @param packet The raw packet.
	 */
	public void handlePacket(InetSocketAddress sender, byte packettype, byte[] packet)
	{
		if (packet.length == 5)
		{
			if (SPacketDefs.MSG_ACK == packettype)
			{
				
					int msgid = SCodingUtil.intFromByteArray(packet, 1);
					inflightmessages.remove(msgid);
					//TODO: Delay if re-sent?
					usedids.remove(msgid);
					txqueue.put(TxPacket.createGenericMsgIdPacket(SPacketDefs.MSG_FIN, sender, msgid));
			}
			else if (SPacketDefs.MSG_FIN == packettype)
			{
				short msgid = SCodingUtil.shortFromByteArray(packet, 1);
				Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
				if (incomingsendermessages != null)
				{
					incomingsendermessages.remove(msgid);
				}
			}
		}
		else
		{
			PacketDispatcher.packetSanityCheckFailed();
		}
	}
}
