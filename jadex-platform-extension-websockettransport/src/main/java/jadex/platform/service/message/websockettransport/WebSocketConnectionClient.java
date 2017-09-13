package jadex.platform.service.message.websockettransport;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Client-side websocket connection.
 *
 */
public class WebSocketConnectionClient extends AWebsocketConnection
{
	/** The connection address. */
	protected String address;
	
	/** The websocket. */
	protected WebSocket websocket;
	
	/**
	 *  Creates the connection.
	 *  
	 *  @param address Target address.
	 *  @param target Target platform.
	 */
	public WebSocketConnectionClient(String address, IComponentIdentifier target, ITransportHandler<IWebSocketConnection> handler)
	{
		super(handler);
		this.address = address;
	}
	
	/**
	 *  Establishes the connection.
	 *  
	 *  @return This object when done, exception on connection error.
	 */
	public IFuture<IWebSocketConnection> connect()
	{
		final Future<IWebSocketConnection> ret = new Future<IWebSocketConnection>();
		try
		{
			WebSocketFactory factory = new WebSocketFactory(); //.setConnectionTimeout(5000);
			websocket = factory.createSocket("ws://" + address);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
			return ret;
		}
		
		websocket.addListener(new WebSocketAdapter()
		{
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
			{
				try
				{
					websocket.getSocket().setTcpNoDelay(false);
				}
				catch (SocketException e)
				{
				}
				ret.setResult(WebSocketConnectionClient.this);
			}
			
			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
			{
//				System.out.println("RecClient: " + Arrays.hashCode(binary) + " " + System.currentTimeMillis());
				handleFramePayload(binary);
			}
			
			public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception
		    {
				exception.printStackTrace();
				ret.setException(exception);
		    }
			
			public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
				handler.connectionClosed(WebSocketConnectionClient.this, null);
			}
		});
		
		websocket.connectAsynchronously();
		
		return ret;
	}
	
	/**
	 *  Send bytes using the connection.
	 *  @param header The message header.
	 *  @param body The message body.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(byte[] header, byte[] body)
	{
//		System.out.println("SendClient: " + Arrays.hashCode(body) + " " + System.currentTimeMillis());
		Future<Void> ret = new Future<Void>();
		try
		{
			websocket.sendBinary(header);
			websocket.sendBinary(body);
//			websocket.sendBinary(SUtil.mergeData(header, body));
			websocket.flush();
			ret.setResult(null);
		}
		catch (Exception e)
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
		if (websocket != null)
		{
			websocket.disconnect();
		}
	}
}
