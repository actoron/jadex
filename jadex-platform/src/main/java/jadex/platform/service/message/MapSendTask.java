package jadex.platform.service.message;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.platform.service.message.transport.ITransport;
import jadex.platform.service.message.transport.MessageEnvelope;
import jadex.platform.service.message.transport.codecs.CodecFactory;

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
	public MapSendTask(Map<String, Object> message, MessageType messagetype, ITransportComponentIdentifier[] receivers, 
		ITransport[] transports, ICodec[] codecs, ClassLoader classloader, IEncodingContext encodingcontext)//, SendManager manager)
	{
		super(receivers, transports, codecs, (Map<String, Object>)message.get(SFipa.X_NONFUNCTIONAL));
		if(codecs==null || codecs.length==0)
			throw new IllegalArgumentException("Codecs must not null.");
		
		this.message = message;
		this.messagetype = messagetype;
		this.classloader = classloader;
		this.encodingcontext = encodingcontext;
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
	 *  Provide the data as a byte array.
	 */
	protected byte[]	fetchData()
	{
		MessageEnvelope	envelope = new MessageEnvelope(message, Arrays.asList(receivers),  messagetype.getName());
		return createData(envelope, codecs, classloader, encodingcontext);
	}
	
	/**
	 *  Get the prolog bytes.
	 *  Separated from data to avoid array copies.
	 *  Message service expects messages to be delivered in the form {prolog}{data}. 
	 *  @return The prolog bytes.
	 */
	public byte[] fetchProlog()
	{
		return createProlog(codecids);
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
	public static byte[] createData(Object msg, ICodec[] codecs, ClassLoader cl, IEncodingContext context)
	{
		Object ret = msg;
		for(int i=0; i<codecs.length; i++)
		{
			ret	= codecs[i].encode(ret, cl, context);
		}
		return (byte[])ret;
	}
	
	/**
	 *  Encode a message.
	 */
	public static byte[] encodeMessage(Object msg, ICodec[] codecs, ClassLoader cl, IEncodingContext context)
	{
		byte[] codecids = new byte[codecs.length];
		for(int i=0; i<codecs.length; i++)
			codecids[i] = codecs[i].getCodecId();
		byte[] prolog = createProlog(codecids);
		byte[] body = createData(msg, codecs, cl, context);
		byte[] ret = new byte[prolog.length+body.length];
		System.arraycopy(prolog, 0, ret, 0, prolog.length);
		System.arraycopy(body, 0, ret, prolog.length, body.length);
		return ret;
	}
	
	/**
	 *  Decode a message.
	 */
	public static Object decodeMessage(byte[] rawmsg, Map<Byte, ICodec> codecs, ClassLoader cl, IErrorReporter rep)
	{
		int	idx	= 1;	// Skip message type.
		byte[] codec_ids = new byte[rawmsg[idx++]];
		for(int i=0; i<codec_ids.length; i++)
		{
			codec_ids[i] = rawmsg[idx++];
		}

		Object tmp = new ByteArrayInputStream(rawmsg, idx, rawmsg.length-idx);
		for(int i=codec_ids.length-1; i>-1; i--)
		{
			ICodec dec = codecs.get(Byte.valueOf(codec_ids[i]));
			if (dec == null) {
				throw new RuntimeException(CodecFactory.CODEC_NAMES[codec_ids[i]] + " not available!");
			}
			tmp = dec.decode(tmp, cl, rep);
		}
		
		return tmp;
	}

	/**
	 *  Get the codecs that have been used for encoding the message.
	 */
	public static ICodec[] getCodecs(byte[] rawmsg, Map<Byte, ICodec> codecs)
	{
		int	idx	= 1;	// Skip message type.
		ICodec[] mcodecs = new ICodec[rawmsg[idx++]];
		for(int i=0; i<mcodecs.length; i++)
		{
			mcodecs[i] = codecs.get(Byte.valueOf(rawmsg[idx++]));
		}
		return mcodecs;
	}
}
