package jadex.base.service.message;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInputConnection;
import jadex.commons.future.IFuture;

/**
 * 
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
	
	/**
	 *  Create a new input connection.
	 */
	public InputConnection(int id)
	{
		this.id = id;
		this.data = new ArrayList<byte[]>();
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
	 * 
	 */
	public synchronized void close()
	{
		// todo:
	}
	
	//-------- methods called from message service --------
	
	/**
	 *  Add data to the internal data buffer.
	 *  @param data The data to add.
	 */
	public synchronized void addData(byte[] data)
	{
		this.data.add(data);
	}
	
	/**
	 *  Set the stream to be closed.
	 */
	public synchronized void setClosed()
	{
		this.closed = true;
	}
}
