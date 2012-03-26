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
	public OutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, 
		int id, boolean initiator, OutputConnectionHandler ch)
	{
		super(sender, receiver, id, false, initiator, ch);
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public synchronized IFuture<Void> write(byte[] data)
	{
		if(closed)
			return new Future<Void>(new RuntimeException("Connection closed."));
		return ((OutputConnectionHandler)ch).send(data);
	}
}
