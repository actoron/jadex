package jadex.platform.service.message.transport.udpmtp.receiving;

import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;
import jadex.platform.service.message.transport.udpmtp.receiving.handlers.IPacketHandler;

import java.net.InetSocketAddress;
import java.util.List;

/**
 *  Dispatcher for incoming packets.
 *
 */
public class PacketDispatcher implements Runnable
{
	/** The packet sender. */
	protected InetSocketAddress sender;
	
	/** The incoming packet. */
	protected byte[] packet;
	
	/** Handlers for incoming packets. */
	protected List<IPacketHandler> packethandlers;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	public PacketDispatcher(InetSocketAddress sender, byte[] packet, List<IPacketHandler> packethandlers, TimedTaskDispatcher timedtaskdispatcher)
	{
		this.sender = sender;
		this.packet = packet;
		this.packethandlers = packethandlers;
		this.timedtaskdispatcher = timedtaskdispatcher;
	}
	
	/**
	 *  Runs the message decoding.
	 */
	public void run()
	{
//		System.out.println("Executing packet dispatch task");
		// Check if packet contains anything at all.
		if (packet.length > 0)
		{
			final byte packettype = packet[0];
			
//			System.out.println("Got packet: " + packettype);
//			
			IPacketHandler[] handlers = null;
			synchronized(packethandlers)
			{
				handlers = packethandlers.toArray(new IPacketHandler[packethandlers.size()]);
			}
			
			boolean handled = false;
			for (int i = 0; i < handlers.length; ++i)
			{
				if (handlers[i].isApplicable(packettype))
				{
					final IPacketHandler handler = handlers[i];
//					threadpool.execute(new Runnable()
//					{
//						public void run()
//						{
							handler.handlePacket(sender, packettype, packet);
							if (handler.isDone())
							{
								packethandlers.remove(handler);
							}
//						}
//					});
					
					handled = true;
				}
			}
			
			if (!handled)
			{
				unknownPacketTypeError(packettype);
			}
		}
		else
		{
			// Empty packet.
			packetSanityCheckFailed();
		}
	}
	
	public static void packetSanityCheckFailed()
	{
		System.err.println("Packet sanity failed!");
	}
	
	protected void unknownPacketTypeError(byte type)
	{
		System.err.println("Unknown Packet: " + type);
	}
}
