package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class StreamSendTask extends AbstractSendTask implements ISendTask
{
	public static final byte MESSAGE_TYPE_STREAM = 99;

	public static final byte INIT_INPUT = 0;
	public static final byte DATA_INPUT = 1;
	public static final byte CLOSE_INPUT = 2;
	public static final byte INIT_OUTPUT = 3;
	public static final byte DATA_OUTPUT = 4;
	public static final byte CLOSE_OUTPUT = 5;
	
	//-------- attributes --------

	/** The binary message part. */
	protected Object message;
		
//	/** The message type. */
//	protected MessageType messagetype;
	
	/** The stream id. */
	protected int streamid;
	
	/** The type of message (init, data, close). */
	protected byte type;
		
	//-------- constructors --------- 

	/**
	 *  Create a new manager send task.
	 */
	public StreamSendTask(byte type, Object message, int streamid, IComponentIdentifier[] receivers, 
		ITransport[] transports, byte[] codecids, ICodec[] codecs)//, SendManager manager)
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
					prolog = new byte[7+codecids.length];
					prolog[0] = MESSAGE_TYPE_STREAM;
					prolog[1] = type;
					prolog[2] = (byte)codecids.length;
					System.arraycopy(codecids, 0, prolog, 3, codecids.length);
					byte[] strid = SUtil.intToBytes(streamid);
					for(int i=0; i<4; i++)
						prolog[i+3+codecids.length] = strid[i];
				}
			}
		}
		return prolog;
	}
}
