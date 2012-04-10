package jadex.bridge.service.types.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ServiceOutputConnection implements IOutputConnection
{
	/** The remote output connection. */
	protected IOutputConnection ocon;

	/** The closed flag. */
	protected boolean closed;
	
	/** Flushed flag. */
	protected boolean flushed;
	
	/** The buffer. */
	protected List<byte[]> buffer;
	
	/** The ready future. */
	protected Future<Void> readyfuture;
	
	/**
	 * 
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
		if(ocon!=null)
		{
			ocon.write(data).addResultListener(new DelegationResultListener<Void>(ret));
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
		if(ocon!=null)
		{
			ocon.flush();
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
	public IFuture<Void> waitForReady()
	{
		Future<Void> ret = new Future<Void>();
		
		if(ocon!=null)
		{
			ocon.waitForReady().addResultListener(new DelegationResultListener<Void>(ret));
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
			if(ocon!=null)
				ocon.close();
		}
	}
	
	/**
	 * 
	 */
	public int getConnectionId()
	{
		if(ocon!=null)
			return ocon.getConnectionId();
		else
			throw new RuntimeException("Uninitialized connection.");
	}

	/**
	 * 
	 */
	public IComponentIdentifier getInitiator()
	{
		if(ocon!=null)
			return ocon.getInitiator();
		else
			throw new RuntimeException("Uninitialized connection.");
	}

	/**
	 * 
	 */
	public IComponentIdentifier getParticipant()
	{
		if(ocon!=null)
			return ocon.getParticipant();
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
	 * 
	 */
	protected void setOutputConnection(IOutputConnection ocon)
	{
		if(this.ocon!=null)
			throw new RuntimeException("Connection already set.");
		
		this.ocon = ocon;
		
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
			ocon.waitForReady().addResultListener(new DelegationResultListener<Void>(readyfuture));
		}
		
		if(closed)
		{
			ocon.close();
		}
	}
}