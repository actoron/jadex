package jadex.base.service.message.transport.tcpmtp;

import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.commons.SUtil;

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
		byte[] codec_ids = new byte[readByte()];
		for(int i=0; i<codec_ids.length; i++)
		{
			codec_ids[i] = (byte)readByte();
		}
		
		msg_size = SUtil.bytesToInt(new byte[]{(byte)readByte(), (byte)readByte(), (byte)readByte(), (byte)readByte()});
		//readByte() << 24 | readByte() << 16 | readByte() << 8 | readByte();
//		System.out.println("reclen: "+msg_size);
		msg_size = msg_size-TCPTransport.PROLOG_SIZE-codec_ids.length-1; // Remove prolog.
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
			
			Object tmp = rawmsg;
			for(int i=codec_ids.length-1; i>-1; i--)
			{
				ICodec dec = codecfac.getCodec(codec_ids[i]);
				tmp = dec.decode((byte[])tmp, classloader);
			}
			ret = (MessageEnvelope)tmp;
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
