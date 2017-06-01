package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import jadex.commons.Tuple2;
import jadex.commons.future.Future;

/**
 *  Struct to hold required information about a channel.
 */
public class TcpChannelHandler
{
	//-------- attributes --------
	
	/** The future, in case somebody wants to be informed about an established connection
	 *  (may be null for server connections). */
	protected Future<SocketChannel>	fut;
	
	/** The channel state (handshake or open). */
	protected boolean open;
	
	/** The message buffer for intermediate results of asynchronous read operations. */
	protected TcpMessageBuffer	buffer;
	
	/** The current header, if any. */
	protected byte[]	header;
	
	//-------- constructors --------
	
	/**
	 *  Create a channel info
	 *  @param fut	The connection future, if any.
	 */
	public TcpChannelHandler(Future<SocketChannel> fut)
	{
		this.fut	= fut;
	}
	
	//-------- methods --------
	
	/**
	 *  Check if the handshake is complete.
	 */
	public boolean	isOpen()
	{
		return open;
	}
	
	/**
	 *  Set after handshake is complete.
	 */
	public void	setOpen(boolean open)
	{
		this.open	= open;
	}
	
	/**
	 * Read a message from the channel.
	 * 
	 * @return True, if a the message is complete.
	 * @throws Exception on read error.
	 */
	public Tuple2<byte[], byte[]>	read(SocketChannel sc) throws IOException
	{
		Tuple2<byte[], byte[]>	ret = null;
		if(buffer==null)
		{
			buffer	= new TcpMessageBuffer();
		}
		
		// Check, if read completes the next data array.
		byte[]	data	= buffer.read(sc);
		if(data!=null)
		{
			// No header -> data is header of next message.
			if(header==null)
			{
				header	= data;
			}
			
			// Header already set -> data is body corresponding to header -> message complete -> return and reset 
			else
			{
				ret	= new Tuple2<byte[], byte[]>(header, data);
				header	= null;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the connection future, if any.
	 */
	public Future<SocketChannel>	getConnectionFuture()
	{
		return fut;
	}
}
