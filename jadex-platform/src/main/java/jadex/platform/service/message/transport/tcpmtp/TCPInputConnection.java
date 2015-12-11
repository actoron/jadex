package jadex.platform.service.message.transport.tcpmtp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import jadex.commons.SUtil;

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
	
	//-------- constructors --------

	/**
	 *  Create a new tcp input connection.
	 *  @param sock The client socket.
	 */
	public TCPInputConnection(Socket sock) throws IOException
	{
		this.sock = sock;
//		sock.setReceiveBufferSize(sock.getReceiveBufferSize()<<2);
		
		// Send handshake byte.
		sock.getOutputStream().write(new byte[1]);
		
		this.is = new BufferedInputStream(sock.getInputStream());
	}
	
	//-------- methods --------
	
	/**
	 *  Receive a message from a socket.
	 *  @param accept The socket.
	 */
	public byte[]	read() throws IOException
	{
		byte[]	ret	= null;
		int	msg_size = SUtil.bytesToInt(new byte[]{(byte)readByte(), (byte)readByte(), (byte)readByte(), (byte)readByte()});
		if(msg_size>0)
		{
			ret	= new byte[msg_size];
			int count = 0;
			while(count<msg_size) 
			{
				int bytes_read = is.read(ret, count, msg_size-count);
				if(bytes_read==-1) 
					throw new IOException("Stream closed");
				count += bytes_read;
			}
		}
//		else
//		{
//			System.out.println("0-message");
//		}
		
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
