package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;

/**
 *  Participant implementation for an input connection.
 */
public class ParticipantInputConnection extends AbstractInputConnection implements IInputConnection
{
	/**
	 *  Create a new input connection.
	 */
	public ParticipantInputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports)
	{
		super(ms, sender, receiver, id, transports, null, null);
	}
	
	/**
	 *  Close the stream.
	 *  Notifies the initiator that the stream has been closed.
	 */
	public void close()
	{
		// Send closed message
		setClosed();
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_INPUT_PARTICIPANT, new byte[1], id, 
			new IComponentIdentifier[]{sender}, transports, null, null);
		sendTask(task);
	}
}
