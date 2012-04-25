package jadex.base.service.message;

import java.io.InputStream;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

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
		int id, boolean initiator, IOutputConnectionHandler ch)
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
		return ((IOutputConnectionHandler)ch).send(data);
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
		
		((IOutputConnectionHandler)ch).flush();
	}
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Void> waitForReady()
	{
		return ((IOutputConnectionHandler)ch).waitForReady();
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
	
	/**
	 *  Do write all data from the input stream.  
	 */
	public ITerminableIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component)
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
						close();
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
						write(buf);
//						System.out.println("wrote: "+size);
						
						ret.addIntermediateResultIfUndone(new Long(filesize));
						
						if(is.available()>0)
						{
//							ia.waitForDelay(100, self);
	//						agent.scheduleStep(self);
							waitForReady().addResultListener(ia.createResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
//									ia.waitForDelay(1000, self);
									component.scheduleStep(self);
	//								agent.waitFor(10, self);
								}
								public void exceptionOccurred(Exception exception)
								{
									close();
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
							close();
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
