package jadex.platform.service.message.websockettransport;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransport;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Transport implementing communication using web sockets.
 *
 */
public class WebSocketTransport implements ITransport<IWebSocketConnection>
{
	/** Connection handler. */
	protected ITransportHandler<IWebSocketConnection> handler;
	
	/** The server for incoming connections. */
	protected WebSocketServer server;
	
	/**
	 *  Initialize the transport.
	 *  To be called once, before any other method.
	 *  @param handler 	The transport handler with callback methods. 
	 */
	public void	init(ITransportHandler<IWebSocketConnection> handler)
	{
		this.handler = handler;
	}
	
	/**
	 *  Shutdown and free all resources.
	 *  To be called once, as last method. 
	 */
	public void	shutdown()
	{
		try
		{
			if (server != null)
			{
				server.closeAllConnections();
				server.stop();
			}
		}
		catch (Exception e)
		{
		}
	}

	/**
	 *  Get the protocol name.
	 */
	public String	getProtocolName()
	{
		return "ws";
	}
	
	/**
	 *  Open a port to accept connections.
	 *  @param port	The (positive) port or 0 for any port.
	 *  @return A future holding the actual port, once the server is running.
	 */
	public IFuture<Integer>	openPort(int port)
	{
		final Future<Integer>	ret	= new Future<Integer>();
		System.out.println("Starting with port " + port);
		if(port<0)
		{
			ret.setException(new IllegalArgumentException("Port must be greater or equal to zero: "+port));
		}
		else
		{
			try
			{
				server = new WebSocketServer(port, handler);
				server.start();
				System.out.println("Started websocket server: " + server.getListeningPort());
				ret.setResult(server.getListeningPort());
			}
			catch (Exception e)
			{
				ret.setException(e);
			}
		}
		return ret;
	}

	/**
	 *  Create a connection to a given address.
	 *  @param address	The target platform's address.
	 *  @param target	The target identifier to maybe perform authentication of the connection.
	 *  @return A future containing the connection when succeeded.
	 */
	public IFuture<IWebSocketConnection> createConnection(String address, final IComponentIdentifier target)
	{
		WebSocketConnectionClient con = new WebSocketConnectionClient(address, target, handler);
		return con.connect();
	}
	
	/**
	 *  Perform close operations on a connection.
	 *  Potentially cleans up key attachments as well.
	 */
	public void closeConnection(IWebSocketConnection con)
	{
		con.close();
	}

	/**
	 *  Send bytes using the given connection.
	 *  @param sc	The connection.
	 *  @param header	The message header.
	 *  @param body	The message body.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(IWebSocketConnection con, byte[] header, byte[] body)
	{
		return con.sendMessage(header, body);
	}
}
