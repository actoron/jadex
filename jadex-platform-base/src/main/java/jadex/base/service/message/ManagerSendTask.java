package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.base.service.message.transport.niotcpmtp.NIOTCPTransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ManagerSendTask
{
	/** The message. */
	protected Map message;
	
	/** The encoded message envelope. */
	protected byte[] data;

	/** The message prolog. */
	protected byte[] prolog;
	
	/** The message type. */
	protected MessageType messagetype;
	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The codecs. */
	protected ICodec[] codecs;
	
	/** The managed receivers. */
	protected IComponentIdentifier[] receivers;

	/** The transports to be tried. */
	protected List transports;

	/** The target manager. */
//	protected SendManager manager;
	
	/**
	 *  Create a new manager send task.
	 */
	public ManagerSendTask(Map message, MessageType messagetype, IComponentIdentifier[] receivers, 
		ITransport[] transports, byte[] codecids, ICodec[] codecs)//, SendManager manager)
	{
		if(codecids==null || codecids.length==0)
			throw new IllegalArgumentException("Codec ids must not null.");
		if(codecs==null || codecs.length==0)
			throw new IllegalArgumentException("Codecs must not null.");
		
		this.message = message;
		this.messagetype = messagetype;
		this.receivers = receivers;
		this.transports = new ArrayList(Arrays.asList(transports));
		this.codecs = codecs;
		this.codecids = codecids;
//		this.manager = manager;
	}
	
	
	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public Map getMessage()
	{
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
	 *  Get the receivers.
	 *  @return the receivers.
	 */
	public IComponentIdentifier[] getReceivers()
	{
		return receivers;
	}
	
	/**
	 *  Get the transports.
	 *  @return the transports.
	 */
	public List getTransports()
	{
		return transports;
	}
	
	/**
	 *  Get the codecs.
	 *  @return the codecs.
	 */
	public ICodec[] getCodecs()
	{
		return codecs;
	}
	
//	/**
//	 *  Set the receivers.
//	 */
//	public void	setReceivers(IComponentIdentifier[] receivers)
//	{
//		this.receivers	= receivers;
//	}

//	/**
//	 *  Get the manager.
//	 *  @return the manager.
//	 */
//	public SendManager getSendManager()
//	{
//		return manager;
//	}

	/**
	 *  Get the codecids.
	 *  @return the codecids.
	 */
	public byte[] getCodecIds()
	{
		return codecids;
	}

//	/**
//	 *  Set the codecids.
//	 *  @param codecids The codecids to set.
//	 */
//	public void setCodecIds(byte[] codecids)
//	{
//		this.codecids = codecids;
//	}
	
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
	 * 
	 */
	public byte[] getProlog()
	{
		byte[] data = getData();
		byte[] prolog = new byte[1+codecids.length+4];
		prolog[0] = (byte)codecids.length;
		System.arraycopy(codecids, 0, prolog, 1, codecids.length);
		System.arraycopy(SUtil.intToBytes(prolog.length+data.length), 0, prolog, codecids.length+1, 4);
		return prolog;
	}
}
