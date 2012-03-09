package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class OutputConnection implements IOutputConnection
{
	/** Boolean flag if connection is closed. */
	protected boolean closed;

	/** The message service. */
	protected MessageService ms;
	
	/** The sender. */
	protected IComponentIdentifier sender;

	/** The receiver. */
	protected IComponentIdentifier receiver;

	/** The connection id. */
	protected int id;
	
	/** The transports. */
	protected ITransport[] transports;
	
	/** The codecids. */
	protected byte[] codecids;
	
	/** The codecs. */
	protected ICodec[] codecs;

	/**
	 * 
	 */
	public OutputConnection(MessageService ms, IComponentIdentifier sender, 
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
		
		// Send init message
		StreamSendTask task = new StreamSendTask(StreamSendTask.INIT_INPUT_INITIATOR, 
			new IComponentIdentifier[]{sender, receiver}, id, 
			new IComponentIdentifier[]{receiver}, transports, codecids, codecs);
		ms.getSendManager(receiver).addMessage(task);
	}
	
	/**
	 * 
	 */
	public synchronized IFuture<Void> send(byte[] data)
	{
		if(closed)
			throw new RuntimeException("Connection closed.");
		
		// Send data message
		SendManager sm = ms.getSendManager(receiver);
		StreamSendTask task = new StreamSendTask(StreamSendTask.DATA_INPUT_INITIATOR, data, id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		return sm.addMessage(task);
	}
	
	/**
	 * 
	 */
	public synchronized IFuture<Void> close()
	{
		if(closed)
			throw new RuntimeException("Connection closed.");

		// Send data message
		setClosed();
		SendManager sm = ms.getSendManager(receiver);
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_INPUT_INITIATOR, new byte[1], id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		return sm.addMessage(task);
	}
	
	/**
	 *  Set the stream to be closed.
	 */
	public synchronized void setClosed()
	{
		this.closed = true;
	}
}
