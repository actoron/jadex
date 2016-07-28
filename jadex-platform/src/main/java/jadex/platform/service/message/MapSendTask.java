package jadex.platform.service.message;

import java.io.ByteArrayInputStream;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.IBinaryCodec;
import jadex.bridge.service.types.message.ISerializer;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.transformation.binaryserializer.IDecoderHandler;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.platform.service.message.transport.ITransport;
import jadex.platform.service.message.transport.MessageEnvelope;
import jadex.platform.service.message.transport.serializers.JadexBinarySerializer;

/**
 *  The manager send task is responsible for coordinating
 *  the sending of a message to a single destination using
 *  multiple available transports.
 *  
 *  Message format is as follows, in octals:
 *  
 *  Offset		Length	Description
 *  0			1		Message Type = MESSAGE_TYPE_MAP = 1
 *  1			1		ID of serializer used to encode message
 *  2			1		Number of binary codec IDs that follow [cidc]
 *  3			[cidc]	IDs of binary codecs used to decode message in encoding order
 *  [cidc]+3	[end]	Encoded message map
 *  
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
	
	/** Service receiver in case of service calls */
	protected IComponentIdentifier servicerec;
	
	/** The rid for decoding if specified. */
	protected IResourceIdentifier rid;
	
	//-------- constructors --------- 

	/**
	 *  Create a new manager send task.
	 */
	public MapSendTask(Map<String, Object> message, MessageType messagetype, ITransportComponentIdentifier[] receivers, IComponentIdentifier servicerec, IResourceIdentifier rid,
		ITransport[] transports, ITraverseProcessor[] preprocessors, ISerializer serializer, IBinaryCodec[] codecs, ClassLoader classloader)//, SendManager manager)
	{
		super(receivers, transports, preprocessors, serializer, codecs, (Map<String, Object>)message.get(SFipa.X_NONFUNCTIONAL));
		if(codecs==null || codecs.length==0)
			throw new IllegalArgumentException("Codecs must not null.");
		this.message = message;
		this.messagetype = messagetype;
		this.classloader = classloader;
		this.servicerec = servicerec;
		this.rid = rid;
	}
	
	//-------- methods used by message service --------
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public MessageEnvelope getEnvelope()
	{
		return new MessageEnvelope(receivers, servicerec, rid, messagetype.getName(), MESSAGE_TYPE_MAP);
	}
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Object getMessage()
	{
//		return new MessageEnvelope(message, Arrays.asList(receivers), getMessageType().getName());
		return message;
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
//		MessageEnvelope	envelope = new MessageEnvelope(message, Arrays.asList(receivers),  messagetype.getName());
		return createData(message, preprocessors, serializer, codecs, classloader);
	}
	
	/**
	 *  Get the prolog bytes.
	 *  Separated from data to avoid array copies.
	 *  Message service expects messages to be delivered in the form {prolog}{data}. 
	 *  @return The prolog bytes.
	 */
	public byte[] fetchProlog()
	{
		MessageEnvelope	envelope = new MessageEnvelope(receivers, servicerec, rid, messagetype.getName(), MESSAGE_TYPE_MAP);
		byte[] envdata = serializer.encode(envelope, MapSendTask.class.getClassLoader(), preprocessors);
		for(int i=0; i<codecs.length; i++)
		{
			envdata	= codecs[i].encode(envdata);
		}
		
		byte[] ret = new byte[7+codecids.length+envdata.length];
		ret[0] = MESSAGE_TYPE_MAP;
		SUtil.intIntoBytes(7+codecids.length+envdata.length, ret, 1);
		ret[5] = serializer.getSerializerId();
		ret[6] = (byte) codecids.length;
		System.arraycopy(codecids, 0, ret, 7, codecids.length);
		System.arraycopy(envdata, 0, ret, 7 + codecids.length, envdata.length);
		
		return ret;
	}
	
	/**
	 *  Create the data.
	 */
	protected static byte[] createData(Object msg, ITraverseProcessor[] preprocessors, ISerializer serializer, IBinaryCodec[] codecs, ClassLoader cl)
	{
		byte[] ret = serializer.encode(msg, cl, preprocessors);
		for(int i=0; i<codecs.length; i++)
		{
			ret	= codecs[i].encode(ret);
		}
		return (byte[])ret;
	}
	
	// TODO: Hack! Remove later when manual encoding hooks are removed.
	protected static ISerializer FALLBACK_SERIALIZER = new JadexBinarySerializer();
	
	/**
	 *  Encode a message.
	 */
