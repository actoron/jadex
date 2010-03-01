package jadex.standalone.transport.tcpmtp;

import jadex.standalone.transport.MessageEnvelope;
import jadex.standalone.transport.codecs.CodecFactory;
import jadex.standalone.transport.codecs.IDecoder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *  Represents the input connection for a tcp stream.  
 */
public class TCPInputConnection
{
	//-------- constants ---------
	
	/** 2MB as message buffer */
	static final int BUFFER_SIZE = 1024 * 1024 * 2;
	
	//-------- attributes --------
	
	/** The client socket. */
	protected Socket sock;
	
	/** The input stream. */
	protected InputStream is;
	
	/** The buffer. */
	protected byte[] buffer;
	
	/** The codec factory. */
	protected CodecFactory codecfac;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	//-------- constructors --------

	/**
	 *  Create a new tcp input connection.
	 *  @param sock The client socket.
	 */
	public TCPInputConnection(Socket sock, CodecFactory codecfac, ClassLoader classloader) throws IOException
	{
		this.sock = sock;
		this.codecfac = codecfac;
		this.classloader = classloader;
		this.buffer = new byte[BUFFER_SIZE];
		this.is = new BufferedInputStream(sock.getInputStream());
	}
	
	//-------- methods --------
	
	/**
	 *  Receive a message from a socket.
	 *  @param accept The socket.
	 */
	public MessageEnvelope read() throws IOException
	{
		MessageEnvelope ret = null;
			
		// Calculate message size by reading the first 4 bytes
		// Read here is always a blocking call.
		int msg_size;
		byte codec_id = (byte)readByte();
		msg_size = readByte() << 24 | readByte() << 16 | readByte() << 8 | readByte();
		msg_size -= TCPTransport.PROLOG_SIZE; // Remove prolog.
		if(msg_size <= buffer.length && msg_size>0)
		{
			int count = 0;
			while(count<msg_size) 
			{
				int bytes_read = is.read(buffer, count, msg_size-count);
				if(bytes_read==-1) 
					throw new IOException("Stream closed");
				count += bytes_read;
			}
					
			byte[] rawmsg = new byte[count];
			System.arraycopy(buffer, 0, rawmsg, 0, count);
			IDecoder dec = codecfac.getDecoder(codec_id);
			ret = (MessageEnvelope)dec.decode(rawmsg, classloader);
		}
		
		return ret;
	}
	
	/**
	 *  Close the connection.
	 */
	public void close()
	{
		try
		{
			sock.close();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
	}
	
	/**
	 *  Read a byte from the stream.
	 *  @return A fresh read byte.
	 */
	protected int readByte() throws IOException
	{
		int ret = is.read();
		if(ret==-1)
			throw new IOException("No data available.");
		return ret;
	}
}
