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
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.commons.future.TerminableIntermediateFuture;

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
	protected ITerminableIntermediateFuture<Long> transferfuture;

	
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
	 *  Write all data from input stream to the connection.
	 *  The result is an intermediate future that reports back the size that was written.
	 *  It can also be used to terminate sending.
	 *  @param is The input stream.
	 *  @param agen
	 */
	public ITerminableIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component)
	{
		final TerminableIntermediateDelegationFuture<Long> ret = new TerminableIntermediateDelegationFuture<Long>();
		
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
			ITerminableIntermediateFuture<Long> src = doWriteFromInputStream(is, component);
			TerminableIntermediateDelegationResultListener<Long> lis = new TerminableIntermediateDelegationResultListener<Long>(ret, src);
			src.addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Do write all data from the input stream.  
	 */
	protected ITerminableIntermediateFuture<Long> doWriteFromInputStream(final InputStream is, final IExternalAccess component)
	{
		final TerminableIntermediateFuture<Long> ret = new TerminableIntermediateFuture<Long>();
		
		try
		{
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				long filesize = 0;
				
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					// Stop transfer on error etc.
					if(ret.isDone())
					{
						ocon.close();
						return IFuture.DONE;
					}
					
					try
					{
						final IComponentStep<Void> self = this;
						int size = Math.min(200000, is.available());
						filesize += size;
						byte[] buf = new byte[size];
						int read = 0;
						while(read!=buf.length)
						{
							read += is.read(buf, read, buf.length-read);
						}
						ocon.write(buf);
//						System.out.println("wrote: "+size);
						
						ret.addIntermediateResultIfUndone(new Long(filesize));
						
						if(is.available()>0)
						{
//							ia.waitForDelay(100, self);
	//						agent.scheduleStep(self);
							ocon.waitForReady().addResultListener(ia.createResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
//									ia.waitForDelay(1000, self);
									component.scheduleStep(self);
	//								agent.waitFor(10, self);
								}
								public void exceptionOccurred(Exception exception)
								{
									ocon.close();
									ret.setException(exception);
									try
									{
										is.close();
									}
									catch(Exception e)
									{
									}
								}
							}));
						}
						else
						{
							ocon.close();
							ret.setFinishedIfUndone();
							is.close();
						}
					}
					catch(Exception e)
					{
						try
						{
							is.close();
						}
						catch(Exception ex)
						{
						}
						ret.setExceptionIfUndone(e);
					}
					
					return IFuture.DONE;
				}
			};
			component.scheduleStep(step);
		}
		catch(Exception e)
		{
		}
		
		return ret;
	}
}