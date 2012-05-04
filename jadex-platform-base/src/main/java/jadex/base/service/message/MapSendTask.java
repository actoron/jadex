package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.MessageType;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

/**
 *  The manager send task is responsible for coordinating
 *  the sending of a message to a single destination using
 *  multiple available transports.
 */
public class MapSendTask extends AbstractSendTask implements ISendTask
{
	//-------- constants --------
	
	/** Constant for string based map message. */
	public static final byte MESSAGE_TYPE_MAP = 1;
	
	//-------- attributes --------
	
	/** The message. */
	protected Map<String, Object> message;
	
	/** The message type. */
	protected MessageType messagetype;
	
	/** The classloader. */
	protected ClassLoader classloader;

	//-------- constructors --------- 

	/**
	 *  Create a new manager send task.
	 */
	public MapSendTask(Map<String, Object> message, MessageType messagetype, IComponentIdentifier[] receivers, 
		ITransport[] transports, ICodec[] codecs, ClassLoader classloader)//, SendManager manager)
	{
		super(receivers, transports, codecs);
		if(codecs==null || codecs.length==0)
			throw new IllegalArgumentException("Codecs must not null.");
		
		this.message = message;
		this.messagetype = messagetype;
		this.classloader = classloader;
	}
	
	//-------- methods used by message service --------
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Object getMessage()
	{
		return new MessageEnvelope(message, Arrays.asList(receivers), getMessageType().getName());
//		return message;
	}

	/**
	 *  Get the messagetype.
	 *  @return the messagetype.
	 */
	public MessageType getMessageType()
	{
		return messagetype;
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
					MessageEnvelope	envelope = new MessageEnvelope(message, Arrays.asList(receivers),  messagetype.getName());
					data = createData(envelope, codecs, classloader);
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
					prolog = createProlog(codecids);
				}
			}
		}
		return prolog;
	}
	
	/**
	 *  Create prolog data.
	 */
	public static byte[] createProlog(byte[] codecids)
	{
		byte[] ret = new byte[2+codecids.length];
		ret[0] = MESSAGE_TYPE_MAP;
		ret[1] = (byte)codecids.length;
		System.arraycopy(codecids, 0, ret, 2, codecids.length);
		return ret;
	}
	
	/**
	 *  Create the data.
	 */
	public static byte[] createData(Object msg, ICodec[] codecs, ClassLoader cl)
	{
		Object ret = msg;
		for(int i=0; i<codecs.length; i++)
		{
			ret	= codecs[i].encode(ret, cl);
		}
		return (byte[])ret;
	}
	
	/**
	 *  Encode a message.
	 */
	public static byte[] encodeMessage(Object msg, ICodec[] codecs, ClassLoader cl)
	{
		byte[] codecids = new byte[codecs.length];
		for(int i=0; i<codecs.length; i++)
			codecids[i] = codecs[i].getCodecId();
		byte[] prolog = createProlog(codecids);
		byte[] body = createData(msg, codecs, cl);
		byte[] ret = new byte[prolog.length+body.length];
		System.arraycopy(prolog, 0, ret, 0, prolog.length);
		System.arraycopy(body, 0, ret, prolog.length, body.length);
		return ret;
	}
	
	/**
	 *  Decode a message.
	 */
	public static Object decodeMessage(byte[] rawmsg, Map<Byte, ICodec> codecs, ClassLoader cl)
	{
		int	idx	= 0;
		byte rmt = rawmsg[idx++];
		byte[] codec_ids = new byte[rawmsg[idx++]];
		for(int i=0; i<codec_ids.length; i++)
		{
			codec_ids[i] = rawmsg[idx++];
		}

		Object tmp = new ByteArrayInputStream(rawmsg, idx, rawmsg.length-idx);
		for(int i=codec_ids.length-1; i>-1; i--)
		{
			ICodec dec = codecs.get(new Byte(codec_ids[i]));
			tmp = dec.decode(tmp, cl);
		}
		
		return tmp;
	}
}
