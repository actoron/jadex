package jadex.platform.service.message.websockettransport;

import java.lang.reflect.Field;
import java.net.ServerSocket;

import com.neovisionaries.ws.client.WebSocketFactory;

import fi.iki.elonen.NanoHTTPD;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
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
	protected static final int PRIORITY = 500;
	
	/** Connection handler. */
	protected WebSocketTransportAgent handler;
	
	/** The server for incoming connections. */
	protected WebSocketServer server;
	
	/**
	 *  Initialize the transport.
	 *  To be called once, before any other method.
	 *  @param handler 	The transport handler with callback methods. 
	 */
	public void	init(ITransportHandler<IWebSocketConnection> handler)
	{
		this.handler = (WebSocketTransportAgent) handler;
		this.handler.setWebsocketFactory(new WebSocketFactory());
		this.handler.getWebSocketFactory().setConnectionTimeout((int) this.handler.getConnectTimeout());
		this.handler.setThreadPoolService(((IInternalRequiredServicesFeature)this.handler.getAccess().getFeature(IRequiredServicesFeature.class)).getRawService(IDaemonThreadPoolService.class));
	}
	
	/**
	 *  Shutdown and free all resources.
	 *  To be called once, as last method. 
	 */
	public void	shutdown()
	{
		if (server != null)
		{
//			System.out.println("Server shutdown start");
			try
			{
				server.closeAllConnections();
			}
			catch (Exception e)
			{
			}
//			System.out.println("Server shutdown closed all connections");
			try
			{
				server.stop();
			}
			catch (Exception e)
			{
			}
//			System.out.println("Server shutdown stopped");
			
			try
			{
				Field f = NanoHTTPD.class.getField("myServerSocket");
				f.setAccessible(true);
				ServerSocket s = (ServerSocket) f.get(server);
				if (s != null)
					s.close();
			}
			catch (Exception e)
			{
			}
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
		if(port<0)
		{
//			ret.setResult((int) (Math.random() * 40000));
			// Does this work?
			ret.setException(new IllegalArgumentException("Port must be greater or equal to zero: "+port));
		}
		else
		{
			try
			{
				WebSocketTransportAgent pojo = (WebSocketTransportAgent) handler.getAccess().getFeature(IPojoComponentFeature.class).getPojoAgent();
		 		int idletimeout = pojo.getIdleTimeout();
				server = new WebSocketServer(port, handler);
				server.start(idletimeout, true);
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
//		System.out.println("WS create connection to " + address);
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
	public IFuture<Integer> sendMessage(IWebSocketConnection con, byte[] header, byte[] body)
	{
//		System.out.println("send: " + Arrays.hashCode(header) + " " + Arrays.hashCode(body));
		return con.sendMessage(header, body);
	}
}
