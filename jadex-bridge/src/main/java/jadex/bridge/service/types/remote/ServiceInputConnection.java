package jadex.bridge.service.types.remote;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 * 
 */
public class ServiceInputConnection implements IInputConnection
{
	/** The remote output connection. */
	protected IInputConnection con;

	/** The closed flag. */
	protected boolean closed;
	
	/** The buffer. */
	protected SubscriptionIntermediateFuture<byte[]> future;
	
	/**
	 * 
	 */
	public ServiceInputConnection()
	{
	}

	
	/**
	 *  Non-blocking read. Tries to read the next byte.
	 *  @return The next byte or -1 if none is currently available.
	 */
	public int read()
	{
		return con==null? -1: con.read();
	}
	
	/**
	 *  Non-blocking read. Tries to fill the 
	 *  buffer from the stream.
	 *  @param buffer The buffer to read in.
	 *  @return The number of bytes that could be read
	 *  into the buffer.
	 */
	public int read(byte[] buffer)
	{
		return con==null? 0: con.read(buffer);
	}
	
	/**
	 *  Asynchronous read. 
	 *  @return Bytes one by one till end of stream or closed.
	 */
	public ISubscriptionIntermediateFuture<byte[]> aread()
	{
		ISubscriptionIntermediateFuture<byte[]> ret = future;
		if(ret==null)
		{
			if(con==null)
			{
				future = new SubscriptionIntermediateFuture<byte[]>();
				ret = future;
			}
			else
			{
				ret = con.aread();
			}
		}
		return ret;
	}
	
//	/**
//	 *  Asynchronous read. 
//	 *  @return Bytes one by one till end of stream or closed.
//	 */
//	public IFuture<Byte> areadNext()
//	{
//		
//	}
	
	/**
	 * 
	 */
	public int getConnectionId()
	{
		if(con!=null)
			return con.getConnectionId();
		else
			throw new RuntimeException("Uninitialized connection.");
	}
	
	/**
	 *  Close the stream.
	 */
	public void close()
	{
		if(con==null)
		{
			closed = true;
		}
		else
		{
			con.close();
		}
	}

	/**
	 * 
	 */
	public IComponentIdentifier getInitiator()
	{
		if(con!=null)
			return con.getInitiator();
		else
			throw new RuntimeException("Uninitialized connection.");
	}

	/**
	 * 
	 */
	public IComponentIdentifier getParticipant()
	{
		if(con!=null)
			return con.getParticipant();
		else
			throw new RuntimeException("Uninitialized connection.");
	}
	
	/**
	 * 
	 */
	protected void setInputConnection(IInputConnection con)
	{
		if(this.con!=null)
			throw new RuntimeException("Connection already set.");
		
		this.con = con;
		
		if(future!=null)
		{
			con.aread().addResultListener(new IntermediateDelegationResultListener<byte[]>(future));
		}
		
		if(closed)
		{
			con.close();
		}
	}
	
	/**
	 * 
	 */
	public IOutputConnection getOutputConnection()
	{
		return new ServiceOutputConnectionProxy(this);
	}
}
