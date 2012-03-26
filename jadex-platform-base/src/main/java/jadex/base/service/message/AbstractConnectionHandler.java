package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * 
 */
public class AbstractConnectionHandler
{
	/** The message service. */
	protected MessageService ms;
	
	/** The transports. */
	protected ITransport[] transports;
	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The codecs. */
	protected ICodec[] codecs;
	
	/** The connection. */
	protected AbstractConnection con;
	
	/** The latest alive time. */
	protected long alivetime;
	
	/**
	 * 
	 */
	public AbstractConnectionHandler(AbstractConnection con, ITransport[] transports,
		byte[] codecids, ICodec[] codecs, MessageService ms)
	{
		this.ms = ms;
		this.con = con;
		this.transports = transports;
		this.codecids = codecids;
		this.codecs = codecs; 
		this.alivetime = System.currentTimeMillis();
	}
	
	/**
	 * 
	 */
	public void setConnection(AbstractConnection con)
	{
		this.con = con;
	}
	
	/**
	 *  Set the alive time of the other connection side.
	 */
	public void setAliveTime(long alivetime)
	{
//		System.out.println("new lease: "+alivetime);
		this.alivetime = alivetime;
	}
	
	/**
	 * 
	 */
	public boolean isConnectionAlive(long lease)
	{
		boolean isalive = System.currentTimeMillis()<alivetime+lease*1.3;
//		System.out.println("alive: "+isalive+" "+alivetime+" "+System.currentTimeMillis());
		return isalive;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendAlive()
	{
//		byte type = con.isInitiatorSide()? StreamSendTask.ALIVE_INITIATOR: StreamSendTask.ALIVE_PARTICIPANT;
		return sendTask(createTask(StreamSendTask.ALIVE, null, null));
	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendInit()
	{
		return sendTask(createTask(StreamSendTask.INIT, 
			new IComponentIdentifier[]{sender, receiver}, true, null));
	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendClose()
	{
		return sendTask(createTask(StreamSendTask.CLOSE, null, null));
	}
	
	
	
	/**
	 * 
	 */
	public  AbstractConnection getConnection()
	{
		return con;
	}
	
	/**
	 *  Close the connection.
	 *  Notifies the other side that the connection has been closed.
	 */
	public void close()
	{
		con.close();
	}
	
	/**
	 *  Get the closed.
	 *  @return The closed.
	 */
	public boolean isClosed()
	{
		return getConnection().isClosed();
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getConnectionId()
	{
		return getConnection().getConnectionId();
	}
	
	/**
	 * 
	 */
	public byte getMessageType(String type)
	{
		// Connection type is determined by initiator and is constant per connection
		boolean contype = getConnection().isInitiatorSide()? getConnection().isInputConnection(): !getConnection().isInputConnection();
		return StreamSendTask.getMessageType(type, contype, getConnection().isInitiatorSide());
	}
	
	/**
	 * 
	 */
	public AbstractSendTask createTask(String type, Object content, Integer seqnumber)
	{
		return createTask(type, content, false, seqnumber);
	}
	
//	/**
//	 * 
//	 */
//	public AbstractSendTask createTask(byte type, Object content, Integer seqnumber)
//	{
//		return createTask(type, content, false, seqnumber);
//	}
	
	/**
	 * 
	 */
	public AbstractSendTask createTask(String type, Object content, boolean usecodecs, Integer seqnumber)
	{
		return new StreamSendTask(getMessageType(type), content==null? StreamSendTask.EMPTY_BYTE_ARRAY: content,
			getConnectionId(), getConnection().isInitiatorSide()? new IComponentIdentifier[]{getConnection().getParticipant()}: new IComponentIdentifier[]{getConnection().getInitiator()}, 
			transports, usecodecs? codecids: null, usecodecs? codecs: null, seqnumber);
	}
	
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
				getConnection().setClosed();
			}
		});
		
		return ret;
	}
}
