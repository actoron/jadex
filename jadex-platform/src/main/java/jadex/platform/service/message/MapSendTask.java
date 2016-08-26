package jadex.platform.service.message;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.IBinaryCodec;
import jadex.bridge.service.types.message.ISerializer;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.platform.service.message.transport.ITransport;
import jadex.platform.service.message.transport.MessageEnvelope;

/**
 *  The manager send task is responsible for coordinating
 *  the sending of a message to a single destination using
 *  multiple available transports.
 *  
 *  Message format is as follows, in octals:
 *  
 *  Offset		Length	Description
 *  0			1		Message Type = MESSAGE_TYPE_MAP = 1
 *  1			1		Number of binary codec IDs that follow [cidc]
 *  2			[cidc]	IDs of binary codecs used to decode message in encoding order
 *  [cidc]+2	[end]	Encoded message envelope containing content map
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
	
	/** Real receiver in case of proxied messages */
	protected IComponentIdentifier realrec;
	
	/** The rid for decoding if specified. */
	protected IResourceIdentifier rid;
	
	/** Cached prolog. */
	protected byte[] prolog;
	
	/** Cached data. */
	protected byte[] data;
	
	//-------- constructors --------- 

	/**
	 *  Create a new manager send task.
	 */
	@SuppressWarnings("unchecked")
	public MapSendTask(Map<String, Object> message, MessageType messagetype, ITransportComponentIdentifier[] receivers, IComponentIdentifier realrec, IResourceIdentifier rid,
		ITransport[] transports, ITraverseProcessor[] preprocessors, ISerializer serializer, IBinaryCodec[] codecs, ClassLoader classloader)//, SendManager manager)
	{
		super(receivers, transports, preprocessors, serializer, codecs, (Map<String, Object>)message.get(SFipa.X_NONFUNCTIONAL));
		if(codecs==null || codecs.length==0)
			throw new IllegalArgumentException("Codecs must not null.");
		this.message = message;
		this.messagetype = messagetype;
		this.classloader = classloader;
		this.realrec = realrec;
		this.rid = rid;
	}
	
	//-------- methods used by message service --------
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public MessageEnvelope getEnvelope()
	{
		return new MessageEnvelope(receivers, realrec, rid, messagetype.getName(), MESSAGE_TYPE_MAP);
	}
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
//	public Object getMessage()
//	{
//		return new MessageEnvelope(message, Arrays.asList(receivers), getMessageType().getName());
//		return message;
//	}
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Object getRawMessage()
	{
		return message;
	}
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public byte[] getEncodedMessage()
	{
		getProlog();
		getData();
		byte[] msg = new byte[prolog.length+data.length];
		System.arraycopy(prolog, 0, msg, 0, prolog.length);
		System.arraycopy(data, 0, msg, prolog.length, data.length);
		return msg;
	}
	
	/**
	 *  Set the message.
	 */
	public void setMessageMap(Map<String, Object> msg)
	{
		message = msg;
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
		if (data == null)
		{
			data = createData(message, preprocessors, serializer, codecs, classloader);
		}
		return data;
	}
	
	
	
//	static FileOutputStream dump;
//	static
//	{
//		try
//		{
//			dump = new FileOutputStream("/home/jander/dumpfile" + (int)(Math.random()*10000));
//		}
//		catch (FileNotFoundException e)
//		{
//		}
//	}
	
	/**
	 *  Get the prolog bytes.
	 *  Separated from data to avoid array copies.
	 *  Message service expects messages to be delivered in the form {prolog}{data}. 
	 *  @return The prolog bytes.
	 */
	public byte[] fetchProlog()
	{
		if (prolog == null)
		{
			prolog = new byte[2 + codecs.length];
			prolog[0] = MESSAGE_TYPE_MAP;
			prolog[1] = (byte) codecs.length;
			System.arraycopy(codecids, 0, prolog, 2, codecids.length);
		}
		
		return prolog;
	}
	
	/**
	 *  Create the data.
	 */
	protected byte[] createData(Object msg, ITraverseProcessor[] preprocessors, ISerializer serializer, IBinaryCodec[] codecs, ClassLoader cl)
	{
		MessageEnvelope	envelope = new MessageEnvelope(receivers, realrec, rid, messagetype.getName(), MESSAGE_TYPE_MAP);
		envelope.setContentData(serializer.encode(msg, cl, preprocessors));
		byte[] envdata = serializer.encode(envelope, MapSendTask.class.getClassLoader(), new ITraverseProcessor[] {
			new ITraverseProcessor()
			{
				// Strip unneeded addresses to reduce message size.
				
				public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
				{
					return new BasicComponentIdentifier((ITransportComponentIdentifier) object);
				}
				
				public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
				{
					return object instanceof ITransportComponentIdentifier;
				}
			}
		});
		
		byte[] ret = new byte[1 + envdata.length];
		ret[0] = serializer.getSerializerId();
		System.arraycopy(envdata, 0, ret, 1, envdata.length);
		
//		System.out.println("MESSAGEuc: " + new String(ret, SUtil.ASCII));
		
		for(int i=0; i<codecs.length; i++)
		{
			ret	= codecs[i].encode(ret);
		}
		
//		synchronized(lock)
//		{
//			avg = avg * count;
//			avg += ret.length;
//			min = ret.length<min?ret.length:min;
//			max = ret.length>max?ret.length:max;
//			++count;
//			avg /= count;
//			System.out.println("MESSAGE SIZE: " + ret.length + " " + min + " " + avg + " " + max);
//		}
		
		return ret;
	}
	
//	static Object lock = new Object();
//	static volatile long count = 0;
//	static volatile double avg = 0;
//	static volatile double min = Double.POSITIVE_INFINITY;
//	static volatile double max = 0;
	
	/**
	 *  Decode a message envelope.
	 */
	protected static MessageEnvelope decodeMessageEnvelope(byte[] rawmsg, Map<Byte, ISerializer> serializers, Map<Byte, IBinaryCodec> codecs, ClassLoader cl, IErrorReporter rep)
	{
//		synchronized(lock)
//		{
//			avg = avg * count;
//			avg += rawmsg.length;
//			min = rawmsg.length<min?rawmsg.length:min;
//			max = rawmsg.length>max?rawmsg.length:max;
//			++count;
//			avg /= count;
//			System.out.println("PROLOG SIZE: " + rawmsg.length + " " + min + " " + avg + " " + max);
//		}
		byte[] codecids = new byte[rawmsg[1] & 0xFF];
		System.arraycopy(rawmsg, 2, codecids, 0, codecids.length);
		
		int offset = 2 + codecids.length;
		int length = rawmsg.length - 1;
		for (int i = codecids.length-1; i >= 0; --i)
		{
			rawmsg = codecs.get(codecids[i]).decode(rawmsg, offset, length);
			offset = 0;
			length = rawmsg.length;
		}
		
		MessageEnvelope envelope = (MessageEnvelope) serializers.get(rawmsg[offset]).decode(new ByteArrayInputStream(rawmsg, offset + 1, rawmsg.length - offset - 1), cl, null, rep);
		envelope.serializerid = rawmsg[offset];
		
		return envelope;
	}
	
	/**
	 *  Decode a message.
	 */
	protected static Object decodeMessage(MessageEnvelope envelope, ITraverseProcessor[] postprocessors, Map<Byte, ISerializer> serializers, Map<Byte, IBinaryCodec> codecs, ClassLoader cl, IErrorReporter rep)
	{
		Object ret = serializers.get(envelope.serializerid).decode(envelope.getContentData(), cl, postprocessors, rep);
		envelope.setContentData(null);
		return ret;
	}

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
