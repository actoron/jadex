package jadex.platform.service.message.transport.httprelaymtp;



/**
 *  Constants and helper methods for relay server and transport.
 */
public class SRelay
{
	//-------- constants --------
	
	/** Relay address scheme. */
	public static final String[]	ADDRESS_SCHEMES	= new String[]{"relay-http://", "relay-https://"};
	
	/** The default ports corresponding to the address schemes (http=80, https=443). */
	public static final int[]	DEFAULT_PORTS	= new int[]{80, 443};
	
	/** Default relay address(es) used by the platform for finding available servers. */
	public static final String	DEFAULT_ADDRESS	= "http://www0.activecomponents.org/relay, http://www2.activecomponents.org/relay, http://relay1.activecomponents.org/, http://jadex.informatik.uni-hamburg.de/relay/";
	
	/** The default message type (followed by arbitrary message content from some sender). */
	public static final byte	MSGTYPE_DEFAULT	= 1;
	
	/** The ping message type (just the type byte and no content). */
	public static final byte	MSGTYPE_PING	= 2;
	
	/** The awareness info message type (awareness info content). */
	public static final byte	MSGTYPE_AWAINFO	= 3;
	
	/** The delay (ms) between server pings (for checking if receiving connection is still active).
	 *  30 minutes -> default session timeout of tomcat. 
	 *  Actually uses only 85% of that time. */
	public static final long	PING_DELAY	= 30*60*1000;
}