//	protected static byte[] encodeMessage(Object msg, ITraverseProcessor[] preprocessors, ISerializer serializer, IBinaryCodec[] codecs, Object ridsource, ClassLoader cl)
//	{
//		serializer = serializer==null?FALLBACK_SERIALIZER:serializer;
//		byte[] codecids = new byte[codecs.length];
//		for(int i=0; i<codecs.length; i++)
//			codecids[i] = codecs[i].getCodecId();
//		byte[] prolog = createProlog(serializer, codecids, ridsource);
//		byte[] body = createData(msg, preprocessors, serializer, codecs, cl);
//		byte[] ret = new byte[prolog.length+body.length];
//		System.arraycopy(prolog, 0, ret, 0, prolog.length);
//		System.arraycopy(body, 0, ret, prolog.length, body.length);
//		return ret;
//	}
	
	/**
	 *  Decode a message envelope.
	 */
	protected static MessageEnvelope decodeMessageEnvelope(byte[] rawmsg, Map<Byte, ISerializer> serializers, Map<Byte, IBinaryCodec> codecs, ClassLoader cl, IErrorReporter rep)
	{
		int envlength = SUtil.bytesToInt(rawmsg, 1) - 7;
		ISerializer serializer = serializers.get(rawmsg[5]);
		byte[] codecids = new byte[rawmsg[6]];
		envlength -= codecids.length;
		System.arraycopy(rawmsg, 7, codecids, 0, codecids.length);
		byte[] envdata = new byte[envlength];
		System.arraycopy(rawmsg, 7 + codecids.length, envdata, 0, envdata.length);
		for (int i = codecids.length-1; i >= 0; --i)
		{
			envdata = codecs.get(codecids[i]).decode(envdata);
		}
		MessageEnvelope envelope =null;
		try{
		 envelope = (MessageEnvelope) serializer.decode(envdata, cl, null, rep);
		}
		catch(Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
		}
		return envelope;
	}
	
	/**
	 *  Decode a message.
	 */
	protected static Object decodeMessage(byte[] rawmsg, IDecoderHandler[] postprocessors, Map<Byte, ISerializer> serializers, Map<Byte, IBinaryCodec> codecs, ClassLoader cl, IErrorReporter rep)
	{
		int	plength	= SUtil.bytesToInt(rawmsg, 1);
		ISerializer serializer = serializers.get(rawmsg[5]);
		byte[] codecids = new byte[rawmsg[6]];
		System.arraycopy(rawmsg, 7, codecids, 0, codecids.length);
		
		byte[] msgbytes = new byte[rawmsg.length - plength];
		System.arraycopy(rawmsg, plength, msgbytes, 0, msgbytes.length);
		for (int i = codecids.length-1; i >= 0; --i)
		{
			msgbytes = codecs.get(codecids[i]).decode(msgbytes);
		}
		
		Object ret = serializer.decode(msgbytes, cl, postprocessors, rep);
		
		return ret;
	}
//	protected static Object decodeMessage(byte[] rawmsg, IDecoderHandler[] postprocessors, Map<Byte, ISerializer> serializers, Map<Byte, IBinaryCodec> codecs, ClassLoader cl, IErrorReporter rep)
//	{
//		int	idx	= 1;	// Skip message type.
//		byte serializer_id = rawmsg[idx++];
//		ISerializer serializer = serializers.get(serializer_id);
//		if (serializer == null)
//			throw new RuntimeException("Serializer ID #" + serializer_id +" not available!");
//		byte[] codec_ids = new byte[rawmsg[idx++]];
//		for(int i=0; i<codec_ids.length; i++)
//		{
//			codec_ids[i] = rawmsg[idx++];
//		}
//
//		Object tmp = new ByteArrayInputStream(rawmsg, idx, rawmsg.length-idx);
//		
//		for(int i=codec_ids.length-1; i>-1; i--)
//		{
//			IBinaryCodec dec = codecs.get(Byte.valueOf(codec_ids[i]));
//			if (dec == null) {
//				throw new RuntimeException("Binary codec ID #" + serializer_id + " not available!");
//			}
//			tmp = dec.decode(tmp);
//		}
//		
//		tmp = serializer.decode(tmp, cl, postprocessors, rep);
//		
//		return tmp;
//	}

	/**
	 *  Get the codecs that have been used for encoding the message.
	 */
	protected static IBinaryCodec[] getCodecs(byte[] rawmsg, Map<Byte, IBinaryCodec> codecs)
	{
		int	idx	= 1;	// Skip message type.
		IBinaryCodec[] mcodecs = new IBinaryCodec[rawmsg[idx++]];
		for(int i=0; i<mcodecs.length; i++)
		{
			mcodecs[i] = codecs.get(Byte.valueOf(rawmsg[idx++]));
		}
		return mcodecs;
	}
}
