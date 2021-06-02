package org.activecomponents.webservice;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import jadex.bridge.IExternalAccess;

/**
 *  WebSocket server implementation.
 *  Opens web socket at an address and acts as bridge to Jadex.
 *  Allows for JavaScript usage of Jadex that comes close to Java programming.
 */
//@ServerEndpoint(value="/wswebapi")//, configurator=ServletAwareConfig.class)
public class RestWebSocket extends Endpoint
{
	/** The endpoint configuration. */
	protected EndpointConfig config;
	
	/** The websocket server functionality. */
	protected AbstractWebSocketServer server;

	// MUST have empty constrcutor as it is created automatically by class def from ServerEndpointConfig
	/**
	 *  Create a new rest websocket.
	 */
	public RestWebSocket()
	{
		this.server = new AbstractWebSocketServer(null) 
		{
			@Override
			public void sendWebSocketData(Object ws, String data) 
			{
				try
				{
					synchronized(ws)
					{
						Session session = (Session)ws;
						session.getBasicRemote().sendText(data);
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
				return session.getUserProperties();
			}
			
			// todo: make async?!
			@Override
			public IExternalAccess getPlatform() 
			{
				return SWebSocket.getPlatform(getServletContext()).get();
			}
		};
	}
	
	@Override
//	@OnOpen
	public void onOpen(final Session session, EndpointConfig config)
	{
		System.out.println("WebSocket opened: " + session.getId() + ", jadexsocket@"+this.hashCode());
		this.config = config;

		// todo ?
		//this.debug = getServletContext().getInitParameter("servicecall_debug")!=null?
		//	Boolean.parseBoolean(getServletContext().getInitParameter("servicecall_debug")): false;

		session.setMaxTextMessageBufferSize(8*1024);
		
		session.addMessageHandler(new MessageHandler.Whole<String>() 
		{
			@Override
 			public void onMessage(String text) 
			{
				try 
				{
					RestWebSocket.this.onMessage(text, session);
				}
				catch(IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

//	@OnMessage
	public void onMessage(final String txt, final Session session) throws IOException
	{
		System.out.println("Message received: " + txt);
//		RemoteEndpoint.Async rea = session.getAsyncRemote();
		
		server.onMessage(session, txt);
	}
	
	@Override
//	@OnClose
	public void onClose(final Session session, CloseReason reason)
	{
		System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase() + ", jadexsocket@"+this.hashCode());
		
		server.onClose(session);
	}

//	/**
//	 *  Get the http session.
//	 *  @return The http session.
//	 */
//	protected HttpSession getHttpSession()
//	{
//		return (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
//	}
	
	/**
	 *  Get the servlet context.
	 *  @return The servlet context.
	 */
	protected ServletContext getServletContext()
	{
		return (ServletContext)config.getUserProperties().get("servletcontext");
//		return (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
	}
}