package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
	
	/** The message service. */
	protected MessageService ms;
	
	/** The connection initiator. */
	protected IComponentIdentifier initiator;

	/** The participant. */
	protected IComponentIdentifier participant;
	
	/** The transports. */
	protected ITransport[] transports;
	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The codecs. */
	protected ICodec[] codecs;
	
	/** The input flag. */
	protected boolean input;
	
	/** The initiator flag. */
	protected boolean ini;
		
	//-------- constructors --------
	
	/**
	 *  Create a new input connection.
	 */
	public AbstractConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports,
		byte[] codecids, ICodec[] codecs, boolean input, boolean initiator)
	{
		this.ms = ms;
		this.initiator = sender;
		this.participant = receiver;
		this.id = id;
		this.transports = transports;
		this.codecids = codecids;
		this.codecs = codecs; 
		this.input = input;
		this.ini = initiator;
		
		// Send init message if initiator side.
		if(isInitiatorSide())
			sendTask(createTask(getMessageType(StreamSendTask.INIT), 
				new IComponentIdentifier[]{sender, receiver}, true, null));
	}
	
	//-------- methods --------
	
	/**
	 *  Send a task. Automatically closes the stream if
	 *  the other side could not be reached.
	 */
	public IFuture<Void> sendTask(AbstractSendTask task)
	{
//		System.out.println("sendTask: "+task);
		IComponentIdentifier[] recs = task.getReceivers();
		if(recs.length!=1)
			throw new RuntimeException("Must have exactly one receiver.");
		SendManager sm = ms.getSendManager(recs[0]);
		
		IFuture<Void> ret = sm.addMessage(task);
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				// nop if could be sent
			}
			public void exceptionOccurred(Exception exception)
			{
				// close connection in case of send error.
				setClosed();
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public AbstractSendTask createTask(byte type, Object content, Integer seqnumber)
	{
		return createTask(type, content, false, seqnumber);
	}
	
	/**
	 * 
	 */
	public AbstractSendTask createTask(byte type, Object content, boolean usecodecs, Integer seqnumber)
	{
		return new StreamSendTask(type, content==null? StreamSendTask.EMPTY_BYTE_ARRAY: content,
			id, isInitiatorSide()? new IComponentIdentifier[]{participant}: new IComponentIdentifier[]{initiator}, 
			transports, usecodecs? codecids: null, usecodecs? codecs: null, seqnumber);
	}
	
	
	
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
		sendTask(createTask(getMessageType(StreamSendTask.CLOSE), null, null));
	}
	
	/**
	 * 
	 */
	public byte getMessageType(String type)
	{
		// Connection type is determined by initiator and is constant per connection
		boolean contype = isInitiatorSide()? isInputConnection(): !isInputConnection();
		return StreamSendTask.getMessageType(type, contype, isInitiatorSide());
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
