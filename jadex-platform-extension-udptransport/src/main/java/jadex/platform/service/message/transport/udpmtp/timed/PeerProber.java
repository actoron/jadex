package jadex.platform.service.message.transport.udpmtp.timed;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTask;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;
import jadex.platform.service.message.transport.udpmtp.sending.SendingThreadTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  Task for probing peers.
 *
 */
public class PeerProber extends TimedTask
{
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** Information about the peer */
	protected PeerInfo peerinfo;
	
	/** The dispatcher for re-scheduling */
	protected TimedTaskDispatcher dispatcher;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/**
	 *  Creates a peer prober.
	 * 
	 *  @param info The peer info. 
	 *  @param dispatcher The timed task dispatcher.
	 */
	public PeerProber(Map<InetSocketAddress, PeerInfo> peerinfos, PeerInfo info, TimedTaskDispatcher dispatcher, PriorityBlockingQueue<TxPacket> packetqueue, Map<Integer, TxMessage> inflightmessages)
	{
		super(info.getAddress(), Long.MIN_VALUE);
		this.peerinfos = peerinfos;
		this.peerinfo = info;
		this.dispatcher = dispatcher;
		this.packetqueue = packetqueue;
		this.inflightmessages = inflightmessages;
	}
	
	/**
	 *  Runs the prober.
	 */
	public void run()
	{
		long currenttime = System.currentTimeMillis();
		TxPacket probe = TxPacket.createGenericTimestampPacket(SPacketDefs.PROBE, peerinfo.getAddress(), currenttime);
		probe.setPriority(STunables.PROBE_PACKETS_DEFAULT_PRIORITY);
		SendingThreadTask.queuePacket(packetqueue, probe);
//		System.out.println("Probe sent, time: " + currenttime + ".");
//		System.out.println("Last Probe of " + peerinfo + " was: " + peerinfo.getLastProbe());
		
		if (peerinfo.getLastProbe() > Long.MIN_VALUE && peerinfo.getLastProbe() < currenttime - STunables.PROBE_INTERVAL_DELAY)
		{
			// No recent response, go to emergency probes mode.
			int retries = peerinfo.getProbeRetries();
			if (retries > STunables.PROBE_RETRIES)
			{
				peerinfo.setState(PeerInfo.STATE_LOST);
				System.out.println("Banning: " + peerinfo.getAddress());
				dispatcher.cancel(peerinfo.getAddress());
				synchronized (inflightmessages)
				{
					TxMessage[] msgs = inflightmessages.values().toArray(new TxMessage[inflightmessages.size()]);
					for (TxMessage msg : msgs)
					{
						if (msg.getResolvedReceiver().equals(peerinfo.getAddress()))
						{
							inflightmessages.remove(msg.getMsgId());
							msg.transmissionFailed("Lost connection to peer: " + peerinfo.getAddress());
						}
					}
				}
				TimedTask unbantask = new TimedTask(currenttime + STunables.PROBE_RESPONSE_BAN_TIME)
				{
					public void run()
					{
//						System.out.println("Performing unban. ");
						peerinfos.remove(peerinfo.getAddress());
					}
				};
				dispatcher.scheduleTask(unbantask);
				return;
			}
			
			peerinfo.setProbeRetries(retries + 1);
			if (retries > STunables.PROBE_NORMAL_RETRIES)
			{
				executiontime = currenttime + STunables.PROBE_INTERVAL_REDUCED_DELAY;
			}
		}
		else
		{
			peerinfo.setProbeRetries(0);
			executiontime = currenttime + STunables.PROBE_INTERVAL_DELAY;
		}
		
		dispatcher.scheduleTask(this);
	}
}
