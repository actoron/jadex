package jadex.platform.service.message.transport.udpmtp;

import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Information about a peer.
 *
 */
public class PeerInfo
{
	/** Unknown status. */
	public static final int STATE_UNKNOWN = 0;
	
	/** Status okay, peer has responded to probe. */
	public static final int STATE_OK = 1;
	
	/** Lost contact with peer. */
	public static final int STATE_LOST = 2;
	
	/** The peer's address. */
	protected InetSocketAddress address;
	
	/** The peer's state. */
	protected volatile int state;
	
	/** Probe retries in emergency mode. */
	protected int proberetries;
	
	/** The last time the peer was probed. */
	protected volatile long lastprobe;
	
	/** The round-trip average */
	protected volatile long ping;
	
	/** Flag indicating the that a message is being re-send to this peer. */
	protected volatile boolean resending;
	
	/** Current quota of send-able bytes. */
	protected int sendbytequota;
	
	/** Tasks awaiting state change. */
	protected Queue<Runnable> statewaiters;
	
	/** Messaging awaiting quota. */
	protected LinkedBlockingQueue<TxMessage> messagequeue;
	
	/** Received unconfirmed bytes from this peer. */
	protected AtomicInteger receivedbytes;
	
	/** The flow controller. */
	//protected PeerFlowController flowcontroller;
	
	/**
	 *  Creates a new peer info.
	 *  @param address Address of the peer.
	 */
	public PeerInfo(InetSocketAddress address, TimedTaskDispatcher timedtaskdispatcher)
	{
		this.address = address;
		//this.sendbytequota = STunables.START_SENDABLE_BYTES;
		this.state = STATE_UNKNOWN;
		this.proberetries = 0;
		this.lastprobe = Long.MIN_VALUE;
		statewaiters = new LinkedBlockingQueue<Runnable>();
		this.messagequeue = new LinkedBlockingQueue<TxMessage>();
		//this.flowcontroller = new PeerFlowController(this, timedtaskdispatcher);
		this.ping = STunables.INITIAL_ROUNDTRIP;
		this.resending = false;
		this.receivedbytes = new AtomicInteger();
	}
	
	/**
	 *  Gets the address.
	 *
	 *  @return The address.
	 */
	public InetSocketAddress getAddress()
	{
		return address;
	}



	/**
	 *  Gets the last probe time.
	 *
	 *  @return The last probe time.
	 */
	public long getLastProbe()
	{
		return lastprobe;
	}

	/**
	 *  Sets the last probe time.
	 *
	 *  @param lastprobe The last probe time.
	 */
	public void setLastProbe(long lastprobe)
	{
//		System.out.println("Setting last probe for " + this + " to: " + lastprobe);
		this.lastprobe = lastprobe;
	}

	/**
	 *  Gets the state.
	 *
	 *  @return The state.
	 */
	public int getState()
	{
		return state;
	}

	/**
	 *  Sets the state.
	 *
	 *  @param status The state.
	 */
	public void setState(int state)
	{
		this.state = state;
//		System.out.println("Setting state for peer " + address + " to: " + state + ".");
	}
	
	

	/**
	 *  Gets the probe retries.
	 *
	 *  @return The probe retries.
	 */
	public int getProbeRetries()
	{
		return proberetries;
	}

	/**
	 *  Sets the probe retries.
	 *
	 *  @param proberetries The probe retries.
	 */
	public void setProbeRetries(int proberetries)
	{
		this.proberetries = proberetries;
	}
	
	/**
	 *  Increases the send byte quota.
	 *
	 *  @param sendbytequotainc The send byte quota increase.
	 */
	public synchronized void increaseSendByteQuota(int sendbytequotainc)
	{
		//this.sendbytequota = Math.min(sendbytequota + sendbytequotainc, STunables.START_SENDABLE_BYTES);
	}

	/**
	 *  Reduces the send byte quota.
	 *
	 *  @param sendbytequotadec The send byte quota decrease.
	 *  @return The new send byte quota.
	 */
	public synchronized int decreaseSendByteQuota(int sendbytequotadec)
	{
		sendbytequota -= sendbytequotadec;
		//System.out.println("Quota: " + sendbytequota);
		return sendbytequota;
	}
	
	/**
	 *  Gets the flow controller.
	 *
	 *  @return The flow controller.
	 */
//	public PeerFlowController getFlowController()
//	{
//		return flowcontroller;
//	}

	/**
	 *  Sets the flow controller.
	 *
	 *  @param flowcontroller The flow controller.
	 */
//	public void setFlowController(PeerFlowController flowcontroller)
//	{
//		this.flowcontroller = flowcontroller;
//	}
	
	/**
	 *  Gets the ping (average).
	 *
	 *  @return The ping.
	 */
	public long getPing()
	{
		return ping;
	}

	/**
	 *  Sets the ping (average).
	 *
	 *  @param ping The ping.
	 */
	public void newPing(long ping)
	{
		long calcping = (long) ((ping * STunables.ROUNDTRIP_WEIGHT) + (this.ping * STunables.INV_ROUNDTRIP_WEIGHT));
		calcping = Math.max(calcping, STunables.MIN_ROUNDTRIP);
		this.ping = calcping;
//		System.out.println("New avg ping: " + this.ping);
	}
	
	
	
	/**
	 *  Gets the flag indicating the that a message is being re-send to this peer.
	 *
	 *  @return The re-sending state.
	 */
	public boolean isResending()
	{
		return resending;
	}

	/**
	 *  Sets the flag indicating the that a message is being re-send to this peer.
	 *
	 *  @param resending The re-sending state.
	 */
	public void setResending(boolean resending)
	{
		this.resending = resending;
	}

	/**
	 *  Gets the received bytes.
	 *
	 *  @return The received bytes.
	 */
	public AtomicInteger getReceivedBytes()
	{
		return receivedbytes;
	}

	/**
	 *  Gets the state waiters.
	 *
	 *  @return The state waiters.
	 */
	public Queue<Runnable> getStateWaiters()
	{
		return statewaiters;
	}

	/**
	 *  Gets the message queue.
	 *
	 *  @return The message queue.
	 */
	public LinkedBlockingQueue<TxMessage> getMessageQueue()
	{
		return messagequeue;
	}
	
	/**
     * Returns a string representation of the object.
     *
     * @return  a string representation of the object.
     */
	public String toString()
	{
		return "Peer " + address.toString();
	}
}
