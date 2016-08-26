package jadex.platform.service.message.transport;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 *  The message envelope holding the native message,
 *  the receivers and the message type.
 */
public class MessageEnvelope
{
	//-------- attributes --------
	
	/** Optional message type defining the structure, e.g. "fipa". */
	protected String messagetype;
	
	/** The receivers. */
	protected IComponentIdentifier[] receivers;
	
	/** The receivers. */
	protected IComponentIdentifier realrec;
	
	/** The rid for decoding if specified. */
	protected IResourceIdentifier rid;
	
	/** Content data. */
	protected byte[] contentdata;
	
	/** Serializer ID field, only used during decode, not transmitted. */
	public byte serializerid;
	
	//-------- constructors --------

	/**
	 *  Create a new message envelope.
	 *  (bean constructor)
	 */
	public MessageEnvelope()
	{
	}
	
	/**
	 *  Create a new message envelope.
	 */
	public MessageEnvelope(IComponentIdentifier[] receivers, IComponentIdentifier realrec, IResourceIdentifier rid, String messagetype, byte messagetypeid)
	{
		this.receivers = new IComponentIdentifier[receivers.length];
		System.arraycopy(receivers, 0, this.receivers, 0, receivers.length);
		this.messagetype = messagetype;
		this.realrec = realrec;
		if (rid != null && rid.getGlobalIdentifier() != null)
			this.rid = rid;
	}
	
	/**
	 * Get the receivers.
	 */
	// Legacy compatibility hack. Should be ITransportComponentIdentifier
	public IComponentIdentifier[] getReceivers()
	{
		return receivers==null? new ITransportComponentIdentifier[0]: receivers;
	}
	
	/**
	 * Get the receivers.
	 */
	// Legacy compatibility hack. Should be ITransportComponentIdentifier
	public void setReceivers(IComponentIdentifier[] receivers)
	{
		this.receivers = receivers;
	}
	
	/**
	 *  Add a receiver.
	 */
	public void addReceiver(ITransportComponentIdentifier receiver)
	{
		if(receivers==null)
			receivers = new ITransportComponentIdentifier[1];
		else
		{
			ITransportComponentIdentifier[] tmp = new ITransportComponentIdentifier[receivers.length + 1];
			System.arraycopy(receivers, 0, tmp, 0, receivers.length);
			receivers = tmp;
		}
		receivers[receivers.length - 1] = receiver;
	}
	
	/**
	 * @return the rid
	 */
	public IResourceIdentifier getRid()
	{
		return rid;
	}

	/**
	 *  Sets the rid.
	 *  @param rid The rid to set
	 */
	public void setRid(IResourceIdentifier rid)
	{
		this.rid = rid;
	}

	/**
	 * @return the real receiver
	 */
	public IComponentIdentifier getRealRec()
	{
		return realrec;
	}

	/**
	 *  Sets the real receiver.
	 *  @param servicerec The real receiver to set
	 */
	public void setServiceRec(IComponentIdentifier realrec)
	{
		this.realrec = realrec;
	}
	/**
	 *  Set the type (e.g. "fipa").
	 * @param messagetypename 
	 */
	public void setTypeName(String messagetypename)
	{
		messagetype = messagetypename;
	}

	/**
	 *  Get the type (e.g. "fipa").
	 */
	public String getTypeName()
	{
		return messagetype;
	}
	
	
	
	/**
	 * @return the contentdata
	 */
	public byte[] getContentData()
	{
		return contentdata;
	}

	/**
	 *  Sets the contentdata.
	 *  @param contentdata The contentdata to set
	 */
	public void setContentData(byte[] contentdata)
	{
		this.contentdata = contentdata;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(SReflect.getInnerClassName(this.getClass())+"(");
		//sb.append("sender: "+getSender()+", ");
		sb.append("receivers: "+SUtil.arrayToString(getReceivers())+", ");
		sb.append("message type: "+messagetype);
//		sb.append("raw values: "+message);
//		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}
}
