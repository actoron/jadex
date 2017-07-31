package jadex.platform.service.message.streams;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Input connection implementation for byte streams.
 *  
 *  Must synchronized its internal data because the connection handler
 *  and the connection user (i.e. a component) are using the connection
 *  concurrently.
 *  
 *  - the handler uses addData to forward received data to the connection.
 *  - the connection user calls interface methods to read data.
 */
public class InputConnection extends AbstractConnection implements IInputConnection
{
	//-------- attributes --------
	
	/** The data. */
	protected List<byte[]> data;
	
	/** The offset (startvalue of current first row). */
	protected int offset;
	
	/** The position. */
	protected int position;
	
	/** The size. */
	protected int size;
	
	/** The read futures. */
	protected SubscriptionIntermediateFuture<byte[]> ifuture;
	protected Future<Byte> ofuture;
		
	//-------- constructors --------

	/**
	 *  Create a new input connection.
	 */
	public InputConnection(ITransportComponentIdentifier sender, ITransportComponentIdentifier receiver, 
		int id, boolean initiator, IAbstractConnectionHandler ch)
	{
		super(sender, receiver, id, true, initiator, ch);
		this.data = new ArrayList<byte[]>();
		
//		System.out.println("created input con: "+id+" "+receiver.getName());
	}
	
	//-------- IInputConnection methods --------

	/**
	 *  Non-blocking read. Tries to read the next byte.
	 *  @return The next byte or -1 if the end of the stream has been reached.
	 */
	public int read()
	{
		if(ifuture!=null || ofuture!=null)
			throw new RuntimeException("Stream has asynchronous reader");
		return internalRead();
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
		if(ifuture!=null || ofuture!=null)
			throw new RuntimeException("Stream has asynchronous reader");

		int startpos = offset;

		int buffercnt = 0;
		if(data.size()>0)
		{
			byte[] row = data.get(0);
			int inrowstart = position-startpos;
			
			for(; buffercnt<buffer.length;)
			{
				buffer[buffercnt++] = row[inrowstart];
				inrowstart++;
				if(inrowstart>=row.length)
				{
					inrowstart = 0;
					offset += row.length;
					data.remove(0);
					if(data.size()>0)
					{
						row = data.get(0);
					}
					else
					{
						break;
					}
				}
			}
			position += buffercnt;
		}
		else if(closed)
		{
			throw new RuntimeException("End of stream reached.");
		}
		
		dataRead();
		return buffercnt;
	}
	
	/**
	 *  Non-blocking read. Tries to read the next byte.
	 *  @return The next byte or -1 if none is currently available.
	 *  @throws exception if end of stream has been reached.
	 */
	protected synchronized int internalRead()
	{
		int startpos = offset;
		
		int ret = -1;
		if(data.size()>0)
		{
			byte[] row = data.get(0);
			int inrowstart = position-startpos;
			
			ret = row[inrowstart];
			ret = ret & 0xff;
			position++;
			if(inrowstart+1==row.length)
			{
				offset += row.length;
				data.remove(0);
			}
		}
		else if(closed)
		{
//			System.out.println("iread closss");
			throw new RuntimeException("End of stream reached.");
		}
		
//		System.out.println("iread: "+ret+" "+position+" "+size+" "+hasread);
		
		dataRead();
		return ret;
	}
	
	/**
	 *  Get the next data byte array.
	 */
	public synchronized byte[] getNextByteArray()
	{
		byte[] ret = null;
		if(data.size()>0)
		{
			ret = data.remove(0);
			if(ret!=null)
			{
				position += ret.length;
				offset += ret.length;
			}
		}
		dataRead();
		return ret;
	}
	
	/**
	 *  Asynchronous read. 
	 *  @return Bytes one by one till end of stream or closed.
	 */
	public ISubscriptionIntermediateFuture<byte[]> aread()
	{
		boolean cl;
		synchronized(this)
		{
			if(ifuture!=null || ofuture!=null)
				return new SubscriptionIntermediateFuture<byte[]>(new RuntimeException("Stream has reader"));
			ifuture = new SubscriptionIntermediateFuture<byte[]>(new ITerminationCommand()
			{
				public void terminated(Exception reason)
				{
					close();
				}
				
				public boolean checkTermination(Exception reason)
				{
					return true;
				}
			});
			cl = closed;
		}
			
		for(byte[] next=getNextByteArray(); next!=null; next=getNextByteArray())
		{
			ifuture.addIntermediateResultIfUndone(next);
		}
		if(cl && position==size)
		{
			ifuture.setFinishedIfUndone();
		}
		
		return ifuture;
	}
	
	/**
	 *  Asynchronous read. 
	 *  @return Bytes one by one till end of stream or closed.
	 */
	public IFuture<Byte> areadNext()
	{
		Future<Byte> ofut;
		synchronized(this)
		{
			if(ifuture!=null || ofuture!=null)
				return new Future<Byte>(new RuntimeException("Stream has reader"));
			ofut = new Future<Byte>();
		}
		
		try
		{
			int val=internalRead();
			if(val!=-1)
				ofut.setResult(Byte.valueOf((byte)val));
		}
		catch(Exception e)
		{
			// catch stream closed exception
			ofut.setException(new RuntimeException("Stream closed"));
		}
		
		synchronized(this)
		{
			if(!ofut.isDone())
				ofuture = ofut;
		}
			
		return ofut;
	}
	
