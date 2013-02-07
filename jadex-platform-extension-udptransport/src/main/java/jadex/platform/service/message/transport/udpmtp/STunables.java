package jadex.platform.service.message.transport.udpmtp;

/**
 *  Adjustable default / performance values for the UDP transport.
 */
public class STunables
{
	/* Default priorities, lower number is higher priority. */
	
	public static final boolean ENABLE_TINY_MODE = true;
	public static final boolean ENABLE_SMALL_MODE = true;
	
	/** Default priority of tiny (0B - 128KiB) messages/ */
	public static final int TINY_MESSAGES_DEFAULT_PRIORITY = 0;
	
	/** Default priority of small (128KiB - 256KiB) messages/ */
	public static final int SMALL_MESSAGES_DEFAULT_PRIORITY = 1;
	
	/** Default priority of medium (256KiB - 2MiB) messages/ */
	public static final int MEDIUM_MESSAGES_DEFAULT_PRIORITY = 2;
	
	/** Default priority of large (2MiB - ~250MiB) messages/ */
	public static final int LARGE_MESSAGES_DEFAULT_PRIORITY = 3;
}
