package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
public class ServiceInputConnectionProxy implements IInputConnection
{
	/** The original connection. */
	protected ServiceOutputConnection con;
	
	/** The connection id. */
	protected int conid;
	
	/**
	 * 
	 */
	public ServiceInputConnectionProxy()
	{
		// Bean constructor.
	}
	
	/**
	 * 
	 */
	public ServiceInputConnectionProxy(ServiceOutputConnection con)
	{
		this.con = con;
	}
	
	/**
	 * 
	 */
	public void setOutputConnection(IOutputConnection ocon)
	{
		con.setOutputConnection(ocon);
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
	 * 
	 */
	public int read(byte[] buffer)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public int read()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
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
	 * 
	 */
	public IIntermediateFuture<Byte> aread()
	{
		throw new UnsupportedOperationException();
	}
}