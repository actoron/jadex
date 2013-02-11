package jadex.platform.service.message.transport.udpmtp.timed;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.SPacketDefs;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTask;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;
import jadex.platform.service.message.transport.udpmtp.sending.ITxTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

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
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/**
	 *  Creates a peer prober.
	 * 
	 *  @param info The peer info. 
	 *  @param dispatcher The timed task dispatcher.
	 */
	public PeerProber(Map<InetSocketAddress, PeerInfo> peerinfos, PeerInfo info, TimedTaskDispatcher dispatcher, PriorityBlockingQueue<ITxTask> txqueue)
	{
		super(info.getAddress(), Long.MIN_VALUE);
		this.peerinfos = peerinfos;
		this.peerinfo = info;
		this.dispatcher = dispatcher;
		this.txqueue = txqueue;
	}
	
	/**
	 *  Runs the prober.
	 */
	public void run()
	{
		long currenttime = System.currentTimeMillis();
		TxPacket probe = TxPacket.createGenericTimestampPacket(SPacketDefs.PROBE, peerinfo.getAddress(), currenttime);
		txqueue.put(probe);
		System.out.println("Probe sent, time: " + currenttime + ".");
		System.out.println("Last Probe of " + peerinfo + " was: " + peerinfo.getLastProbe());
		
		if (peerinfo.getLastProbe() > Long.MIN_VALUE && peerinfo.getLastProbe() < currenttime - STunables.PROBE_INTERVAL_DELAY)
		{
			// No recent response, go to emergency probes mode.
			int retries = peerinfo.getProbeRetries();
			if (retries > STunables.PROBE_RETRIES)
			{
				peerinfo.setState(PeerInfo.STATE_LOST);
				TimedTask unbantask = new TimedTask(currenttime + STunables.PROBE_RESPONSE_BAN_TIME)
				{
					public void run()
					{
						System.out.println("Performing unban. ");
						peerinfos.remove(peerinfo.getAddress());
					}
				};
				dispatcher.scheduleTask(unbantask);
				return;
			}
			
			peerinfo.setProbeRetries(retries + 1);
			executiontime = currenttime + STunables.PROBE_INTERVAL_REDUCED_DELAY;
		}
		else
		{
			executiontime = currenttime + STunables.PROBE_INTERVAL_DELAY;
		}
		
		dispatcher.scheduleTask(this);
	}
}
