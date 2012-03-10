package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Implementation for an output connection on initiator side.
 */
public class InitiatorOutputConnection extends AbstractConnection implements IOutputConnection
{
	/**
	 *  Create a new connection.
	 */
	public InitiatorOutputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports, 
		byte[] codecids, ICodec[] codecs)
	{
		super(ms, sender, receiver, id, transports, codecids, codecs);
		
		// Send init message
		StreamSendTask task = new StreamSendTask(StreamSendTask.INIT_INPUT_INITIATOR, 
			new IComponentIdentifier[]{sender, receiver}, id, 
			new IComponentIdentifier[]{receiver}, transports, codecids, codecs);
		sendTask(task);
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
		StreamSendTask task = new StreamSendTask(StreamSendTask.DATA_INPUT_INITIATOR, data, id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		return sendTask(task);
	}
	
	/**
	 *  Close the connection.
	 */
	public synchronized void close()
	{
		if(closed)
			throw new RuntimeException("Connection closed.");

		// Send data message
		setClosed();
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_INPUT_INITIATOR, new byte[1], id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		sendTask(task);
	}
	
}
