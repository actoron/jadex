package jadex.base.service.message;

import jadex.bridge.IComponentIdentifier;

/**
 *  Abstract base class for connections.
 */
public abstract class AbstractConnection
{
	//-------- attributes --------
	
	/** Boolean flag if connection is closed. */
	protected boolean closed;
	
	/** The connection id. */
	protected int id;
	
	/** The connection initiator. */
	protected IComponentIdentifier initiator;

	/** The participant. */
	protected IComponentIdentifier participant;

	/** The input flag. */
	protected boolean input;
	
	/** The initiator flag. */
	protected boolean ini;
	
	/** The abstract connection handler. */
	protected AbstractConnectionHandler ch;
		
	//-------- constructors --------
	
	/**
	 *  Create a new input connection.
	 */
	public AbstractConnection(IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, boolean input, boolean initiator, AbstractConnectionHandler ch)
	{
		this.initiator = sender;
		this.participant = receiver;
		this.id = id;
		this.input = input;
		this.ini = initiator;
		this.ch = ch;
		
		if(ch==null)
			throw new IllegalArgumentException("Connection hanlder must not null.");
		
		// Send init message if initiator side.
		ch.setConnection(this);
		if(isInitiatorSide())
			ch.sendInit();
	}
	
	//-------- methods --------	
	
	/**
	 *  Set the connection to closed.
	 */
	public synchronized void setClosed()
	{
		this.closed = true;
	}

	/**
	 *  Get the closed.
	 *  @return The closed.
	 */
	public boolean isClosed()
	{
		return closed;
	}

	/**
	 *  Close the connection.
	 *  Notifies the other side that the connection has been closed.
	 */
	public synchronized void close()
	{
		if(closed)
			return;

		// Send data message
		setClosed();
		ch.sendClose();
//		sendTask(createTask(getMessageType(StreamSendTask.CLOSE), null, null));
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getConnectionId()
	{
		return id;
	}
	
	/**
	 * 
	 */
	public boolean isInitiatorSide()
	{
		return ini;
	}

	/**
	 * 
	 */
	public boolean isInputConnection()
	{
		return input;
	}
	
	/**
	 *  Get the initiator.
	 *  @return The initiator.
	 */
	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	/**
	 *  Get the participant.
	 *  @return The participant.
	 */
	public IComponentIdentifier getParticipant()
	{
		return participant;
	}
	
}
