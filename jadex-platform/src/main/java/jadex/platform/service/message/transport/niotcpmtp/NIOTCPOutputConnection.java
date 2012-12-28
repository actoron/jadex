package jadex.platform.service.message.transport.niotcpmtp;

import jadex.platform.service.message.transport.niotcpmtp.SelectorThread.Cleaner;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 *  Struct for holding information about an output connection.
 */
public class NIOTCPOutputConnection	implements Closeable
{
	//-------- attributes --------
	
	/** The socket channel. */
	protected SocketChannel	sc;

	/** The address. */
	protected InetSocketAddress	address;
	
	/** The cleaner. */
	protected Cleaner	cleaner;
	
	//-------- constructors --------
	
	/**
	 *  Create a new output connection.
	 */
	public NIOTCPOutputConnection(SocketChannel sc, InetSocketAddress address, Cleaner cleaner)
	{
		this.sc	= sc;
		this.address	= address;
		this.cleaner	= cleaner;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the address.
	 */
	public InetSocketAddress getAddress()
	{
		return address;
	}
	
	/**
	 *  Get the channel.
	 */
	public SocketChannel getSocketChannel()
	{
		return sc;
	}
	
	/**
	 *  Get the cleaner;
	 */
	public Cleaner getCleaner()
	{
		return cleaner;
	}

	
	/**
	 *  Close the connection.
	 */
	public void close() throws IOException
	{
//		System.out.println("Shutdown: "+sc.socket());
		sc.close();
		sc.socket().shutdownOutput();
		cleaner.remove();
	}
}
