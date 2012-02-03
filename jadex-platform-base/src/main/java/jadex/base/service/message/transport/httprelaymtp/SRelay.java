package jadex.base.service.message.transport.httprelaymtp;


/**
 *  Constants for relay service.
 */
public class SRelay
{
	//-------- constants --------
	
	/** Relay address scheme. */
	public static final String	ADDRESS_SCHEME	= "relay-http://";
	
	/** Default relay address. */
	public static final String	DEFAULT_ADDRESS	= ADDRESS_SCHEME+"jadex.informatik.uni-hamburg.de/relay/";
	
	/** The default message type (followed by arbitrary message content from some sender). */
	public static final byte	MSGTYPE_DEFAULT	= 1;
	
	/** The ping message type (just the type byte and no content). */
	public static final byte	MSGTYPE_PING	= 2;
	
	/** The awareness info message type (awareness info content). */
	public static final byte	MSGTYPE_AWAINFO	= 3;
	
	/** The delay (ms) between server pings (for checking if receiving connection is still active). */
	public static final long	PING_DELAY	= 30000;
}
