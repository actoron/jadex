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
	//-------- constants ----------
	
	/** The message type for streams. */
	public static final byte MESSAGE_TYPE_STREAM = 99;

	/** The minimal lease time. */
	public static final long MIN_LEASETIME = 5000;
	
	/** Constants for message types. */
	
	/** Init a connection. */
	public static final String INIT = "INIT";
	/** Acknowledge init. */
	public static final String ACKINIT = "ACKINIT";
	/** Send data message. */
	public static final String DATA = "DATA";
	/** Acknowledge data message. */
	public static final String ACKDATA = "ACKDATA"; 
	/** Close the connection. */
	public static final String CLOSE = "CLOSE";
	/** Acknowledge the close message. */
	public static final String ACKCLOSE = "ACKCLOSE"; 
	/** Close request (from participant which cannot close itself). */
	public static final String CLOSEREQ = "CLOSEREQ";
	/** Acknowledge the close request. */ 
	public static final String ACKCLOSEREQ = "ACKCLOSEREQ"; 
	/** The alive message. */
	public static final String ALIVE = "ALIVE";
	
	/** Create virtual output connection - from initiator. */
	public static final byte INIT_OUTPUT_INITIATOR = 1; 
	/** Ack the init - from initiator. */
	public static final byte ACKINIT_OUTPUT_PARTICIPANT = 2; 
	/** Send data - from initiator. */
	public static final byte DATA_OUTPUT_INITIATOR = 3;
	/** Ack data/close - from participant .*/
	public static final byte ACKDATA_OUTPUT_PARTICIPANT = 4;
	/** Request close connection - from participant. */
	public static final byte CLOSEREQ_OUTPUT_PARTICIPANT = 5;
	/** Ack for close request - from initiator .*/
	public static final byte ACKCLOSEREQ_OUTPUT_INITIATOR = 6;
	/** Close connection - from initiator. */
	public static final byte CLOSE_OUTPUT_INITIATOR = 7;
	/** Ack data/close - from participant .*/
	public static final byte ACKCLOSE_OUTPUT_PARTICIPANT = 8;

	
	/** Create virtual input connection - from initiator. */ 
	public static final byte INIT_INPUT_INITIATOR = 11;
	/** Ack the init - from participant. */
	public static final byte ACKINIT_INPUT_PARTICIPANT = 12; 
	/** Send data - from participant. */
	public static final byte DATA_INPUT_PARTICIPANT = 13;
	/** Ack data - from participant .*/
	public static final byte ACKDATA_INPUT_INITIATOR = 14;
	/** Close request connection - from initiator. */
	public static final byte CLOSEREQ_INPUT_INITIATOR = 15;
	/** Ack for close request - from initiator .*/
	public static final byte ACKCLOSEREQ_INPUT_PARTICIPANT = 16;
	/** Close connection - from participant. */
	public static final byte CLOSE_INPUT_PARTICIPANT = 17;
	/** Ack for close - from initiator .*/
	public static final byte ACKCLOSE_INPUT_INITIATOR = 18;

	
	/** Alive message - from initiator. */ 
	public static final byte ALIVE_INITIATOR = 20;
	/** Alive message - from participant. */ 
	public static final byte ALIVE_PARTICIPANT = 21;

	/** String type, boolean input, boolean initiator. */
	public static final Map<Tuple, Byte> MESSAGETYPES;
	
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	static
	{
		MESSAGETYPES = new HashMap<Tuple, Byte>();
		
		MESSAGETYPES.put(new Tuple(INIT, false, true), new Byte(INIT_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKINIT, false, false), new Byte(ACKINIT_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(DATA, false, true), new Byte(DATA_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKDATA, false, false), new Byte(ACKDATA_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(CLOSE, false, true), new Byte(CLOSE_OUTPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKCLOSE, false, false), new Byte(ACKCLOSE_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(CLOSEREQ, false, false), new Byte(CLOSEREQ_OUTPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ACKCLOSEREQ, false, true), new Byte(ACKCLOSEREQ_OUTPUT_INITIATOR));

		MESSAGETYPES.put(new Tuple(INIT, true, true), new Byte(INIT_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKINIT, true, false), new Byte(ACKINIT_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(DATA, true, false), new Byte(DATA_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ACKDATA, true, true), new Byte(ACKDATA_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSE, true, false), new Byte(CLOSE_INPUT_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ACKCLOSE, true, true), new Byte(ACKCLOSE_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(CLOSEREQ, true, true), new Byte(CLOSEREQ_INPUT_INITIATOR));
		MESSAGETYPES.put(new Tuple(ACKCLOSEREQ, true, false), new Byte(ACKCLOSEREQ_INPUT_PARTICIPANT));

		MESSAGETYPES.put(new Tuple(ALIVE, true, true), new Byte(ALIVE_INITIATOR));
		MESSAGETYPES.put(new Tuple(ALIVE, false, true), new Byte(ALIVE_INITIATOR));
		MESSAGETYPES.put(new Tuple(ALIVE, true, false), new Byte(ALIVE_PARTICIPANT));
		MESSAGETYPES.put(new Tuple(ALIVE, false, false), new Byte(ALIVE_PARTICIPANT));
	}
	
	//-------- attributes --------

	/** The binary message part. */
	protected Object message;
		
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
		this.type = type;
		this.message = message;
		this.streamid = streamid;
		this.seqnumber = seqnumber;
	}
	
	/**
	 *  Create a shallow copy.
	 */
	public StreamSendTask(StreamSendTask task)
	{
		super(task);
		
		this.type = task.type;
		this.message = task.message;
		this.streamid = task.streamid;
		this.seqnumber = task.seqnumber;
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
	 *  Get the message type.
	 *  @param type The type.
	 *  @param input Flag if in input connection.
	 *  @param initiator Flag if is initiator side.
	 */
	public static byte getMessageType(String type, boolean input, boolean initiator)
	{
		try
		{
			return MESSAGETYPES.get(new Tuple(type, input, initiator));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
