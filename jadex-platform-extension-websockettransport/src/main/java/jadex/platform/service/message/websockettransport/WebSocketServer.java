package jadex.platform.service.message.websockettransport;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

import fi.iki.elonen.NanoWSD;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Server component of the websocket transport for incoming connections.
 *
 */
public class WebSocketServer extends NanoWSD
{
	/** The handler. */
	protected ITransportHandler<IWebSocketConnection> handler;
	
	/** 
	 *  Creates the server.
	 *  @param port Port of the server.
	 */
	public WebSocketServer(int port, ITransportHandler<IWebSocketConnection> handler)
	{
		super(port);
		this.handler = handler;
	}
	
	/**
	 *  Opens a web socket.
	 */
	protected WebSocket openWebSocket(IHTTPSession handshake)
	{
		WebSocketConnectionServer ret = new WebSocketConnectionServer(handshake, handler);
		return ret.getWebSocket();
	}
	
	/**
	 *  Overrides the creation of the client handler to disable Nagle's algorithm.
	 */
	protected ClientHandler createClientHandler(Socket finalAccept, InputStream inputStream)
	{
		return new WSTransportClientHandler(inputStream, finalAccept);
	}
	
	/**
	 *  Client handler that disables Nagle's algorithm on the accept socket.
	 *
	 */
	public class WSTransportClientHandler extends ClientHandler
	{
		/**
		 *  Creates the handler.
		 */
		public WSTransportClientHandler(InputStream inputstream, Socket acceptsocket)
		{
			super(inputstream, acceptsocket);
			try
			{
				acceptsocket.setTcpNoDelay(true);
			}
			catch (SocketException e)
			{
			}
        }
	}
}
