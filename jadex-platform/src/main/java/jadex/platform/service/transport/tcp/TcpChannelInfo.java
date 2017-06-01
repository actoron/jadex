package jadex.platform.service.transport.tcp;

import java.nio.channels.SocketChannel;

import jadex.commons.future.Future;

/**
 *  Struct to hold required information about a channel.
 */
public class TcpChannelInfo
{
	//-------- attributes --------
	
	/** The channel state (handshake or open). */
	protected boolean open;
	
	/** The message buffer for intermediate results of asynchronous read operations. */
	protected TcpMessageBuffer	buffer;
	
	/** The future, in case somebody wants to be informed about an established connection
	 *  (may be null for server connections). */
	protected Future<SocketChannel>	fut;
	
	//-------- constructors --------
	
	/**
	 *  Create a channel info
	 *  @param fut	The connection future, if any.
	 */
	public TcpChannelInfo(Future<SocketChannel> fut)
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
	 *  Get the message buffer.
	 */
	public TcpMessageBuffer	getMessageBuffer()
	{
		if(buffer==null)
		{
			buffer	= new TcpMessageBuffer();
		}
		return buffer;
	}
	
	/**
	 *  Get the connection future, if any.
	 */
	public Future<SocketChannel>	getConnectionFuture()
	{
		return fut;
	}
}
