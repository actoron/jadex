package jadex.platform.service.transport.intravm;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransport;
import jadex.platform.service.transport.ITransportHandler;


/**
 *  Message transport for bisimulation (no external threads)
 *  based on intra VM passing of message data.
 */
public class IntravmTransport	implements ITransport<IntravmTransport>
{
	//-------- constants --------
	
	/** Priority of transport. */
	public static final int	PRIORITY	= 10000;
	
	/** The "ports". */
	protected static final Map<Integer, IntravmTransport>	ports	= Collections.synchronizedMap(new LinkedHashMap<>());
	
	// -------- attributes --------

	/** The transport handler, e.g. for delivering received messages. */
	protected ITransportHandler<IntravmTransport>	handler;
//	
//	/** Flag indicating the thread should be running (set to false for shutdown). */
//	protected boolean	running;
//	
//	/** Flag indicating the transport has been shut down.. */
//	protected boolean	shutdown;
//	
//	/** Maximum size a message is allowed to have (including header). */
//	protected int maxmsgsize;
//	
//	/** The NIO selector. */
//	protected Selector	selector;
//
//	/** The tasks enqueued from external threads. */
//	protected List<Runnable>	tasks;
//	
//	/** The write tasks of data waiting to be written to a connection. */
//	protected Map<SocketChannel, List<Tuple2<ByteBuffer, Future<Integer>>>>	writetasks;
//	
		
	//-------- ITransport interface --------	

	/**
	 *  Initialize the transport.
	 *  To be called once, before any other method.
	 *  @param handler 	The transport handler with callback methods. 
	 */
	public void	init(ITransportHandler<IntravmTransport> handler)
	{
		this.handler	= handler;
	}
		
	/**
	 *  Cleanup
	 */
	public void	shutdown()
	{
		ports.remove(SUtil.findKeyForValue(ports, this));
	}
	
	/**
	 *  Get the protocol name.
	 */
	public String	getProtocolName()
	{
		return "intravm";
	}

	/**
	 *  Open a server "socket".
	 */
	public IFuture<Integer>	openPort(int port)
	{
		final Future<Integer>	ret	= new Future<>();
		if(port<0)
		{
			ret.setException(new IllegalArgumentException("Port must be greater or equal to zero: "+port));
		}
		else if(port==0)
		{
			// Find free port
			while(ports.containsKey(++port));
		}
		
		if(ports.containsKey(port))
		{
			ret.setException(new IllegalArgumentException("Port already in use: "+port));
		}
		else
		{
			ports.put(port, this);
			ret.setResult(port);
		}
		
		return new Future<Integer>(port);
	}
	
	/**
	 *  Create a connection to a given address.
	 *  @param	address	The target platform's address.
	 *  @param target	The target identifier to maybe perform authentication of the connection.
	 *  @return A future containing the connection when succeeded.
	 */
	public IFuture<IntravmTransport>	createConnection(final String address, final IComponentIdentifier target)
	{
		Future<IntravmTransport>	ret	= new Future<IntravmTransport>();
		
		try
		{
			// Some scheme required for URI parsing
			URI uri = new URI(getProtocolName()+"://" + address);
			int	port	= uri.getPort();
			IntravmTransport	con	= ports.get(port);
			if(con!=null)
			{
				ret.setResult(con);
			}
			else
			{
				ret.setException(new IllegalArgumentException("No such platform: "+address));
			}
		}
		catch(Exception ex)
		{
			ret.setException(ex);
		}
		
		return ret;
	}
	
	/**
	 *  Perform close operations on a connection.
	 *  Potentially cleans up key attachments as well.
	 */
	public void closeConnection(IntravmTransport con)
	{
		// NOP.
	}
	
	/**
	 *  Send bytes using the given connection.
	 *  @param con	The connection.
	 *  @param header	The message header.
	 *  @param body	The message body.
	 *  @return	A future indicating success.
	 */
	public IFuture<Integer> sendMessage(IntravmTransport con, byte[] header, byte[] body)
	{
		con.handler.messageReceived(this, header, body);		
		return new Future<>(PRIORITY);
	}
}
