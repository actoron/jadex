package jadex.platform.service.message.transport.niotcpmtp;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import jadex.commons.SReflect;
import jadex.commons.SUtil;


/**
 * The input connection (channel) for incoming requests.
 */
public class NIOTCPInputConnection	implements Closeable
{
	// -------- constants ---------

	/** 10 kB as message buffer */
	static final int BUFFER_SIZE	= 1024 * 10;

	//-------- attributes ---------

	/** The socket channel for receiving messages. */
	protected SocketChannel	sc;

	/** The write buffer for the channel. */
	protected ByteBuffer wb;

	/** The read buffer for reading out the messages. */
	protected ByteBuffer rb;

	/** The current message length (-1 for none). */
	protected int msg_len;
	
	/** The current result message. */
	protected byte[] msg;
	
	/** The msg pos. */
	protected int msg_pos;

	//-------- constrcutors --------

//	static Map<SocketChannel, NIOTCPInputConnection> icons = new HashMap<SocketChannel, NIOTCPInputConnection>();
	
	/**
	 * Constructor for InputConnection.
	 * 
	 * @param sc
	 * @param dec
	 * @throws IOException
	 */
	public NIOTCPInputConnection(SocketChannel sc)
	{
//		System.out.println("Creating input con: "+sc);
		this.sc = sc;
		this.wb = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.rb = wb.asReadOnlyBuffer();
		msg_len = -1; // No message available.
//		
//		synchronized(NIOTCPInputConnection.class)
//		{
//			icons.put(sc, this);
//			System.out.println("icons create: "+icons.size());
//		}
	}

	// -------- methods --------

	/**
	 * Read a message from the channel.
	 * 
	 * @return The message if a complete message is finished.
	 * @throws Exception on read error.
	 */
	public byte[]	read() throws IOException
	{
		byte[]	ret = null;

		// Write data from channel into the buffer.
		if(sc.read(wb) == -1)
		{
			throw new IOException("Channel closed: "+sc.socket().getInetAddress()+":"+sc.socket().getPort());
		}

		// First try to determine the message size if unknown (-1)
		// Read next msg header
		// Need at least 4 size bytes
		
		if(msg_len==-1)
		{
			if(wb.position()-rb.position()>=4)
			{
				byte[]	bytes	= new byte[4];
				bytes[0]	= rb.get();
				bytes[1]	= rb.get();
				bytes[2]	= rb.get();
				bytes[3]	= rb.get();
				msg_len = SUtil.bytesToInt(bytes);
				msg = new byte[msg_len];
				
//				if(msg_len==0)
//				{
//					System.out.println("0-message");					
//					throw new IOException("Connection closed: "+sc.socket().getInetAddress()+":"+sc.socket().getPort());
//				}
			}
		}

		// Read out the buffer if enough data has been retrieved for the message.
		if(msg_len!=-1)
		{
			// Read till end of message
			if(msg_len <= msg_pos+wb.position()-rb.position())
			{
				int len	= msg_len-msg_pos;
				rb.get(msg, msg_pos, len);
				msg_pos	= msg_len;
			}
			// Read full buffer when full and part of message
			else if(wb.remaining()==0)
			{
				int len = wb.capacity()-rb.position();
				rb.get(msg, msg_pos, len);
				msg_pos	+= len;
				rb.clear();
				wb.clear();
			}
			
			if(msg_pos==msg_len)
			{
				ret = msg;
				
				// Reset the readbuffer and compact (i.e. copy rest) the writebuffer
				wb.limit(wb.position());
//				int pos = (msg_len+prolog_len)%wb.capacity();
				wb.position(rb.position());
				wb.compact();
				rb.clear();
				
				msg = null;
				msg_pos = 0;
				msg_len = -1;
			}
		}

		return ret;
	}
	
	/**
	 * Close the connection.
	 */
	public void close()
	{
//		synchronized(NIOTCPInputConnection.class)
//		{
////			Object old = icons.remove(sc);
//			System.out.println("icons rem: "+icons.size());
//		}
		
		try
		{
//			System.out.println("Closing icon: "+sc);
//			sc.socket().close();
			sc.close();
			sc = null;
//			sc.socket().close();
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
	}

	/**
	 *  A string representation.
	 */
	public String toString()
	{
		try
		{
			return SReflect.getUnqualifiedClassName(getClass())+"("+sc.socket()+")";
		}
		catch(Exception e)
		{
			return SReflect.getUnqualifiedClassName(getClass());
		}
	}
}