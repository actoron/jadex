package jadex.platform.service.message.websockettransport;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import jadex.commons.SUtil;
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
		WebSocketConnectionServer ret = new WebSocketConnectionServer(handshake, handler, ((SocketHttpSession) handshake).getSocket());
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
		/** The socket. */
		protected Socket acceptsocket;
		
		/** The input stream. */
		protected InputStream inputstream;
		
		/**
		 *  Creates the handler.
		 */
		public WSTransportClientHandler(InputStream inputstream, Socket acceptsocket)
		{
			super(inputstream, acceptsocket);
			this.acceptsocket = acceptsocket;
			this.inputstream = inputstream;
			try
			{
				acceptsocket.setTcpNoDelay(true);
			}
			catch (SocketException e)
			{
			}
        }
		
		/**
		 *  Run.
		 */
		public void run()
		{
			OutputStream outputstream = null;
            try
            {
            	outputstream = this.acceptsocket.getOutputStream();
                TempFileManager tempFileManager = getTempFileManagerFactory().create();
                SocketHttpSession session = new SocketHttpSession(tempFileManager, inputstream, outputstream, acceptsocket);
                while (!this.acceptsocket.isClosed())
                {
                    session.execute();
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
            	SUtil.close(outputstream);
            	SUtil.close(inputstream);
            	SUtil.close(acceptsocket);
            	setAsyncRunner(asyncRunner);
                asyncRunner.closed(this);
            }
		}
	}
	
	/**
	 *  Http session containing the socket.
	 *
	 */
	protected class SocketHttpSession extends HTTPSession
	{
		/** The connection socket. */
		protected Socket socket;
		
		/**
		 *  Create the session.
		 */
		public SocketHttpSession(TempFileManager tempfilemanager, InputStream inputstream, OutputStream outputstream, Socket socket)
		{
			super(tempfilemanager, inputstream, outputstream, socket.getInetAddress());
			this.socket = socket;
		}
		
		/**
		 *  Gets the socket.
		 *  
		 *  @return The socket.
		 */
		public Socket getSocket()
		{
			return socket;
		}
	}
}
