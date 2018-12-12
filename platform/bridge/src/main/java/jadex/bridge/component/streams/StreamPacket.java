package jadex.bridge.component.streams;

import jadex.bridge.IComponentIdentifier;

/**
 *  Data container for a streaming packet.
 */
public class StreamPacket
{
	//-------- attributes --------
	
	/** The type of the message. */
	protected byte type;
	
	/** The id of the connection. */
	protected Integer connectionid;
	
	/** The stream data. */
	protected Object data;
	
	/** The sequence number. */
	protected Integer sequencenumber;
	
	/** The reqceiver. */ // todo: remove?!
	public IComponentIdentifier receiver;

	//-------- constructors --------
	
	/**
	 *  Create a new task.
	 */
	public StreamPacket()
	{
		// Bean constructor
	}
	
	/**
	 *  Create a new task.
	 */
	public StreamPacket(byte type, Integer connectionid, Object data, Integer sequencenumber, IComponentIdentifier receiver)
	{
		this.type = type;
		this.connectionid = connectionid;
		this.data = data;
		this.sequencenumber = sequencenumber;
		this.receiver = receiver;
	}
	
	/**
	 *  Create a new task.
	 */
	public StreamPacket(StreamPacket packet)
	{
		this.type = packet.getType();
		this.connectionid = packet.getConnectionId();
		this.data = packet.getData();
		this.sequencenumber = packet.getSequenceNumber();
		this.receiver = packet.receiver;
	}

	/**
	 *  Get the type.
	 *  @return the type
	 */
	public byte getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set
	 */
	public void setType(byte type)
	{
		this.type = type;
	}

	/**
	 *  Get the data.
	 *  @return the data
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 *  Get the sequenceNumber.
	 *  @return the sequenceNumber
	 */
	public Integer getSequenceNumber()
	{
		return sequencenumber;
	}

	/**
	 *  Set the sequenceNumber.
	 *  @param sequenceNumber The sequenceNumber to set
	 */
	public void setSequenceNumber(Integer sequenceNumber)
	{
		this.sequencenumber = sequenceNumber;
	}

	/**
	 *  Get the connectionid.
	 *  @return the connectionid
	 */
	public Integer getConnectionId()
	{
		return connectionid;
	}

	/**
	 *  Set the connectionid.
	 *  @param connectionid The connectionid to set
	 */
	public void setConnectionId(Integer connectionid)
	{
		this.connectionid = connectionid;
	}
	
	
}
