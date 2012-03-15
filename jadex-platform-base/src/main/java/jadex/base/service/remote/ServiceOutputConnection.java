package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;

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
	
	/** The buffer. */
	protected List<byte[]> buffer;
	
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
	
	public int getConnectionId()
	{
		if(ocon!=null)
			return ocon.getConnectionId();
		else
			throw new RuntimeException("Uninitialized connection.");
	}

	public IComponentIdentifier getInitiator()
	{
		if(ocon!=null)
			return ocon.getInitiator();
		else
			throw new RuntimeException("Uninitialized connection.");
	}

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
		
		if(closed)
		{
			ocon.close();
		}
	}
}