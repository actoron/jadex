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
	//-------- attributes --------
	
	/** The client socket. */
	protected Socket sock;
	
	/** The input stream. */
	protected InputStream is;
	
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
		int	read	= is.read();
		if(read!=-1)	// read==-1 when connection is closed on sender side.
		{
			byte codec_id = (byte)read;
			msg_size = readByte() << 24 | readByte() << 16 | readByte() << 8 | readByte();
			msg_size -= TCPTransport.PROLOG_SIZE; // Remove prolog.
			if(msg_size>0)
			{
				byte[] rawmsg = new byte[msg_size];
				int count = 0;
				while(count<msg_size) 
				{
					int bytes_read = is.read(rawmsg, count, msg_size-count);
					if(bytes_read==-1) 
						throw new IOException("Stream closed");
					count += bytes_read;
				}
				IDecoder dec = codecfac.getDecoder(codec_id);
				ret = (MessageEnvelope)dec.decode(rawmsg, classloader);
			}
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
