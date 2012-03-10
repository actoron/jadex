package jadex.base.service.message;

import java.util.ArrayList;
import java.util.List;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

/**
 * 
 */
public class InitiatorInputConnection extends AbstractConnection implements IInputConnection
{
	/** The data. */
	protected List<byte[]> data;
	
	/** The offset (startvalue of current first row). */
	protected int offset;
	
	/** The position. */
	protected int position;
	
	/** The size. */
	protected int size;
	
	/** The read future. */
	protected IntermediateFuture<Byte> future;
	
	/**
	 *  Create a new input connection.
	 */
	// changed
	public InitiatorInputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports,
		byte[] codecids, ICodec[] codecs)
	{
		super(ms, sender, receiver, id, transports, codecids, codecs);
		this.data = new ArrayList<byte[]>();

		// Send init message
		StreamSendTask task = new StreamSendTask(StreamSendTask.INIT_OUTPUT_INITIATOR, 
			new IComponentIdentifier[]{sender, receiver}, id, 
			new IComponentIdentifier[]{receiver}, transports, codecids, codecs);
		sendTask(task);
	}
	
	/**
	 *  Non-blocking read. Tries to read the next byte.
	 *  @return The next byte or -1 if the end of the stream has been reached.
	 */
	public int read()
	{
		if(future!=null)
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
		if(future!=null)
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
			if(future!=null)
				return new IntermediateFuture<Byte>(new RuntimeException("Stream has reader"));
			future = new IntermediateFuture<Byte>();
			cl = closed;
		}
			
		try
		{
			try
			{
				for(int val=internalRead(); val!=-1; )
				{
					future.addIntermediateResult(new Byte((byte)val));
				}
			}
			catch(Exception e)
			{
				// catch stream closed exception
			}
			if(cl && position==size)
			{
				future.setFinished();
//				future.setException(new RuntimeException("Stream closed"));
			}
		}
		catch(Exception e)
		{
			future.setException(e);
		}
		
		return future;
	}

	/**
	 *  Close the stream.
	 *  Notifies the initiator that the stream has been closed.
	 */
	// changed
	public void close()
	{
		// Send closed message
		setClosed();
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_OUTPUT_INITIATOR, new byte[1], id, 
			new IComponentIdentifier[]{receiver}, transports, null, null);
		sendTask(task);
	}
	
	//-------- methods called from message service --------
	
	/**
	 *  Add data to the internal data buffer.
	 *  @param data The data to add.
	 */
	public void addData(byte[] data)
	{
		try
		{
		IntermediateFuture<Byte> ret;
		boolean cl;
		synchronized(this)
		{
			if(future!=null)
			{
				this.data.add(data);
			}
			else
			{
				offset += data.length;
			}
			this.size += data.length;
			ret = future;
			cl = closed;
		}
		
		if(ret!=null)
		{
			position += data.length;
			for(int i=0; i<data.length; i++)
			{
				ret.addIntermediateResult(data[i]);
			}
			
			if(cl && position==size)
			{
				ret.setFinished();
//				ret.setException(new RuntimeException("Stream closed"));
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Set the stream to be closed.
	 */
	public void setClosed()
	{
		IntermediateFuture<Byte> ret;
		boolean cl;
		synchronized(this)
		{
			super.setClosed();
			ret = future;
			cl = position == size;
		}
		// notify async listener if last byte has been read and stream is closed.
		if(ret!=null && cl)
		{
			ret.setFinished();
//			ret.setException(new RuntimeException("Stream closed"));
		}
	}
}
