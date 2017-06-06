package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;

/**
 *  Object holding required information about a channel.
 */
public class TcpChannelHandler
{
	//-------- attributes --------
	
	/** The socket channel (required). */
	protected SocketChannel	sc;
	
	/** The intended or confirmed opposite end of the connection. */
	protected IComponentIdentifier	opposite;

	/** The future, in case somebody wants to be informed about an established connection
	 *  (may be null for server connections). */
	protected Future<SocketChannel>	fut;
	
	/** The channel state (handshake or open). */
	protected boolean	open;
	
	/** The message buffer for intermediate results of asynchronous read operations. */
	// TODO: shouldn't hold message buffer while idle?
	protected TcpMessageBuffer	buffer;
	
	/** The current header, if any. */
	protected byte[]	header;
	
	//-------- constructors --------
	
	/**
	 *  Create a channel info
	 *  @param sc	The socket channel (required).
	 *  @param target	The target identifier to maybe perform authentication of the connection.
	 *  @param fut	The connection future, if any.
	 */
	public TcpChannelHandler(SocketChannel sc, IComponentIdentifier target, Future<SocketChannel> fut)
	{
		this.sc	= sc;
		this.opposite	= target;
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
	 *  @param o
	 */
	public void	handshakeComplete(String target) throws IOException
	{
		if(this.opposite==null)
		{
			this.opposite	= new BasicComponentIdentifier(target);
		}
		else if(!this.opposite.getName().equals(target))
		{
			throw new IOException("Handshake failed: Expected "+this.opposite+" but was "+target);
		}
		
		this.open	= true;
		
		if(fut!=null)
		{
			fut.setResult(sc);
			fut	= null;
		}			
	}
	
	/**
	 * Read a message from the channel.
	 * 
	 * @return True, if a the message is complete.
	 * @throws Exception on read error.
	 */
	public Tuple2<byte[], byte[]>	read() throws IOException
	{
		Tuple2<byte[], byte[]>	ret = null;
		if(buffer==null)
		{
			buffer	= new TcpMessageBuffer();
		}
		
		// Check, if read completes the next data array.
		byte[]	data	= buffer.read(sc);
		
		// No header -> data is header of next message.
		if(data!=null && header==null)
		{
			header	= data;
			
			// Check if more data is available.
			data	= buffer.read(sc);
		}
			
		// Header already set -> data is the body corresponding to the header -> message complete -> return and reset 
		if(data!=null && header!=null)
		{
			ret	= new Tuple2<byte[], byte[]>(header, data);
			header	= null;
		}
		
		return ret;
	}
	
	/**
	 *  Get the opposite.
	 */
	public IComponentIdentifier	getOpposite()
	{
		return opposite;
	}
}
