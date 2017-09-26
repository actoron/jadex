package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.util.Arrays;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoWSD.WebSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Server-side websocket implemented using nanohttpd.
 *
 */
public class WebSocketConnectionServer extends AWebsocketConnection
{
	/** The web socket. */
	protected WebSocket websocket;
	
	/**
	 *  Creates the websocket.
	 */
	public WebSocketConnectionServer(IHTTPSession handshakerequest, ITransportHandler<IWebSocketConnection> handlr)
	{
		super(handlr);
		websocket = new WebSocket(handshakerequest)
		{
			/**
			 *  Called on open.
			 */
			protected void onOpen()
			{
				
				handler.connectionEstablished(WebSocketConnectionServer.this);
			}

			/**
			 *  Called on close.
			 */
			protected void onClose(CloseCode code, String reason, boolean initiatedByRemote)
			{
				handler.connectionClosed(WebSocketConnectionServer.this, null);
			}

			/**
			 *  Called on message.
			 */
			protected void onMessage(WebSocketFrame message)
			{
				byte[] payload = null;
				try
				{
					payload = message.getBinaryPayload();
				}
				catch(Exception e)
				{
				}
				
				if (payload != null)
				{
//					System.out.println("RecServer: " + Arrays.hashCode(payload) + " " + System.currentTimeMillis());
					handleFramePayload(payload);
				}
			}
			
			/**
			 *  Called on pong.
			 */
			protected void onPong(WebSocketFrame pong)
			{
			}
			
			/**
			 *  Called on exception.
			 */
			protected void onException(IOException exception)
			{
				handler.connectionClosed(WebSocketConnectionServer.this, exception);
			}
		};
	}
	
	/**
	 *  Send bytes using the connection.
	 *  @param header The message header.
	 *  @param body The message body.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(byte[] header, byte[] body)
	{
//		System.out.println("SendServer: " + Arrays.hashCode(body) + " " + System.currentTimeMillis());
		Future<Void> ret = new Future<Void>();
		try
		{
			websocket.send(header);
			websocket.send(body);
//			websocket.send(SUtil.mergeData(header, body));
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Closes the connection (ignored if already closed).
	 */
	public void close()
	{
		try
		{
			websocket.close(CloseCode.NormalClosure, "Disconnect", false);
		}
		catch (IOException e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Returns the raw server web socket.
	 *  
	 *  @return The raw server web socket.
	 */
	public WebSocket getWebSocket()
	{
		return websocket;
	}
}
