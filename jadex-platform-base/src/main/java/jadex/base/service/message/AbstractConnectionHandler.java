package jadex.base.service.message;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

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
	
//	/** The transports. */
//	protected ITransport[] transports;
//	
//	/** The codecids. */
//	protected byte[] codecids;
//	
//	/** The codecs. */
//	protected ICodec[] codecs;
	
	/** The connection. */
	protected AbstractConnection con;
	
	/** The latest alive time. */
	protected long alivetime;
	
	/** The current sequence number. */
	protected int seqnumber;
		
	/**
	 * 
	 */
//	public AbstractConnectionHandler(ITransport[] transports,
//		byte[] codecids, ICodec[] codecs, MessageService ms)
	public AbstractConnectionHandler(MessageService ms)
	{
		this.ms = ms;
//		this.transports = transports;
//		this.codecids = codecids;
//		this.codecs = codecs; 
		this.alivetime = System.currentTimeMillis();
		this.seqnumber = -1;
	}
	
	//-------- methods called from message service --------
	
	/**
	 * 
	 */
	public void setConnection(AbstractConnection con)
	{
		this.con = con;
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
	 * 
	 */
	public boolean isDataFinished()
	{
		return true;
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
		boolean isalive = System.currentTimeMillis()<alivetime+lease*1.5;
//		System.out.println("alive: "+isalive+" "+alivetime+" "+System.currentTimeMillis());
		return isalive;
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
	 *  Get the closed.
	 *  @return The closed.
	 */
	public boolean isClosing()
	{
		return getConnection().isClosing();
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getConnectionId()
	{
		return getConnection().getConnectionId();
	}
	
	//-------- methods called from connection --------
	
	/**
	 * 
	 */
	public IFuture<Void> doClose()
	{
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendInit()
	{
		return sendTask(createTask(StreamSendTask.INIT, 
			new IComponentIdentifier[]{getConnection().getInitiator(), getConnection().getParticipant()}, true, null));
	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendAlive()
	{
//		byte type = con.isInitiatorSide()? StreamSendTask.ALIVE_INITIATOR: StreamSendTask.ALIVE_PARTICIPANT;
		return sendTask(createTask(StreamSendTask.ALIVE, null, null));
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get the seqnumber.
	 *  @return the seqnumber.
	 */
	public int getSequenceNumber()
	{
		return seqnumber;
	}
	
	/**
	 *  Get The next seqnumber.
	 *  @return The next seqnumber.
	 */
	public synchronized int getNextSequenceNumber()
	{
		return ++seqnumber;
	}
	
	/**
	 * 
	 */
	public  AbstractConnection getConnection()
	{
		return con;
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
	
	/**
	 * 
	 */
	public AbstractSendTask createTask(String type, Object content, boolean usecodecs, Integer seqnumber)
	{
		return new StreamSendTask(getMessageType(type), content==null? StreamSendTask.EMPTY_BYTE_ARRAY: content,
			getConnectionId(), getConnection().isInitiatorSide()? new IComponentIdentifier[]{getConnection().getParticipant()}: new IComponentIdentifier[]{getConnection().getInitiator()}, 
			getTransports(), usecodecs? getCodecIds(): null, usecodecs? getCodecs(): null, seqnumber);
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
	
	/**
	 * 
	 */
	protected ICodec[] getCodecs()
	{
		return ms.getMessageCodecs(ms.getCodecFactory().getDefaultCodecIds());
	}
	
	/**
	 * 
	 */
	protected byte[] getCodecIds()
	{
		return ms.getCodecFactory().getDefaultCodecIds();
	}
	
	/**
	 * 
	 */
	protected ITransport[] getTransports()
	{
		return ms.getTransports();
	}

}
