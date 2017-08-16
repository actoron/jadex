package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.util.List;

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
public class WebSocketConnectionServer extends WebSocket implements IWebSocketConnection
{
	/** The connection handler. */
	protected ITransportHandler<IWebSocketConnection> handler;
	
	/**
	 *  Creates the websocket.
	 */
	public WebSocketConnectionServer(IHTTPSession handshakerequest, ITransportHandler<IWebSocketConnection> handler)
	{
		super(handshakerequest);
		this.handler = handler;
	}
	
	/**
	 *  Send bytes using the connection.
	 *  @param message The message.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(byte[] message)
	{
//		System.out.println("SendServer: " + Arrays.hashCode(message) + " " + System.currentTimeMillis());
		Future<Void> ret = new Future<Void>();
		try
		{
			send(message);
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
			close(CloseCode.NormalClosure, "Disconnect", false);
		}
		catch (IOException e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}

	/**
	 *  Called on open.
	 */
	protected void onOpen()
	{
		
		handler.connectionEstablished(this);
	}

	/**
	 *  Called on close.
	 */
	protected void onClose(CloseCode code, String reason, boolean initiatedByRemote)
	{
		handler.connectionClosed(this, null);
	}

	/**
	 *  Called on message.
	 */
	protected void onMessage(WebSocketFrame message)
	{
		byte[] payload = message.getBinaryPayload();
		if (payload != null)
		{
//			System.out.println("RecServer: " + Arrays.hashCode(payload) + " " + System.currentTimeMillis());
			try
			{
				List<byte[]> splitdata = SUtil.splitData(payload);
				if (splitdata.size() != 2)
					throw new IllegalArgumentException("Invalid data detected, closing connection...");
				
				handler.messageReceived(WebSocketConnectionServer.this, splitdata.get(0), splitdata.get(1));
			}
			catch (Exception e)
			{
				handler.connectionClosed(WebSocketConnectionServer.this, e);
				try
				{
					close(CloseCode.AbnormalClosure, "", false);
				}
				catch (IOException e1)
				{
				}
			}
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
		handler.connectionClosed(this, exception);
	}
}
