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
	 *  @return Calls future when next data can be written. Provides a value of how much data should be given to the connection for best performance.
	 */
	public IFuture<Integer> waitForReady()
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
		
		component.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				waitForReady().addResultListener(ia.createResultListener(new IResultListener<Integer>()
				{
					long filesize = 0;
					
					public void resultAvailable(Integer bytes)
					{
						// Stop transfer on cancel etc.
						if(ret.isDone())
						{
							close();
							try
							{
								is.close();
							}
							catch(Exception e)
							{
							}
						}
						else
						{
							try
							{
								int size = Math.min(bytes.intValue(), is.available());
								filesize += size;
								byte[] buf = new byte[size];
								int read = 0;
								// Hack!!! Should only read once, as subsequent reads might block, because available() only provides an estimate
								while(read!=buf.length)
								{
									read += is.read(buf, read, buf.length-read);
								}
								write(buf);
	//							System.out.println("wrote: "+size);
								
								ret.addIntermediateResultIfUndone(new Long(filesize));
								
								// Hack!!! Should not assume that stream is at end, only if currently no bytes are available
								if(is.available()>0)
								{
									waitForReady().addResultListener(ia.createResultListener(this));
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
								close();
								ret.setExceptionIfUndone(e);
								try
								{
									is.close();
								}
								catch(Exception ex)
								{
								}							
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						close();
						ret.setExceptionIfUndone(exception);
						try
						{
							is.close();
						}
						catch(Exception e)
						{
						}
					}
				}));

				return IFuture.DONE;
			}
		});
		
		return ret;
	}
}
