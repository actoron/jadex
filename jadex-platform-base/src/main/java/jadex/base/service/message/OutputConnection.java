package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Output connection for writing data.
 */
public class OutputConnection extends AbstractConnection implements IOutputConnection
{
	/** The current sequence number. */
	protected int seqnumber;
		
	/** The connection handler. */
	protected OutputConnectionHandler ch;
	
	/**
	 *  Create a new connection.
	 */
	public OutputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports, 
		byte[] codecids, ICodec[] codecs, boolean initiator, OutputConnectionHandler ch)
	{
		super(ms, sender, receiver, id, transports, codecids, codecs, false, initiator);
		if(ch==null)
			throw new IllegalArgumentException("Connection hanlder must not null.");
		this.ch = ch;
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public synchronized IFuture<Void> write(byte[] data)
	{
		if(closed)
			return new Future<Void>(new RuntimeException("Connection closed."));
		StreamSendTask task = (StreamSendTask)createTask(getMessageType(StreamSendTask.DATA), data, new Integer(seqnumber++));
		// Send data message
//		return sendTask(task);
		return ch.send(task);
	}
}
