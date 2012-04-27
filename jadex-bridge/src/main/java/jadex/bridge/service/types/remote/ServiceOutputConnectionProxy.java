package jadex.bridge.service.types.remote;

import java.io.InputStream;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 * 
 */
public class ServiceOutputConnectionProxy implements IOutputConnection
{
	/** The original connection. */
	protected ServiceInputConnection con;
	
	/** The connection id. */
	protected int conid;
	
	/**
	 * 
	 */
	public ServiceOutputConnectionProxy()
	{
		// Bean constructor.
	}
	
	/**
	 * 
	 */
	public ServiceOutputConnectionProxy(ServiceInputConnection con)
	{
		this.con = con;
	}
	
	/**
	 * 
	 */
	public void setInputConnection(IInputConnection icon)
	{
		con.setInputConnection(icon);
	}
	
	/**
	 *  Get the connectionid.
	 *  @return The connectionid.
	 */
	public int getConnectionId()
	{
		return conid;
	}

	/**
	 *  Set the connectionid.
	 *  @param connectionid The connectionid to set.
	 */
	public void setConnectionId(int conid)
	{
		this.conid = conid;
	}
	
	
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data)
	{
		return new Future<Void>(new UnsupportedOperationException());
	}
	
	/**
	 *  Flush the data.
	 */
	public void flush()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Integer> waitForReady()
	{
		return new Future<Integer>(new UnsupportedOperationException());
	}
	
	/**
	 *  Close the connection.
	 */
	// todo: make IFuture<Void> ?
	public void close()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	public IComponentIdentifier getInitiator()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	public IComponentIdentifier getParticipant()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Do write all data from the input stream.  
	 */
	public ITerminableIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component)
	{
		throw new UnsupportedOperationException();
	}
}
