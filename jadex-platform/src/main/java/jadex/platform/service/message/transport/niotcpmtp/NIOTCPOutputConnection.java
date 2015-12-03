package jadex.platform.service.message.transport.niotcpmtp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import jadex.platform.service.message.transport.niotcpmtp.SelectorThread.Cleaner;

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
	
//	static Map<SocketChannel, NIOTCPOutputConnection> ocons = new HashMap<SocketChannel, NIOTCPOutputConnection>();
//	static int crecnt = 0;
//	static int clocnt = 0;
	
	/**
	 *  Create a new output connection.
	 */
	public NIOTCPOutputConnection(SocketChannel sc, InetSocketAddress address, Cleaner cleaner)
	{
		this.sc	= sc;
		this.address	= address;
		this.cleaner	= cleaner;
		
//		synchronized(NIOTCPInputConnection.class)
//		{
////			ocons.put(sc, this);
//			System.out.println("ocons create: "+(++crecnt)+" "+clocnt+" "+(crecnt-clocnt));
//		}
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
//		synchronized(NIOTCPInputConnection.class)
//		{
////			Object old = ocons.remove(sc);
////			System.out.println("ocons rem: "+ocons.size());
//			System.out.println("ocons closed: "+crecnt+" "+(++clocnt)+" "+(crecnt-clocnt));
//		}
		
//		System.out.println("Shutdown: "+sc.socket());
		try
		{
//			sc.socket().close();
			sc.close();
			sc = null;
//			sc.socket().close();
//			sc.socket().shutdownInput();
//			sc.socket().shutdownOutput();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		cleaner.remove();
	}
}
