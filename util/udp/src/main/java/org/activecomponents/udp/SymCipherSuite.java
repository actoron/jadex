package org.activecomponents.udp;

import java.util.concurrent.atomic.AtomicLong;

import org.activecomponents.udp.symciphers.ISymCipher;

/** Class containing a symmetric cipher and associated security data. */
public class SymCipherSuite
{
	/** Counters for packet IDs, must be greater than Long.MIN_VALUE to allow replay identification to work. */
	protected AtomicLong packetidcounter = new AtomicLong(Long.MIN_VALUE + 1);
	
	/** Counters for message IDs. */
	protected AtomicLong messageidcounter = new AtomicLong(Long.MIN_VALUE);
	
	/** Tracker for identifying packet replays */
	protected IdReplayTracker packetidreplaytracker;
	
	/** The symmetric cipher. */
	protected ISymCipher symcipher; 
	
	public SymCipherSuite(ISymCipher symcipher)
	{
		this.symcipher = symcipher;
		packetidreplaytracker = new IdReplayTracker(packetidcounter.get(), STunables.MISSING_PACKET_TIMEOUT);
	}
	
	/**
	 *  Gets the packet ID counter.
	 *  @return The packet ID counter.
	 */
	public AtomicLong getPacketidcounter()
	{
		return packetidcounter;
	}
	
	/**
	 *  Gets the message ID counter.
	 *  @return The message ID counter.
	 */
	public AtomicLong getMessageidcounter()
	{
		return messageidcounter;
	}
	
	/**
	 *  Returns Packet ID tracker for identifying replay attacks. 
	 *  @return Packet ID tracker.
	 */
	public IdReplayTracker getPacketIdReplayTracker()
	{
		return packetidreplaytracker;
	}
	
	/**
	 *  Gets the symmetric cipher.
	 * @return The symmetric cipher.
	 */
	public ISymCipher getSymCipher()
	{
		return symcipher;
	}
}
