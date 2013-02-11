package jadex.platform.service.message.transport.udpmtp;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	/** Tasks awaiting state change. */
	protected Queue<Runnable> statewaiters;
	
	/**
	 *  Creates a new peer info.
	 *  @param address Address of the peer.
	 */
	public PeerInfo(InetSocketAddress address)
	{
		this.address = address;
		this.state = STATE_UNKNOWN;
		this.proberetries = 0;
		this.lastprobe = Long.MIN_VALUE;
		statewaiters = new LinkedBlockingQueue<Runnable>();
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
		System.out.println("Setting last probe for " + this + " to: " + lastprobe);
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
		System.out.println("Setting state for peer " + address + " to: " + state + ".");
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
	 *  Gets the state waiters.
	 *
	 *  @return The state waiters.
	 */
	public Queue<Runnable> getStateWaiters()
	{
		return statewaiters;
	}
}