	/**
	 *  Get the number of available bytes.
	 *  @return The number of available bytes. 
	 */
	public int available()
	{
		return getStoredDataSize();
	}
	
//	/**
//	 *  Blocking read. Read the next byte.
//	 *  @return The next byte or -1 if the end of the stream has been reached.
//	 */
//	public int bread()
//	{
//		int ret = -1;
//		
//		try
//		{
//			ret = read();
//			if(ret == -1)
//			{
//				try
//				{
//					Thread.currentThread().wait();
//				}
//				catch(Exception e)
//				{
//				}
//				try
//				{
//					ret = read();
//					if(ret == -1)
//						throw new RuntimeException("Stream read error.");
//				}
//				catch(Exception e)
//				{
//				}
//			}
//		}
//		catch(Exception e)
//		{
//		}
//		
//		return ret;
//	}

	//-------- methods called from message service --------
	
	/**
	 *  Add data to the internal data buffer.
	 *  @param data The data to add.
	 *  
	 *  If stream is closed adding data is not allowed.
	 */
	public void addData(byte[] data)
	{
//		System.out.println("added: "+data.length+" "+position+" "+size);

		Future<Byte> oret = null;
		synchronized(this)
		{
			this.data.add(data);
			this.size += data.length;

			if(ofuture!=null)
			{
				oret = ofuture;
				ofuture = null;
			}
		}
		
		if(ifuture!=null)
		{
			for(byte[] next=getNextByteArray(); next!=null; next=getNextByteArray())
			{
				ifuture.addIntermediateResultIfUndone(next);
			}
			if(closed && position==size)
				ifuture.setFinishedIfUndone();
		}
		else if(oret!=null)
		{
			oret.setResult(Byte.valueOf((byte)internalRead()));
		}
	}
	
	/**
	 *  Set the stream to be closed.
	 */
	public void setClosed()
	{
		super.setClosed();
		
		Future<Byte> oret;
		boolean cl;
		synchronized(this)
		{
			super.setClosed();
			oret = ofuture;
			cl = position==size;
		}
		
//		System.out.println("setClosed(InputCon): "+position+" "+size+" "+cl);
		
		// notify async listener if last byte has been read and stream is closed.
		if(ifuture!=null && cl)
		{
			ifuture.setFinishedIfUndone();
		}
		else if(oret!=null && cl)
		{
			oret.setException(new RuntimeException("Stream closed"));
		}
	}
	
	/**
	 *  Get the size of the stored data.
	 */
	public int getStoredDataSize()
	{
		int ret = 0;
		synchronized(this)
		{
			for(int i=0; i<data.size(); i++)
			{
				ret += data.get(i).length;
			}
		}
//		System.out.println("vvvvv stored: "+ret+" "+data.size());
		return ret;
	}
	
	/**
	 * 
	 */
	protected void dataRead()
	{
//		((IInputConnectionHandler)getConnectionHandler()).notifyDataRead();
	}
	
	/**
	 *  Read all data from output stream to the connection.
	 *  The result is an intermediate future that reports back the size that was read.
	 *  It can also be used to terminate reading.
	 *  @param is The input stream.
	 *  @param component The component.
	 */
	public ISubscriptionIntermediateFuture<Long> writeToOutputStream(final OutputStream os, final IExternalAccess component)
	{
		final SubscriptionIntermediateFuture<Long> ret = new SubscriptionIntermediateFuture<Long>();
		
		try
		{
			final long[] filesize = new long[1];
			
			component.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					final ISubscriptionIntermediateFuture<byte[]> fut = aread();

					fut.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<byte[]>()
					{
						public void resultAvailable(Collection<byte[]> result)
						{
							for(Iterator<byte[]> it=result.iterator(); it.hasNext(); )
							{
								intermediateResultAvailable(it.next());
							}
							finished();
						}
						
						public void intermediateResultAvailable(byte[] result)
						{
//							System.out.println("got: "+(filesize[0]+result.length));
							
							// Check if was aborted on receiver side
							if(ret.isDone())
							{
								fut.terminate();
								return;
							}
							
							try
							{
								os.write(result);
								filesize[0] += result.length;
								ret.addIntermediateResultIfUndone(Long.valueOf(filesize[0]));
								// todo: close con?
							}
							catch(Exception e)
							{
								ret.setExceptionIfUndone(e);
							}
						}
						
						public void finished()
						{
							try
							{
								os.close();
								ret.setFinishedIfUndone();
								// todo: close con?
							}
							catch(Exception e)
							{
								ret.setExceptionIfUndone(e);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setExceptionIfUndone(exception);
						}
					}));
					
					return IFuture.DONE;
				}
			});
		}
		catch(Exception e)
		{
			ret.setExceptionIfUndone(e);
		}
		return ret;
	}
}
