package jadex.extension.rs.publish;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.activecomponents.webservice.AbstractWebSocketServer;

import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
import jadex.bridge.IExternalAccess;

/**
 *  The nano websocket server handles websocket requests from clients like browsers.
 */
public class NanoWebsocketServer extends NanoHttpServer
{
	/** The websocket server functionality. */
	protected AbstractWebSocketServer server;
	
	/** The websockets per session. */
	protected Map<IHTTPSession, MyWebSocket> websockets;
	
	/** 
	 *  Creates the server.
	 *  @param port Port of the server.
	 */
	public NanoWebsocketServer(int port, IExternalAccess agent, IRequestHandlerService handler)
	{
		super(port, handler);
		//Logger.getLogger(NanoHTTPD.class.getName()).setLevel(Level.OFF);
		//Logger.getLogger(NanoWSD.class.getName()).setLevel(Level.OFF);
		
		this.server = new AbstractWebSocketServer(port, agent) 
		{
			@Override
			public void sendWebSocketData(Object ws, String data) 
			{
				try
				{
					// Do we need this?
					synchronized(ws)
					{
						IHTTPSession session = (IHTTPSession)ws;
						getWebSocket(session).send(data);
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public Map<String, Object> getSessionProperties(Object ws) 
			{
				IHTTPSession session = (IHTTPSession)ws;
				return websockets.get(session).getProperties();
			}
		};
		
		this.websockets = Collections.synchronizedMap(new HashMap<>());
	}
	
	@Override
    public void start() throws IOException 
	{
        super.start();
        
        ScheduledExecutorService exes = Executors.newSingleThreadScheduledExecutor();
//        ScheduledExecutorService exes = Executors.newSingleThreadScheduledExecutor(Void -> 
//        {
//        	Thread t = new Thread();
//        	t.setDaemon(true);
//        	return t;
//        });
        
        final ScheduledFuture<?>[] f =new ScheduledFuture[1];
        f[0] = exes.scheduleAtFixedRate(() -> 
        {
            if(isAlive())
            {
            	// Hack! https://www.bountysource.com/issues/44957864-websocket-closes-after-being-open
                MyWebSocket[] wss; 
                // values() delivers internal collection that is not synchronized
                synchronized(websockets)
                {
                	wss = websockets.values().toArray(new MyWebSocket[0]);
                }
                
                for(MyWebSocket ws : wss)
                {
                    try
                    { 
                    	//System.out.println("sending ping: "+ws.getHandshakeRequest().getUri());
                    	ws.ping("ping".getBytes()); 
                    }
                    catch(Exception e)
                    { 
                    	// todo: remove
                    	//websockets.remove
                    }
                }
            }
            else
            {
            	if(f[0]!=null)
            		f[0].cancel(false);
            }
        }, 4, 4, TimeUnit.SECONDS); // todo: time is defined where?
    }
	
	/**
	 *  Opens a web socket.
	 */
	protected WebSocket openWebSocket(IHTTPSession session)
	{
		MyWebSocket ws = new MyWebSocket(session);
		websockets.put(session, ws);
		return ws;
	}
	
	/**
	 *  Get websocket per session.
	 *  @param session The session.
	 *  @return The socket.
	 */
	protected MyWebSocket getWebSocket(IHTTPSession session)
	{
		MyWebSocket ws = websockets.get(session);
		if(ws!=null)
			return ws;
		else
			throw new RuntimeException("No websocket found for: "+session);		
	}
	
	/**
	 *  Websocket impl that delegates calls to the server.
	 */
	class MyWebSocket extends WebSocket
	{
		protected IHTTPSession session;
		
		protected Map<String, Object> properties;
		
		public MyWebSocket(IHTTPSession session)
		{
			super(session);
			this.session = session;
			this.properties = new HashMap<>();
		}
		
		public Map<String, Object> getProperties()
		{
			return properties;
		}

		@Override
		protected void onOpen()
		{
			System.out.println("WebSocket opened: "+", jadexsocket@"+this.hashCode());
		}
		
		@Override
		protected void onMessage(WebSocketFrame message)
		{
			String txt = message.getTextPayload();
			onMessage(txt);
		}
		
		protected void onMessage(String txt)
		{
			System.out.println("Message received: " + txt);
//			RemoteEndpoint.Async rea = session.getAsyncRemote();
			
			server.onMessage(session, txt);
		}
		
		@Override
		protected void onClose(CloseCode code, String reason, boolean initiatedByRemote)
		{
			System.out.println("Closing a WebSocket due to " + reason + ", jadexsocket@"+this.hashCode());
			
			server.onClose(session);
			
			websockets.remove(session);
		}
		
		@Override
		protected void onException(IOException exception)
		{
			System.out.println("onExeption: "+exception);
		}
		
		@Override
		protected void onPong(WebSocketFrame pong)
		{
			//System.out.println("onPong: "+pong.getTextPayload());
		}
	};
}
