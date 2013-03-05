package jadex.platform.service.message.transport.udpmtp;

/**
 *  Adjustable default / performance values for the UDP transport.
 */
public class STunables
{
	/** Random port seek cycles before starting systematic search.  */
	public final static int RANDOM_PORT_CYCLES = 20;
	
	/** Address parse cache size. */
	public final static int PARSE_CACHE_SIZE = 500;
	
	/** Enables the tiny packet transfer mode. */
	public static final boolean ENABLE_TINY_MODE = false;
	
	/** Enables the small packet transfer mode. */
	public static final boolean ENABLE_SMALL_MODE = false;
	
	/** Probe interval delay. */
	public static final int PROBE_INTERVAL_DELAY = 1000;
	
	/** Probe interval delay when there's no response. */
	public static final int PROBE_INTERVAL_REDUCED_DELAY = 100;
	
	/** Ban time after no probe response. */
	public static final int PROBE_RESPONSE_BAN_TIME = 10 * 60000;
	
	/** Probe enters reduced delay mode after exceeding this number of retries. */
	public static final int PROBE_NORMAL_RETRIES = 5;
	
	/** Probe retry limit. */
	public static final int PROBE_RETRIES = 30;
	
	/** Initial assumed round-trip time. */
	public static final long INITIAL_ROUNDTRIP =  100;
	
	/** Historic round-trip weight. */
	public static final double ROUNDTRIP_WEIGHT = 0.9;
	
	/** Inverse historic round-trip weight. */
	public static final double INV_ROUNDTRIP_WEIGHT = 1.0 - ROUNDTRIP_WEIGHT;
	
	/** The default acknowledgment delay if no ping is available. */
	public static final long ACK_DELAY = 2000;
	
	/** Decay time for receiving messages. */
	public static final long RX_MESSAGE_DECAY = 1200000;
	
	/** The fin delay. */
//	public static final long FIN_DELAY = ACK_DELAY >> 1;
	
	/** The re-send delay factor. */
	public static final double RESEND_DELAY_FACTOR = 1.3;
	
	/** Send-able bytes at start up. */
//	public static final int START_SENDABLE_BYTES = 16384;
	public static final int MIN_SENDABLE_BYTES = 16384;
//	public static final int MIN_SENDABLE_BYTES = 16;
//	public static final int INITIAL_SENDABLE_BYTES = 131072;
	
	public static final int MAX_SENDABLE_BYTES = 16777216;
//	public static final int MAX_SENDABLE_BYTES = 1048576;
//	public static final int MAX_SENDABLE_BYTES = 262144;
//	public static final int MAX_SENDABLE_BYTES = 131072;
//	public static final int MAX_SENDABLE_BYTES = 16384;
	
	/** Socket buffer size. */
//	public static final int BUFFER_SIZE = 1048576;
//	public static final int BUFFER_SIZE = 262144;
	public static final int BUFFER_SIZE = 65536;
//	public static final int BUFFER_SIZE = 16384;
	
	/** The confirmation threshold in bytes. */
	public static final int CONFIRMATION_THRESHOLD = MIN_SENDABLE_BYTES / 4;
	
	/** Minimum round-trip time. */
	public static final int MIN_ROUNDTRIP = 200;
	
	/** 
	 *  This number multiplied by the number of packets in the
	 *  message defines the re-send upper limit
	 *  (message-size-equivalent re-sends).
	 */
	public static final int MAX_RESENDS = 100;
	
	/* Default priorities, lower number is higher priority. */
	
	/** Default priority of control packets. / */
	public static final int CONTROL_PACKETS_DEFAULT_PRIORITY = -1;
	
	/** Default priority of probe packets. / */
	public static final int PROBE_PACKETS_DEFAULT_PRIORITY = -5;
	
	/** Default priority of tiny (0B - 128KiB) messages. / */
	public static final int RESEND_MESSAGES_PRIORITY = 5;
	
	/** Default priority of tiny (0B - 128KiB) messages. / */
	public static final int TINY_MESSAGES_DEFAULT_PRIORITY = 10;
	
	/** Default priority of small (128KiB - 256KiB) messages. / */
	public static final int SMALL_MESSAGES_DEFAULT_PRIORITY = 20;
	
	/** Default priority of medium (256KiB - 2MiB) messages. / */
	public static final int MEDIUM_MESSAGES_DEFAULT_PRIORITY = 30;
	
	/** Default priority of large (2MiB - ~250MiB) messages. / */
	public static final int LARGE_MESSAGES_DEFAULT_PRIORITY = 40;
}
