package jadex.platform.service.message.transport.udpmtp;


/**
 *  UDP packet IDs.
 *
 */
public class SPacketDefs
{
	/** Definitions for small packet ID message packets. */
	public static class S_MSG
	{
		/** Message ID Definition. */
		public static final byte PACKET_TYPE_ID = 32;
		
		/** Header size. */
		public static final int HEADER_SIZE = 11;
		
		/** Offset for message ID. */
		public static final int MSG_ID_OFFSET = 1;
		
		/** Offset for message size. */
		public static final int MSG_SIZE_OFFSET = 5;
		
		/** Offset for packet number. */
		public static final int PACKET_NUMBER_OFFSET = 9;
		
		/** Offset for total packets count. */
		public static final int TOTAL_PACKETS_OFFSET = 10;
		
		/** Maximum number of packets for this message type. */
		public static final int MAX_PACKETS = 256;
	}
	
	/** Definitions for large packet ID message packets. */
	public static class L_MSG
	{
		/** Message ID Definition. */
		public static final byte PACKET_TYPE_ID = 34;
		
		/** Header size. */
		public static final int HEADER_SIZE = 13;
		
		/** Offset for message ID. */
		public static final int MSG_ID_OFFSET = 1;
		
		/** Offset for message size. */
		public static final int MSG_SIZE_OFFSET = 5;
		
		/** Offset for packet number. */
		public static final int PACKET_NUMBER_OFFSET = 9;
		
		/** Offset for total packets count. */
		public static final int TOTAL_PACKETS_OFFSET = 11;
		
		/** Maximum number of packets for this message type. */
		public static final int MAX_PACKETS = 30000;
	}
	
	/** Size of the checksum */
	public static final int CHECKSUM_SIZE = 4;
	
	// Control Packet IDs
	
	/** Echo request. */
	public static final byte ECHO_REQUEST = 0;
	
	/** Echo reply. */
	public static final byte ECHO_REPLY   = 1;
	
	/** Probe request. */
	public static final byte PROBE = 2;
	
	/** Probe acknowledgment. */
	public static final byte PROBE_ACK   = 3;
	
	/** Probe finished. */
	public static final byte PROBE_FIN   = 4;
	
	/** Message acknowledged. */
	public static final byte MSG_ACK = 16;
	
	/** Message that arrived is garbage. */
	public static final byte MSG_GARBAGE = 17;
	
	/** Message finished. */
	public static final byte MSG_FIN = 18;
	
	/** Message confirmation packet. */
	public static final byte MSG_CONFIRM = 19;
	
	/** Message confirm error/denied. */
	public static final byte MSG_CONFIRM_ERROR = 20;
}
