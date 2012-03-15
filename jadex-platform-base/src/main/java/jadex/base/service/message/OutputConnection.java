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
	/**
	 *  Create a new connection.
	 */
	public OutputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports, 
		byte[] codecids, ICodec[] codecs, boolean initiator)
	{
		super(ms, sender, receiver, id, transports, codecids, codecs, false, initiator);
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public synchronized IFuture<Void> write(byte[] data)
	{
		if(closed)
			return new Future<Void>(new RuntimeException("Connection closed."));
		
		// Send data message
		return sendTask(createTask(getMessageType(StreamSendTask.DATA), data));
	}
}
