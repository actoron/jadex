package org.activecomponents.udp;

public class STunables
{
	/** Protocol version. */
	public static final int PROTOCOL_VERSION = 0;
	
	/** Timeout value for missing packets. */
	protected static final int MISSING_PACKET_TIMEOUT = 30000;
	
	/** Maximum size for message parts */
	protected static final int MAX_MSG_PART_SIZE = 16000;
	
	/** Minimum assumed latency in ms. */
	public static final int MIN_LATENCY = 10;
	
	/** Initial assumed latency in ms. */
	public static final int INITIAL_LATENCY = 100;
	
	/** Factor defining when an unconfirmed packet is resend in terms of measured roundtrip latency. */
	public static final double MIN_LATENCY_RESEND_DELAY_FACTOR = 2.0;
	
	/** Factor defining when an unconfirmed packet is resend in terms of measured roundtrip latency. */
	public static final double RND_LATENCY_RESEND_DELAY_FACTOR = 2.0;
	
	/** Disconnect verification packet length in bytes */
	public static final int FIN_VERIFICATION_LENGTH = 64;
	
	/** SYN tries before giving up. */
	public static final int SYN_RESEND_TRIES = 10;
	
	/** Delay between SYN resends */
	public static final int SYN_RESEND_DELAY = 2000;
	
	/** Delay waiting for FINACK confirmation. */
	public static final int FINACK_RESEND_DELAY = 200;
	
	/** Number of packets received waiting for FINACK confirmation nad how often to send them. */
	public static final int FINACK_MAX_PACKETS = 10;
	
	/** Interval between keepalive packets */
	public static final int KEEPALIVE_INTERVAL = 2000;
	
	/** Timeout for Keepalive packets. */
	public static final int KEEPALIVE_TIMEOUT = 10000;
	
	/** Maximum number of bytes before renewing the communication key. */
	public static final long MAX_KEY_LIFETIME = 256*1024*1024;
	
	/** Old keys retained after key renewal to catch stray packets. */
	public static final byte MAX_KEY_RETENTION = 1;
}