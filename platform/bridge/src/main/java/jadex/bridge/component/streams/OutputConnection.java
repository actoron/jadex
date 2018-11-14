package jadex.bridge.component.streams;

import java.io.InputStream;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

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
				
				waitForReady().addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Integer>()
				{
					public void resultAvailable(Integer bytes)
					{
						// Stop transfer on cancel etc.
						if(ret.isDone())
						{
							finished(null);
						}
						else
						{
							try
							{
								int size = Math.min(bytes.intValue(), is.available());
								if(size>0)
								{
									if(buf==null || buf.length!=size)
									{
										buf = new byte[size];
									}
									int read = is.read(buf, 0, buf.length);
									dataRead(read);
								}
								else
								{
									Future<Integer>	read	= new Future<Integer>();
									asyncBlockingRead(read);
									
									read.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Integer>()
									{
										public void resultAvailable(Integer read)
										{
											dataRead(read.intValue());
										}
										
										public void exceptionOccurred(Exception exception)
										{
											finished(exception);
										}
									}));
								}
							}
							catch(Exception e)
							{
								finished(e);
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						finished(exception);
					}
					
					/**
					 *  Called on end of transmission.
					 */
					protected void	finished(Exception ex)
					{
						buf	= null;
						close();
						if(ex!=null)
						{
							ret.setExceptionIfUndone(ex);
						}
						else
						{
							ret.setFinishedIfUndone();
						}
						
						try
						{
							is.close();
						}
						catch(Exception e)
						{
						}
					}
					
					/**
					 *  Called, when read from input stream returned.
					 */
					protected void	dataRead(int read)
					{
						if(read==-1)
						{
							finished(null);							
						}
						else
						{
							// Maybe less bytes read than buffer size;
							assert read<=buf.length;
							if(read<buf.length)
							{
								byte[]	tmp	= new byte[read];
								System.arraycopy(buf, 0, tmp, 0, read);
								buf	= tmp;
							}

							write(buf);
							filesize[0]	+= read;
//							System.out.println("wrote: "+filesize[0]);
							ret.addIntermediateResultIfUndone(Long.valueOf(filesize[0]));
						
							waitForReady().addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Integer>()
							{
								public void resultAvailable(Integer result)
								{
									component.scheduleStep(self);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									finished(exception);		
								}
							}));
						}
					}
					
					IDaemonThreadPoolService	dtps;
					
					/**
					 *  Perform blocking read on extra thread.
					 */
					protected void	asyncBlockingRead(final Future<Integer> read)
					{
						if(dtps==null)
						{
							component.searchService( new ServiceQuery<>(IDaemonThreadPoolService.class))
								.addResultListener(new ExceptionDelegationResultListener<IDaemonThreadPoolService, Integer>(read)
							{
								public void customResultAvailable(IDaemonThreadPoolService result)
								{
									dtps	= result;
									asyncBlockingRead(read);
								}
							});
						}
						else
						{
							dtps.execute(new Runnable()
							{
								public void run()
								{
									try
									{
										if(buf==null)
										{
											buf	= new byte[256];
										}
										int	len	= is.read(buf);
										read.setResult(Integer.valueOf(len));
									}
									catch(Exception e)
									{
										read.setException(e);
									}
								}
							});
						}
					}
				}));

				return IFuture.DONE;
			}
		});
		
		return ret;
	}
}
