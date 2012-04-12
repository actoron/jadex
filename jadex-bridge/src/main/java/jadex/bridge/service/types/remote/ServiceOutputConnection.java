package jadex.bridge.service.types.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  A service output connection can be used to write data to a remote input connection.
 *  For this purpose getInputConnection() has to be called and transferred to the
 *  remote side.
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
	
	/** The transfer future. */
	protected ITerminableFuture<Void> transferfuture;

	
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
	 *  Set the real output connection to the other side.
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
	
	/**
	 * 
	 */
	public ITerminableFuture<Void> writeFromInputStream(final InputStream is, final IExternalAccess agent)
	{
		final TerminableFuture<Void> ret = new TerminableFuture<Void>();
		
		if(ocon==null)
		{
			if(transferfuture==null)
			{
				transferfuture = ret;
			}
			else
			{
				ret.setException(new RuntimeException("Must not be called twice."));
			}
		}
		else
		{
			doWriteFromInputStream(is, agent).addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> doWriteFromInputStream(final InputStream is, final IExternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			final long[] cnt = new long[1];
				
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Stop transfer on stop
					if(ret.isDone())
					{
						ocon.close();
						return IFuture.DONE;
					}
					
					try
					{
						final IComponentStep<Void> self = this;
						int size = Math.min(200000, is.available());
						cnt[0] += size;
						byte[] buf = new byte[size];
						int read = 0;
						while(read!=buf.length)
						{
							read += is.read(buf, read, buf.length-read);
						}
						ocon.write(buf);
//						System.out.println("wrote: "+size);
						
//						fi.setState(FileInfo.TRANSFERRING);
//						fi.setDone(cnt[0]);
//						updateUpload(fi);
						
						if(is.available()>0)
						{
							ia.waitForDelay(1000, self);
	//						agent.scheduleStep(self);
	//						ocon.waitForReady().addResultListener(new IResultListener<Void>()
	//						{
	//							public void resultAvailable(Void result)
	//							{
	//								agent.scheduleStep(self);
	////												agent.waitFor(10, self);
	//							}
	//							public void exceptionOccurred(Exception exception)
	//							{
	//								exception.printStackTrace();
	//								ocon.close();
	//							}
	//						});
						}
						else
						{
							ocon.close();
//							fi.setState(FileInfo.COMPLETED);
//							updateUpload(fi);
							ret.setResult(null);
						}
					}
					catch(Exception e)
					{
//						fi.setState(FileInfo.ERROR);
//						updateUpload(fi);
						e.printStackTrace();
						ret.setException(e);
					}
					
					return IFuture.DONE;
				}
			};
			agent.scheduleStep(step);
		
		}
		catch(Exception e)
		{
			ret.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
}