package jadex.base.service.message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Output connection for writing data.
 *  
 *  Must synchronized its internal data because the connection handler
 *  and the connection user (i.e. a component) are using the connection
 *  concurrently.
 *  
 *  - the user calls interface methods like write and flush
 *  - the connection handler calls close to signal that the connection should close.
 */
public class OutputConnection extends AbstractConnection implements IOutputConnection
{		
	//-------- constructors --------
	
	/**
	 *  Create a new connection.
	 */
	public OutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, 
		int id, boolean initiator, OutputConnectionHandler ch)
	{
		super(sender, receiver, id, false, initiator, ch);
	}
	
	//-------- IOutputConnection methods --------

	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data)
	{
		synchronized(this)
		{
			if(closing || closed)
				return new Future<Void>(new RuntimeException("Connection closed."));
		}
		return ((OutputConnectionHandler)ch).send(data);
	}
	
	/**
	 *  Flush the data.
	 */
	public void flush()
	{
		synchronized(this)
		{
			if(closing || closed)
				return;
		}
		
		((OutputConnectionHandler)ch).flush();
	}
	
	/**
	 *  Close the connection.
	 *  Notifies the other side that the connection has been closed.
	 */
	public void close()
	{
		synchronized(this)
		{
			if(closing || closed)
				return;
		}
		
		flush();
		
		super.close();
	}
}
