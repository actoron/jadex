package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.MessageType;

import java.util.Arrays;
import java.util.Map;

/**
 *  The manager send task is responsible for coordinating
 *  the sending of a message to a single destination using
 *  multiple available transports.
 */
public class MapSendTask extends AbstractSendTask implements ISendTask
{
	public static final byte MESSAGE_TYPE_MAP = 1;
	
	//-------- attributes --------
	
	/** The message. */
	protected Map<String, Object> message;
	
	/** The message type. */
	protected MessageType messagetype;

	//-------- constructors --------- 

	/**
	 *  Create a new manager send task.
	 */
	public MapSendTask(Map<String, Object> message, MessageType messagetype, IComponentIdentifier[] receivers, 
		ITransport[] transports, byte[] codecids, ICodec[] codecs)//, SendManager manager)
	{
		super(receivers, transports, codecids, codecs);
		if(codecids==null || codecids.length==0)
			throw new IllegalArgumentException("Codec ids must not null.");
		if(codecs==null || codecs.length==0)
			throw new IllegalArgumentException("Codecs must not null.");
		
		this.message = message;
		this.messagetype = messagetype;
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
					
					Object enc_msg = envelope;
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
					prolog = new byte[2+codecids.length];
					prolog[0] = MESSAGE_TYPE_MAP;
					prolog[1] = (byte)codecids.length;
					System.arraycopy(codecids, 0, prolog, 2, codecids.length);
				}
			}
		}
		return prolog;
	}
}
