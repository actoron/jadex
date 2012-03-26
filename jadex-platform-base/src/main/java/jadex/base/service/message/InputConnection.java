package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class InputConnection extends AbstractConnection implements IInputConnection
{
	/** The data. */
	protected List<byte[]> data;
	
	/** The offset (startvalue of current first row). */
	protected int offset;
	
	/** The position. */
	protected int position;
	
	/** The size. */
	protected int size;
	
	/** The read futures. */
	protected IntermediateFuture<Byte> ifuture;
	protected Future<Byte> ofuture;
		
	/**
	 *  Create a new input connection.
	 */
	public InputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, 
		int id, boolean initiator, AbstractConnectionHandler ch)
	{
		super(sender, receiver, id, true, initiator, ch);
		this.data = new ArrayList<byte[]>();
	}
	
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

//		for(; rowcnt<data.size(); rowcnt++)
//		{
//			byte[] row = data.get(rowcnt);
//			if(position<startpos+row.length)
//				break;
//			startpos += row.length;
//		}
		
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
		
//		for(; rowcnt<data.size(); rowcnt++)
//		{
//			byte[] row = data.get(rowcnt);
//			if(position<startpos+row.length)
//				break;
//			startpos += row.length;
//		}
		
		int ret = -1;
		if(data.size()>0)
		{
			byte[] row = data.get(0);
			int inrowstart = position-startpos;
			
			ret = row[inrowstart];
			position++;
			if(inrowstart+1==row.length)
			{
				offset += row.length;
				data.remove(0);
			}
		}
		else if(closed)
		{
			throw new RuntimeException("End of stream reached.");
		}
		
		return ret;
	}
	
	/**
	 *  Asynchronous read. 
	 *  @return Bytes one by one till end of stream or closed.
	 */
	public IIntermediateFuture<Byte> aread()
	{
		boolean cl;
		synchronized(this)
		{
			if(ifuture!=null || ofuture!=null)
				return new IntermediateFuture<Byte>(new RuntimeException("Stream has reader"));
			ifuture = new IntermediateFuture<Byte>();
			cl = closed;
		}
			
		try
		{
			try
			{
				for(int val=internalRead(); val!=-1; )
				{
					ifuture.addIntermediateResult(new Byte((byte)val));
				}
			}
			catch(Exception e)
			{
				// catch stream closed exception
			}
			if(cl && position==size)
			{
				ifuture.setFinished();
//				future.setException(new RuntimeException("Stream closed"));
			}
		}
		catch(Exception e)
		{
			ifuture.setException(e);
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
				ofut.setResult(new Byte((byte)val));
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
//		System.out.println("added: "+data.length);
		IntermediateFuture<Byte> iret;
		Future<Byte> oret = null;
		boolean cl;
		synchronized(this)
		{
			if(ifuture==null)
			{
				this.data.add(data);
			}
			else
			{
				offset += data.length;
			}
			
			this.size += data.length;
			iret = ifuture;
			if(ofuture!=null)
			{
				oret = ofuture;
				ofuture = null;
			}
			cl = closed;
		}
		
		if(iret!=null)
		{
			position += data.length;
			for(int i=0; i<data.length; i++)
			{
				iret.addIntermediateResult(data[i]);
			}
			
			if(cl && position==size)
			{
				iret.setFinished();
//				ret.setException(new RuntimeException("Stream closed"));
			}
		}
		else if(oret!=null)
		{
			oret.setResult(new Byte((byte)internalRead()));
		}
	}
	
	/**
	 *  Set the stream to be closed.
	 */
	public void setClosed()
	{
		IntermediateFuture<Byte> iret;
		Future<Byte> oret;
		boolean cl;
		synchronized(this)
		{
			super.setClosed();
			iret = ifuture;
			oret = ofuture;
			cl = position == size;
		}
		// notify async listener if last byte has been read and stream is closed.
		if(iret!=null && cl)
		{
			iret.setFinished();
//			ret.setException(new RuntimeException("Stream closed"));
		}
		else if(oret!=null && cl)
		{
			oret.setException(new RuntimeException("Stream closed"));
		}
	}
}
