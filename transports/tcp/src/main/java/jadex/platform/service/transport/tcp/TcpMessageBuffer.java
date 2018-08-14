package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;


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
	
	/** The header buffer to be filled while reading. */
	protected byte[] header;
	
	/** The body buffer to be filled while reading. */
	protected byte[] body;
	
	/** The amount of data already read in the current array. */
	protected int pos;
	
	/** Maximum message size. */
	protected int maxsize;

	//-------- constructors --------

	/**
	 *  Create a message buffer.
	 */
	public TcpMessageBuffer(int maxsize)
	{
		this.maxsize = maxsize;
		this.wb = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.rb = wb.asReadOnlyBuffer();
	}

	// -------- methods --------

	/**
	 *  Read messages from the channel.
	 *  Always reads the length as 4 byte int and then reads the required amount of data into an array. 
	 * 
	 *  @return Two byte arrays, when the next message is complete, null as long as data is still pending.
	 *  @throws IOException on read error.
	 */
	public Tuple2<byte[], byte[]>	read(SocketChannel sc) throws IOException
	{
		Tuple2<byte[], byte[]>	ret = null;

		// Transfer data from channel into the buffer.
		if(sc.read(wb) == -1)
		{
			throw new IOException("Channel closed: "+sc.socket().getInetAddress()+":"+sc.socket().getPort());
		}
		
		// Read into header when not yet created or not yet complete -> body not created (i.e. pos still refers to header) and pos(header)<length(header)
		if(header==null || body==null && pos<header.length)
		{
			header = readBytes(header);
		}
		
		// Read into body, when body already created or body not created but header complete
		if(body!=null || header!=null && pos==header.length)
		{
			body	= readBytes(body);
		}
		
		// Finished when body is complete.
		if(body!=null && pos==body.length)
		{
			// Set result and reset internal structures.
			ret	= new Tuple2<byte[], byte[]>(header, body);
			header	= null;
			body	= null;
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
	
	/**
	 *  Read data in to the given array, if any.
	 *  Also adjusts the pos varaiable.
	 *  @param data	The already read data, if any.
	 *  @return	The maybe updated data array.
	 * @throws IOException 
	 */
	protected byte[] readBytes(byte[] data) throws IOException
	{
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
			
			// Size sanity check.
			int remsize = maxsize;
			if (header != null)
				remsize -= header.length;
			if (len < 0 || len > remsize)
				throw new IOException("Transmitted message size outside limits: " + len);
			
			data	= new byte[len];
			pos	= 0;
		}

		// Read out the buffer if enough data has been retrieved for the header or buffer is full.
		if(data!=null && pos<data.length)
		{
			int required	= data.length-pos;
			int	available	= wb.position()-rb.position();
			
			// Read till end of the array
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
		return data;
	}
}