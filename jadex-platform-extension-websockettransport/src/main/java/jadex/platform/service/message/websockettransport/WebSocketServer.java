package jadex.platform.service.message.websockettransport;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
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
	
	/** Hack! Map used to recover the server connection sockets from their input streams */
	protected Map<InputStream, Socket> socketrecoverymap = Collections.synchronizedMap(new HashMap<InputStream, Socket>());
	
	/** 
	 *  Creates the server.
	 *  @param port Port of the server.
	 */
	public WebSocketServer(int port, ITransportHandler<IWebSocketConnection> hndler)
	{
		super(port);
		Logger.getLogger(NanoHTTPD.class.getName()).setLevel(Level.OFF);
		Logger.getLogger(NanoWSD.class.getName()).setLevel(Level.OFF);
		this.handler = hndler;
		setAsyncRunner(new AsyncRunner()
		{
			/** Active sockets. */
			protected List<Socket> sockets = Collections.synchronizedList(new ArrayList<Socket>());
			
			/**
			 *  Executes.
			 */
			public void exec(ClientHandler code)
			{
				try
				{
					Field f = ClientHandler.class.getDeclaredField("acceptSocket");
					f.setAccessible(true);
					Socket socket = (Socket) f.get(code);
					sockets.add(socket);
				}
				catch (Exception e)
				{
				}
				((WebSocketTransportAgent) handler).getThreadPoolService().execute(code);
			}
			
			/**
			 *  Can this be implemented at all using thread pools?
			 */
			public void closed(ClientHandler clientHandler)
			{
				try
				{
					Field f = ClientHandler.class.getDeclaredField("acceptSocket");
					f.setAccessible(true);
					Socket socket = (Socket) f.get(clientHandler);
					sockets.remove(socket);
				}
				catch (Exception e)
				{
				}
			}
			
			/**
			 *  Can this be implemented at all using thread pools?
			 */
			public void closeAll()
			{
				synchronized(sockets)
				{
					while (sockets.size() > 0)
					{
						Socket socket = sockets.remove(sockets.size() - 1);
						try
						{
							socket.close();
						}
						catch (Exception e)
						{
						}
					}
				}
			}
		});
	}
	
	/**
	 *  Opens a web socket.
	 */
	protected WebSocket openWebSocket(IHTTPSession handshake)
	{
		WebSocketConnectionServer ret = new WebSocketConnectionServer(handshake, handler, socketrecoverymap.remove(handshake.getInputStream()));
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
			socketrecoverymap.put(inputstream, acceptsocket);
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
