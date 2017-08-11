package jadex.bridge.service.types.remote;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.ICommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

/**
 *  A service output connection can be used to write data to a remote input connection.
 *  For this purpose getInputConnection() has to be called and transferred to the
 *  remote side.
 */
public class ServiceOutputConnection implements IOutputConnection
{
	/** The remote output connection. */
	protected IOutputConnection con;

	/** The closed flag. */
	protected boolean closed;
	
	/** Flushed flag. */
	protected boolean flushed;
	
	/** The buffer. */
	protected List<byte[]> buffer;
	
	/** The ready future. */
	protected Future<Integer> readyfuture;
	
	/** The transfer future. */
	protected ICommand transfercommand;

	/**
	 *  Create a new connection.
	 */
	public ServiceOutputConnection()
	{
		this.buffer = new ArrayList<byte[]>();
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data)
	{
		Future<Void> ret = new Future<Void>();
		if(con!=null)
		{
			con.write(data).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			buffer.add(data);
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Flush the data.
	 */
	public void flush()
	{
		if(con!=null)
		{
			con.flush();
		}
		else
		{
			flushed = true;
		}
	}
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Integer> waitForReady()
	{
		Future<Integer> ret = new Future<Integer>();
		
		if(con!=null)
		{
			con.waitForReady().addResultListener(new DelegationResultListener<Integer>(ret));
		}
		else if(readyfuture==null)
		{
			readyfuture = ret;
		}
		else
		{
			ret.setException(new RuntimeException("Must not be called twice without waiting for result."));
		}
		
		return ret;
	}
	
	/**
	 *  Close the connection.
	 */
	public void close()
	{
		if(!closed)
		{
			closed = true;
			if(con!=null)
				con.close();
		}
	}
	
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
	public IInputConnection getInputConnection()
	{
		return new ServiceInputConnectionProxy(this);
	
//		LocalInputConnectionHandler ich = new LocalInputConnectionHandler();
//		LocalOutputConnectionHandler och = new LocalOutputConnectionHandler(ich);
//		ich.setConnectionHandler(och);
//
//		InputConnection icon = new InputConnection(null, null, sicp.getConnectionId(), false, ich);
//		OutputConnection ocon = new OutputConnection(null, null, sicp.getConnectionId(), true, och);
	}
	
	/**
	 *  Set the real output connection to the other side.
	 */
	protected void setOutputConnection(IOutputConnection ocon)
	{
		if(this.con!=null)
			throw new RuntimeException("Connection already set.");
		
		this.con = ocon;
		
		while(buffer.size()>0)
		{
			byte[] data = buffer.remove(0);
			ocon.write(data);
		}
		
		if(flushed)
		{
			ocon.flush();
		}
		
		if(readyfuture!=null)
		{
			ocon.waitForReady().addResultListener(new DelegationResultListener<Integer>(readyfuture));
		}
		
		if(closed)
		{
			ocon.close();
		}
		
		if(transfercommand!=null)
		{
			transfercommand.execute(null);
		}
	}
	
	/**
	 *  Write all data from input stream to the connection.
	 *  The result is an intermediate future that reports back the size that was written.
	 *  It can also be used to terminate sending.
	 *  @param is The input stream.
	 *  @param agen
	 */
	public ISubscriptionIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component)
	{
		final SubscriptionIntermediateDelegationFuture<Long> ret = new SubscriptionIntermediateDelegationFuture<Long>();
		
		if(con==null)
		{
			if(transfercommand==null)
			{
				transfercommand = new ICommand()
				{
					public void execute(Object args)
					{
						ISubscriptionIntermediateFuture<Long> src = con.writeFromInputStream(is, component);
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
			ISubscriptionIntermediateFuture<Long> src = con.writeFromInputStream(is, component);
			TerminableIntermediateDelegationResultListener<Long> lis = new TerminableIntermediateDelegationResultListener<Long>(ret, src);
			src.addResultListener(lis);
		}
		
		return ret;
	}
	
	
}