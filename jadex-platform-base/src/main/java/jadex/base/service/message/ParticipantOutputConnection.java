package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Output connection on participant side.
 */
public class ParticipantOutputConnection extends AbstractConnection implements IOutputConnection
{
	/**
	 *  Create a new connection.
	 */
	public ParticipantOutputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports, 
		byte[] codecids, ICodec[] codecs)
	{
		super(ms, sender, receiver, id, transports, codecids, codecs);
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	// changed
	public synchronized IFuture<Void> write(byte[] data)
	{
		if(closed)
			return new Future<Void>(new RuntimeException("Connection closed."));
		
		// Send data message
		StreamSendTask task = new StreamSendTask(StreamSendTask.DATA_OUTPUT_PARTICIPANT, data, id, 
			new IComponentIdentifier[]{sender}, transports, null, null);
		return sendTask(task);
	}
	
	/**
	 *  Close the connection.
	 */
	// changed
	public synchronized void close()
	{
		if(closed)
			throw new RuntimeException("Connection closed.");

		// Send data message
		setClosed();
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_OUTPUT_PARTICIPANT, new byte[1], id, 
			new IComponentIdentifier[]{sender}, transports, null, null);
		sendTask(task);
	}
}
