package jadex.extension.rs.publish;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.activecomponents.webservice.AbstractWebSocketServer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import jadex.bridge.IInternalAccess;

/**
 *  The websocket impl for jetty.
 *  
 *  Should be able to use the RestWebSocket class, but jetty complains that
 *  the websocket has to implement (jetty) WebsocketListener or use (jetty) @WebSocket annotion.
 */
@WebSocket
public class JettyWebsocketServer
{	
	/** The websocket server functionality. */
	protected AbstractWebSocketServer server;

	/** The websockets per session. */
	protected Map<Session, Map<String, Object>> props;
	
	/**
	 *  Create a new rest websocket.
	 */
	public JettyWebsocketServer(IInternalAccess agent)
	{
		this.props = new HashMap<>();
		this.server = new AbstractWebSocketServer(agent) 
		{
			@Override
			public void sendWebSocketData(Object ws, String data) 
			{
				try
				{
					synchronized(ws)
					{
						Session session = (Session)ws;
						session.getRemote().sendString(data);
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
				Session session = (Session)ws;
				return getUserProperties(session);
			}
		};
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session)
	{
		System.out.println("Websocket session started: "+session);
	}
	
	@OnWebSocketMessage
	public void onText(Session session, String message)
	{
		server.onMessage(session, message);
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int status, String reason)
	{
		server.onClose(session);
	}
	
	@OnWebSocketError
	public void onError(Throwable error)
	{
		System.out.println("Websocket error: "+error);
	}
	
	public Map<String, Object> getUserProperties(Session session)
	{
		Map<String, Object> ret = props.get(session); 
		if(ret==null)
		{
			ret = new HashMap<>();
			props.put(session, ret);
		}
		return  ret;
	}
}
