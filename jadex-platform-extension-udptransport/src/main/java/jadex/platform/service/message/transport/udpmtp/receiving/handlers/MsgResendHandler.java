package jadex.platform.service.message.transport.udpmtp.receiving.handlers;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import jadex.platform.service.message.transport.udpmtp.SCodingUtil;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.L_MSG;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs.S_MSG;
import jadex.platform.service.message.transport.udpmtp.receiving.PacketDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.RxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.ITxTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

/**
 *  Handler dealing with re-sends.
 *
 */
public class MsgResendHandler implements IPacketHandler
{
	/** Incoming Message pool. */
	protected Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** Queue for scheduled transmissions */
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/**
	 *  Creates the handler.
	 *  
	 *  @param incomingsendermessages Incoming Messages pool.
	 *  @param inflightmessages The messages in flight.
	 *  @param txqueue Queue for scheduled transmissions.
	 */
	public MsgResendHandler(Map<InetSocketAddress, Map<Integer, RxMessage>> incomingmessages,
							Map<Integer, TxMessage> inflightmessages,
							PriorityBlockingQueue<ITxTask> txqueue)
	{
		this.incomingmessages = incomingmessages;
		this.inflightmessages = inflightmessages;
		this.txqueue = txqueue;
	}
	
	/**
	 *  Returns if the handler is applicable for this packet type.
	 *  
	 *  @param packettype The packet type.
	 *  @return True, if the handler is applicable.
	 */
	public boolean isApplicable(byte packettype)
	{
		return S_MSG.RESEND_REQ_ID == packettype ||
			   L_MSG.RESEND_REQ_ID == packettype ||
			   SPacketDefs.MSG_RESEND_ERROR == packettype;
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
		if (packet.length >= 5)
		{
			if (packettype == SPacketDefs.MSG_RESEND_ERROR)
			{
				Map<Integer, RxMessage> incomingsendermessages = incomingmessages.get(sender);
				if (incomingsendermessages != null)
				{
					int msgid = SCodingUtil.intFromByteArray(packet, 1);
					incomingsendermessages.remove(msgid);
				}
			}
			else
			{
				int msgid = SCodingUtil.intFromByteArray(packet, 1);
				
				TxMessage msg = inflightmessages.get(msgid);
				if (msg != null)
				{
					if (packet.length == 5)
					{
						// Full resend
						txqueue.put(msg);
					}
					else
					{
						if (packettype == L_MSG.RESEND_REQ_ID)
						{
							if ((packet.length - 5) % 2 == 0)
							{
								short[] resendpackets = new short[(packet.length - 5) / 2];
								for (int i = 5; i < packet.length; i = i + 2)
								{
									resendpackets[i] = SCodingUtil.shortFromByteArray(packet, i);
								}
							}
							else
							{
								PacketDispatcher.packetSanityCheckFailed();
							}
						}
						else
						{
							short[] resendpackets = new short[packet.length - 5];
							for (int i = 5; i < packet.length; ++i)
							{
								resendpackets[i] = packet[i];
							}
						}
					}
				}
				else
				{
					txqueue.put(TxPacket.createGenericMsgIdPacket(SPacketDefs.MSG_RESEND_ERROR, sender, msgid));
				}
			}
		}
		else
		{
			PacketDispatcher.packetSanityCheckFailed();
		}
	}
}
