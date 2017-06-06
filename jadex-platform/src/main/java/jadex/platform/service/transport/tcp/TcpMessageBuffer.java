package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jadex.commons.SUtil;


/**
 *  The message buffer hold state about a partially received message
 *  until the data is complete.
 */
public class TcpMessageBuffer
{
	// -------- constants ---------

	/** 10 kB as message buffer */
	static final int BUFFER_SIZE	= 1024 * 10;

	//-------- attributes ---------

	/** The write buffer for the channel. */
	protected ByteBuffer wb;

	/** The read buffer for reading out the messages. */
	protected ByteBuffer rb;
	
	/** The data buffer to be filled while reading. */
	protected byte[] data;
	
	/** The amount of data already read. */
	protected int pos;

	//-------- constructors --------

	/**
	 *  Create a message buffer.
	 */
	public TcpMessageBuffer()
	{
		this.wb = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.rb = wb.asReadOnlyBuffer();
	}

	// -------- methods --------

	/**
	 *  Read data from the channel.
	 *  Always reads the length as 4 byte int and then reads the required amount of data into an array. 
	 * 
	 *  @return A byte array, when the next data is complete, null as long as data is still pending.
	 *  @throws Exception on read error.
	 */
	public byte[]	read(SocketChannel sc) throws IOException
	{
		byte[]	ret = null;

		// Transfer data from channel into the buffer.
		if(sc.read(wb) == -1)
		{
			throw new IOException("Channel closed: "+sc.socket().getInetAddress()+":"+sc.socket().getPort());
		}

		// First try to determine the data size if unknown (array==null)
		// Need at least 4 size bytes, else NOP until more bytes available.
		if(data==null && wb.position()-rb.position()>=4)
		{
			byte[]	bytes	= new byte[4];
			bytes[0]	= rb.get();
			bytes[1]	= rb.get();
			bytes[2]	= rb.get();
			bytes[3]	= rb.get();
			int	len = SUtil.bytesToInt(bytes);
			data	= new byte[len];
		}

		// Read out the buffer if enough data has been retrieved for the header or buffer is full.
		if(data!=null && pos<data.length)
		{
			int required	= data.length-pos;
			int	available	= wb.position()-rb.position();
			
			// Read till end of header
			if(available >= required)
			{
				rb.get(data, pos, required);
				pos	= data.length;
			}
			
			// Read full buffer when full and only part of message
			else if(wb.remaining()==0)
			{
				rb.get(data, pos, available);
				pos	+= available;
				rb.clear();
				wb.clear();
			}
		}
		
		// Finished when body is complete.
		if(data!=null && pos==data.length)
		{
			// Set result and reset internal structures.
			ret	= data;
			data	= null;
			pos = 0;
				
			// Reset the readbuffer and compact (i.e. copy rest) the writebuffer
			wb.limit(wb.position());
//			int pos = (msg_len+prolog_len)%wb.capacity();
			wb.position(rb.position());
			wb.compact();
			rb.clear();
		}

		return ret;
	}
}