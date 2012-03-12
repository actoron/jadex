package jadex.base.service.message;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

/**
 *  Input connection that is created on initiator side.
 */
public class InitiatorInputConnection extends AbstractInputConnection implements IInputConnection
{
	/**
	 *  Create a new input connection.
	 */
	public InitiatorInputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports,
		byte[] codecids, ICodec[] codecs)
	{
		super(ms, sender, receiver, id, transports, codecids, codecs);

		// Send init message to establish stream
		StreamSendTask task = new StreamSendTask(StreamSendTask.INIT_OUTPUT_INITIATOR, 
			new IComponentIdentifier[]{sender, receiver}, id, 
			new IComponentIdentifier[]{receiver}, transports, codecids, codecs);
		sendTask(task);
	}
	
	/**
	 *  Close the stream.
	 *  Notifies the initiator that the stream has been closed.
	 */
	public void close()
	{
		// Send closed message
		setClosed();
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_OUTPUT_INITIATOR, new byte[1], id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		sendTask(task);
	}
}
