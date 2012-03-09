package jadex.base.service.message;

import jadex.base.service.message.MessageService.SendManager;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Participant implementation for an input connection.
 */
public class InputConnection implements IInputConnection
{
	/** Boolean flag if connection is closed. */
	protected boolean closed;
	
	/** The connection id. */
	protected int id;
	
	/** The data. */
	protected List<byte[]> data;
	
	/** The position. */
	protected int position;
	
	/** The message service. */
	protected MessageService ms;
	
	/** The sender. */
	protected IComponentIdentifier sender;

	/** The receiver. */
	protected IComponentIdentifier receiver;
	
	/** The transports. */
	protected ITransport[] transports;
	
	/** The connection map. */
	protected Map<Integer, Object> cons;
	
	/** The read future. */
	protected IntermediateFuture<Byte> future;
	
	/**
	 *  Create a new input connection.
	 */
	public InputConnection(MessageService ms, IComponentIdentifier sender, 
		IComponentIdentifier receiver, int id, ITransport[] transports,
		Map<Integer, Object> cons)
	{
		this.ms = ms;
		this.sender = sender;
		this.receiver = receiver;
		this.id = id;
		this.transports = transports;
		this.data = new ArrayList<byte[]>();
		this.cons = cons;
		cons.put(id, this);
	}
	
	/**
	 *  Non-blocking read. Tries to fill the 
	 *  buffer from the stream.
	 *  @param buffer The buffer to read in.
	 *  @return The number of bytes that could be read
	 *  into the buffer.
	 */
	public synchronized int read(byte[] buffer)
	{
		if(future!=null)
			throw new RuntimeException("Stream has asynchronous reader");

		int startpos = 0;
		int rowcnt = 0;
		for(; rowcnt<data.size(); rowcnt++)
		{
			byte[] row = data.get(rowcnt);
			if(position<startpos+row.length)
				break;
			startpos += row.length;
		}
		
		int buffercnt = 0;
		if(rowcnt<data.size())
		{
			byte[] row = data.get(rowcnt);
			int inrowstart = position-startpos;
			
			for(; buffercnt<buffer.length;)
			{
				buffer[buffercnt++] = row[inrowstart];
				inrowstart++;
				if(inrowstart>=row.length)
				{
					inrowstart = 0;
					if(++rowcnt<data.size())
					{
						row = data.get(rowcnt);
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
	public synchronized int read()
	{
//		if(future!=null)
//			throw new RuntimeException("Stream has asynchronous reader");

		int startpos = 0;
		int rowcnt = 0;
		for(; rowcnt<data.size(); rowcnt++)
		{
			byte[] row = data.get(rowcnt);
			if(position<startpos+row.length)
				break;
			startpos += row.length;
		}
		
		int ret = -1;
		if(rowcnt<data.size())
		{
			byte[] row = data.get(rowcnt);
			int inrowstart = position-startpos;
			
			ret = row[inrowstart];
			position++;
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
			for(int val=read(); val!=-1; )
			{
				future.addIntermediateResult(new Byte((byte)val));
			}
			// end of current stream reached
			if(cl)
			{
				future.setException(new RuntimeException("Stream closed"));
			}
		}
		catch(Exception e)
		{
			future.setException(e);
		}
		
		return future;
	}

	
	/**
	 * 
	 */
	public void close()
	{
		// Send data message
		setClosed();
		SendManager sm = ms.getSendManager(sender);
		StreamSendTask task = new StreamSendTask(StreamSendTask.CLOSE_INPUT_PARTICIPANT, new byte[1], id, 
			new IComponentIdentifier[]{sender}, transports, null, null);
		sm.addMessage(task); 
	}
	
	//-------- methods called from message service --------
	
	/**
	 *  Add data to the internal data buffer.
	 *  @param data The data to add.
	 */
	public void addData(byte[] data)
	{
		IntermediateFuture<Byte> ret;
		boolean cl;
		synchronized(this)
		{
			this.data.add(data);
			ret = future;
			cl = closed;
		}
		
		if(ret!=null)
		{
			
			for(int i=0; i<data.length; i++)
			{
				ret.addIntermediateResult(data[i]);
			}
			if(cl)
			{
				ret.setException(new RuntimeException("Stream closed"));
			}
		}
	}
	
	/**
	 *  Set the stream to be closed.
	 */
	public void setClosed()
	{
		IntermediateFuture<Byte> ret;
		synchronized(this)
		{
			this.closed = true;
			cons.remove(id);	
			ret = future;
		}
		if(ret!=null)
			ret.setException(new RuntimeException("Stream closed"));
	}
}
