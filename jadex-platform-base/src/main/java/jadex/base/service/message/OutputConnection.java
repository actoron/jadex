package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class OutputConnection implements IOutputConnection
{
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
		StreamSendTask task = new StreamSendTask(StreamSendTask.INIT_INPUT, receiver, id, 
			new IComponentIdentifier[]{receiver}, transports, codecids, codecs);
		ms.getSendManager(receiver).addMessage(task);
	}
	
	/**
	 * 
	 */
	public IFuture<Void> send(byte[] data)
	{
		// Send data message
		SendManager sm = ms.getSendManager(receiver);
		StreamSendTask task = new StreamSendTask(StreamSendTask.DATA_INPUT, data, id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		return sm.addMessage(task);
	}
	
	/**
	 * 
	 */
	public IFuture<Void> close()
	{
		// Send data message
		SendManager sm = ms.getSendManager(receiver);
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_INPUT, new byte[1], id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		return sm.addMessage(task);
	}
}
