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
	/** Boolean flag if connection is closed. */
	protected boolean closed;
	
	/** The connection id. */
	protected int id;
	
	/** The message service. */
	protected MessageService ms;
	
	/** The sender. */
	protected IComponentIdentifier sender;

	/** The receiver. */
	protected IComponentIdentifier receiver;
	
	/** The transports. */
	protected ITransport[] transports;
	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The codecs. */
	protected ICodec[] codecs;
	
	/**
	 *  Create a new input connection.
	 */
	public AbstractConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports,
		byte[] codecids, ICodec[] codecs)
	{
		this.ms = ms;
		this.sender = sender;
		this.receiver = receiver;
		this.id = id;
		this.transports = transports;
		this.codecids = codecids;
		this.codecs = codecs; 
	}
	
	/**
	 *  Send a task. Automatically closes the stream if
	 *  the other side could not be reached.
	 */
	protected IFuture<Void> sendTask(AbstractSendTask task)
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
	 *  Set the connection to closed.
	 */
	public synchronized void setClosed()
	{
		this.closed = true;
	}

	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getConnectionId()
	{
		return id;
	}
}
