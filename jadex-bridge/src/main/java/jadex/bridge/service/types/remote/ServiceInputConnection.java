package jadex.bridge.service.types.remote;


import java.io.OutputStream;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.ICommand;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

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
	
	/** The transfer future. */
	protected ITerminableIntermediateFuture<Long> transferfuture;

	/** The transfer future. */
	protected ICommand transfercommand;
	
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
	
	/**
	 *  Get the number of available bytes.
	 *  @return The number of available bytes. 
	 */
	public int available()
	{
		int ret = 0;
		if(con!=null)
		{
			ret = con.available();
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
		{
			return con.getConnectionId();
		}
		else
		{
			throw new RuntimeException("Uninitialized connection.");
		}
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
	 *  Get the non-functional properties of the connection.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
		if(con!=null)
			return con.getNonFunctionalProperties();
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
		
		if(transfercommand!=null)
		{
			transfercommand.execute(null);
		}
	}
	
	/**
	 * 
	 */
	public IOutputConnection getOutputConnection()
	{
		return new ServiceOutputConnectionProxy(this);
	}
	
	/**
	 *  Read all data from output stream to the connection.
	 *  The result is an intermediate future that reports back the size that was read.
	 *  It can also be used to terminate reading.
	 *  @param is The input stream.
	 *  @param component The component.
	 */
	public ITerminableIntermediateFuture<Long> writeToOutputStream(final OutputStream os, final IExternalAccess component)
	{
		final TerminableIntermediateDelegationFuture<Long> ret = new TerminableIntermediateDelegationFuture<Long>();
		
		if(con==null)
		{
			if(transfercommand==null)
			{
				transfercommand = new ICommand()
				{
					public void execute(Object args)
					{
						ITerminableIntermediateFuture<Long> src = con.writeToOutputStream(os, component);
						TerminableIntermediateDelegationResultListener<Long> lis = new TerminableIntermediateDelegationResultListener<Long>(ret, src);
						src.addResultListener(lis);
					}
				};
			}
			else
			{
				ret.setException(new RuntimeException("Must not be called twice."));
			}
		}
		else
		{
			ITerminableIntermediateFuture<Long> src = con.writeToOutputStream(os, component);
			TerminableIntermediateDelegationResultListener<Long> lis = new TerminableIntermediateDelegationResultListener<Long>(ret, src);
			src.addResultListener(lis);
		}
		
		return ret;
	}
		
}
