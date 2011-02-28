package jadex.base.service.message.transport.niotcpmtp;

import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.commons.SUtil;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/**
 * The input connection (channel) for incoming requests.
 */
public class NIOTCPInputConnection
{
	// -------- constants ---------

	/** 2MB as message buffer */
	static final int BUFFER_SIZE	= 1024 * 10;

	// -------- attributes ---------

	/** The socket channel for receiving messages. */
	protected SocketChannel	sc;

	/** The write buffer for the channel. */
	protected ByteBuffer wb;

	/** The read buffer for reading out the messages. */
	protected ByteBuffer rb;

	/** The prolog_len. */
	protected int prolog_len = -1;
	
	/** The current message length (-1 for none). */
	protected int msg_len;
	
	/** The current result message. */
	protected byte[] msg;
	
	/** The msg pos. */
	protected int msg_pos;

	/** The codec ids. */
	protected byte codec_ids[];

	/** The codec factory. */
	protected CodecFactory codecfac;

	/** The classloader. */
	protected ClassLoader classloader;

	// -------- constrcutors --------

	/**
	 * Constructor for InputConnection.
	 * 
	 * @param sc
	 * @param dec
	 * @throws IOException
	 */
	public NIOTCPInputConnection(SocketChannel sc, CodecFactory codecfac,
		ClassLoader classloader)
	{
		// System.out.println("Creating input con: "+sc);
		this.sc = sc;
		this.codecfac = codecfac;
		this.classloader = classloader;
		this.wb = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.rb = wb.asReadOnlyBuffer();
		msg_len = -1; // No message available.
		codec_ids = null; // No codecs
	}

	// -------- methods --------

	/**
	 * Read a message from the channel.
	 * 
	 * @return The message if a complete message is finished.
	 * @throws Exception on read error.
	 */
	public MessageEnvelope read() throws IOException
	{
		MessageEnvelope ret = null;

		// Write data from channel into the buffer.
		if(sc.read(wb) == -1)
			throw new IOException("Channel closed: "+sc.socket().getInetAddress()+":"+sc.socket().getPort());

		// First try to determine the message size if unknown (-1)
		// Read next msg header
		// Need at least 4 size bytes
		
		if(msg_len == -1)
		{
			if(codec_ids==null && wb.position()-rb.position()>1)
			{
				byte num = rb.get();
				codec_ids = new byte[num];
			}
			
			if(codec_ids!=null && wb.position()-rb.position()>=codec_ids.length)
			{
				for(int i=0; i<codec_ids.length; i++)
					codec_ids[i] = rb.get();
				prolog_len = 1+codec_ids.length+NIOTCPTransport.PROLOG_SIZE;
			}
		
			if(prolog_len>0 && wb.position()-rb.position()>=prolog_len)
			{
				byte[]	bytes	= new byte[4];
				bytes[0]	= rb.get();
				bytes[1]	= rb.get();
				bytes[2]	= rb.get();
				bytes[3]	= rb.get();
				msg_len = SUtil.bytesToInt(bytes)-prolog_len;
//				System.out.println("len: "+msg_len);
				msg = new byte[msg_len];
				
	//			if(msg_end > wb.limit())
	//				throw new RuntimeException("Buffer overflow: " + msg_end + ">" + wb.limit());
				if(msg_len <= 0)
					throw new BufferUnderflowException();
			}
		}

		// Read out the buffer if enough data has been retrieved for the message.
		if(msg_len != -1)
		{
			boolean first = msg_pos==0;
			// Read till end of message
			if(msg_len <= msg_pos+wb.position()-(first? prolog_len: 0))
			{
				int len = msg_len-msg_pos;
//				rb.limit(len+(first? NIOTCPTransport.PROLOG_SIZE: 0));
//				System.out.println("last: "+len);
				rb.get(msg, msg_pos, len);
				msg_pos=msg_len;
			}
			// Read full buffer when full and part of message
			else if(wb.remaining()==0)
			{
				int len = wb.capacity()-rb.position();
				rb.get(msg, msg_pos, len);
				msg_pos+=len;
				rb.clear();
				wb.clear();
			}
			
			if(msg_pos==msg_len)
			{
				Object tmp = msg;
				for(int i=codec_ids.length-1; i>-1; i--)
				{
					ICodec dec = codecfac.getCodec(codec_ids[i]);
					tmp = dec.decode((byte[])tmp, classloader);
				}
				ret = (MessageEnvelope)tmp;
				
				// Reset the readbuffer and compact (i.e. copy rest) the writebuffer
				wb.limit(wb.position());
				int pos = msg_len%wb.capacity()+prolog_len;
				wb.position(pos);
				wb.compact();
				rb.clear();
				codec_ids = null;
				
				msg = null;
				msg_pos = 0;
				msg_len = -1;
				prolog_len = -1;
			}
		}

		return ret;
	}
	
	/**
	 * Close the connection.
	 */
	public void close()
	{
		try
		{
			sc.close();
		}
		catch(IOException e)
		{
			// e.printStackTrace();
		}
	}
}