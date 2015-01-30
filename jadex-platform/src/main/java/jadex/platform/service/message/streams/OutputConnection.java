package jadex.platform.service.message.streams;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.io.InputStream;

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
	public ISubscriptionIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component)
	{		
		final SubscriptionIntermediateFuture<Long> ret = new SubscriptionIntermediateFuture<Long>();
		
		final long[] filesize = new long[1];

		component.scheduleStep(new IComponentStep<Void>()
		{
			byte[]	buf	= null;
			
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				final IComponentStep<Void> self = this;
				
				waitForReady().addResultListener(ia.createResultListener(new IResultListener<Integer>()
				{
					public void resultAvailable(Integer bytes)
					{
						// Stop transfer on cancel etc.
						if(ret.isDone())
						{
							buf	= null;
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
								filesize[0] += size;
								if(buf==null || buf.length!=size)
								{
									buf = new byte[size];
								}
								int read = 0;
								// Hack!!! Should only read once, as subsequent reads might block, because available() only provides an estimate
								while(read!=buf.length)
								{
									read += is.read(buf, read, buf.length-read);
								}
								write(buf);
//								System.out.println("wrote: "+filesize[0]);
								
								ret.addIntermediateResultIfUndone(Long.valueOf(filesize[0]));
								
								// Hack!!! Should not assume that stream is at end, only if currently no bytes are available
								if(is.available()>0)
								{
//									final IResultListener<Integer> lis = this;
//									ia.waitForDelay(100, new IComponentStep<Void>()
//									{
//										public IFuture<Void> execute(IInternalAccess ia)
//										{
//											waitForReady().addResultListener(ia.createResultListener(lis));
//											return IFuture.DONE;
//										}
//									});
									
									// Cannot use simple version below, because this will lead to
									// a non-ending loop of calls in local case (same platform, two components)
									// (ia.createResultListener() does not help as is on right thread)
//									waitForReady().addResultListener(ia.createResultListener(this));
									waitForReady().addResultListener(ia.createResultListener(new IResultListener<Integer>()
									{
										public void resultAvailable(Integer result)
										{
											try
											{
												if(is.available()>0)
												{
													component.scheduleStep(self);
												}
												else
												{
													component.scheduleStep(self, 50);												
												}
											}
											catch(Exception e)
											{
												buf	= null;
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
										public void exceptionOccurred(Exception exception)
										{
										}
									}));
								}
								else
								{
									buf	= null;
									close();
									ret.setFinishedIfUndone();
									is.close();
								}
							}
							catch(Exception e)
							{
								buf	= null;
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
						buf	= null;
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
