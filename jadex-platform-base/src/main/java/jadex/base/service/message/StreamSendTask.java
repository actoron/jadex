package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;
import jadex.commons.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 *  Task to send data via streams.
 */
public class StreamSendTask extends AbstractSendTask implements ISendTask
{
	public static final byte MESSAGE_TYPE_STREAM = 99;

	public static final long LEASETIME = 5000;
	
	public static final String INIT = "INIT";
	public static final String DATA = "DATA";
	public static final String CLOSE = "CLOSE";
	public static final String ALIVE = "ALIVE";
	public static final String RESEND = "RESEND"; // request a resend
	
	/** Create virtual output connection - from initiator. */
	public static final byte INIT_OUTPUT_INITIATOR = 0; 
	/** Send data - from initiator. */
	public static final byte DATA_OUTPUT_INITIATOR = 1;
	/** Close connection - from initiator. */
	public static final byte CLOSE_OUTPUT_INITIATOR = 2;
	/** Close connection - from participant. */
	public static final byte CLOSE_OUTPUT_PARTICIPANT = 3;
	/** Close connection - from . */
	public static final byte RESEND_OUTPUT_PARTICIPANT = 3;

	
	/** Create virtual input connection - from initiator. */ 
	public static final byte INIT_INPUT_INITIATOR = 4;
	/** Send data - from participant. */
	public static final byte DATA_INPUT_PARTICIPANT = 5;
	/** Close connection - from initiator. */
	public static final byte CLOSE_INPUT_INITIATOR = 6;
	/** Close connection - from participant. */
	public static final byte CLOSE_INPUT_PARTICIPANT = 7;
	
	/** Alive message - from initiator. */ 
	public static final byte ALIVE_INITIATOR = 8;
	/** Alive message - from participant. */ 
	public static final byte ALIVE_PARTICIPANT = 9;

	/** String type, boolean input, boolean initiator. */
	public static final Map<Tuple, Byte> MESSAGETYPES;
	
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	static
	{
		MESSAGETYPES = new HashMap<Tuple, Byte>();
		
		MESSAGETYPES.put(new Tuple(INIT, false, true), new Byte(INIT_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(DATA, false, true), new Byte(DATA_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSE, false, true), new Byte(CLOSE_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSE, false, false), new Byte(CLOSE_OUTPUT_PARTICIPANT));

		MESSAGETYPES.put(new Tuple(INIT, true, true), new Byte(INIT_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(DATA, true, false), new Byte(DATA_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(CLOSE, true, true), new Byte(CLOSE_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSE, true, false), new Byte(CLOSE_INPUT_PARTICIPANT));

		MESSAGETYPES.put(new Tuple(ALIVE, true, true), new Byte(ALIVE_INITIATOR));
		MESSAGETYPES.put(new Tuple(ALIVE, false, true), new Byte(ALIVE_INITIATOR));
		MESSAGETYPES.put(new Tuple(ALIVE, true, false), new Byte(ALIVE_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ALIVE, false, false), new Byte(ALIVE_PARTICIPANT));
	}
	
	//-------- attributes --------

	/** The binary message part. */
	protected Object message;
		
//	/** The message type. */
//	protected MessageType messagetype;
	
	/** The stream id. */
	protected int streamid;
	
	/** The type of message (init, data, close). */
	protected byte type;
	
	/** The sequence number. */
	protected Integer seqnumber;
		
	//-------- constructors --------- 

	
	/**
	 *  Create a new manager send task.
	 */
	public StreamSendTask(byte type, Object message, int streamid, IComponentIdentifier[] receivers, 
		ITransport[] transports, byte[] codecids, ICodec[] codecs, Integer seqnumber)
	{
		super(receivers, transports, codecids, codecs);
		
		for(int i=0; i<receivers.length; i++)
		{
			if(receivers[i].getAddresses()==null)
				throw new IllegalArgumentException("Addresses must not null");
		}
		
		this.type = type;
		this.message = message;
		this.streamid = streamid;
		this.seqnumber = seqnumber;
	}
	
	//-------- methods used by message service --------
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Object getMessage()
	{
		byte[] prolog = getProlog();
		byte[] data = getData();
		byte[] msg = new byte[prolog.length+data.length];
		System.arraycopy(prolog, 0, msg, 0, prolog.length);
		System.arraycopy(data, 0, msg, prolog.length, data.length);
		return msg;
	}

	/**
	 *  Get the messagetype.
	 *  @return the messagetype.
	 */
	public MessageType getMessageType()
	{
		return null;
	}
	
	/**
	 *  Get the sequence number.
	 *  @return the sequence number.
	 */
	public Integer getSequenceNumber()
	{
		return seqnumber;
	}

	/**
	 *  Get the encoded message.
	 *  Saves the message to avoid multiple encoding with different transports.
	 */
	public byte[] getData()
	{
		if(data==null)
		{
			synchronized(this)
			{
				if(data==null)
				{
					Object enc_msg = message;
					for(int i=0; i<codecs.length; i++)
					{
						enc_msg	= codecs[i].encode(enc_msg, getClass().getClassLoader());
					}
					data = (byte[])enc_msg;
				}
			}
		}
		return data;
	}
	
	/**
	 *  Get the prolog bytes.
	 *  Separated from data to avoid array copies.
	 *  Message service expects messages to be delivered in the form {prolog}{data}. 
	 *  @return The prolog bytes.
	 */
	public byte[] getProlog()
	{
		if(prolog==null)
		{
			synchronized(this)
			{
				if(prolog==null)
				{
					prolog = new byte[7+codecids.length+(seqnumber==null? 0: 4)];
					prolog[0] = MESSAGE_TYPE_STREAM;
					prolog[1] = type;
					prolog[2] = (byte)codecids.length;
					System.arraycopy(codecids, 0, prolog, 3, codecids.length);
					byte[] strid = SUtil.intToBytes(streamid);
					for(int i=0; i<4; i++)
						prolog[i+3+codecids.length] = strid[i];
					if(seqnumber!=null)
					{
						byte[] seqnum = SUtil.intToBytes(seqnumber.intValue());
						for(int i=0; i<4; i++)
							prolog[i+7+codecids.length] = seqnum[i];
					}
				}
			}
		}
		return prolog;
	}
	
	/**
	 * 
	 */
	public static byte getMessageType(String type, boolean input, boolean initiator)
	{
		return MESSAGETYPES.get(new Tuple(type, input, initiator));
	}
}
