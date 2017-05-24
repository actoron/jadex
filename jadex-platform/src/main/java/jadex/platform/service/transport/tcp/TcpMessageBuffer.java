package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;


/**
 *  The message buffer hold state about a partially received message
 *  until header and body are complete.
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
	
	/** The header. */
	protected byte[] header;
	
	/** The body. */
	protected byte[] body;
	
	/** The header pos. */
	protected int header_pos;
	
	/** The header pos. */
	protected int body_pos;

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
	 * Read a message from the channel.
	 * 
	 * @return True, if a the message is complete.
	 * @throws Exception on read error.
	 */
	public Tuple2<byte[], byte[]>	read(SocketChannel sc) throws IOException
	{
		Tuple2<byte[], byte[]>	ret = null;

		// Write data from channel into the buffer.
		if(sc.read(wb) == -1)
		{
			throw new IOException("Channel closed: "+sc.socket().getInetAddress()+":"+sc.socket().getPort());
		}

		// First try to determine the header/body size if unknown (array==null)
		// Read next msg header
		// Need at least 4 size bytes
		
		if(header==null || header_pos==header.length && body==null)
		{
			if(wb.position()-rb.position()>=4)
			{
				byte[]	bytes	= new byte[4];
				bytes[0]	= rb.get();
				bytes[1]	= rb.get();
				bytes[2]	= rb.get();
				bytes[3]	= rb.get();
				int	len = SUtil.bytesToInt(bytes);
				if(header==null)
				{
					header	= new byte[len];
				}
				else
				{
					body	= new byte[len];
				}
			}
		}

		// Read out the buffer if enough data has been retrieved for the header or buffer is full.
		if(header!=null && header_pos<header.length)
		{
			int required	= header.length-header_pos;
			int	available	= wb.position()-rb.position();
			
			// Read till end of header
			if(available >= required)
			{
				rb.get(header, header_pos, required);
				header_pos	= header.length;
			}
			
			// Read full buffer when full and only part of message
			else if(wb.remaining()==0)
			{
				rb.get(header, header_pos, available);
				header_pos	+= available;
				rb.clear();
				wb.clear();
			}
		}
		
		// Read out the buffer if enough data has been retrieved for the body or buffer is full.
		if(body!=null && body_pos<body.length)
		{
			int required	= body.length-body_pos;
			int	available	= wb.position()-rb.position();
			
			// Read till end of body
			if(available >= required)
			{
				rb.get(body, body_pos, required);
				body_pos	= body.length;
			}
			
			// Read full buffer when full and only part of message
			else if(wb.remaining()==0)
			{
				rb.get(body, body_pos, available);
				body_pos	+= available;
				rb.clear();
				wb.clear();
			}
		}
		
		// Finished when bosy is complete.
		if(body!=null && body_pos==body.length)
		{
			ret	= new Tuple2<byte[], byte[]>(header, body);
				
			// Reset the readbuffer and compact (i.e. copy rest) the writebuffer
			wb.limit(wb.position());
//			int pos = (msg_len+prolog_len)%wb.capacity();
			wb.position(rb.position());
			wb.compact();
			rb.clear();
				
			header	= null;
			body	= null;
			header_pos = 0;
			body_pos = 0;
		}

		return ret;
	}
}