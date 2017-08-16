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
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Client-side websocket connection.
 *
 */
public class WebSocketConnectionClient implements IWebSocketConnection
{
	/** The connection target. */
	protected IComponentIdentifier target;
	
	/** The connection address. */
	protected String address;
	
	/** The websocket. */
	protected WebSocket websocket;
	
	/** The handler. */
	protected ITransportHandler<IWebSocketConnection> handler;
	
	/**
	 *  Creates the connection.
	 *  
	 *  @param address Target address.
	 *  @param target Target platform.
	 */
	public WebSocketConnectionClient(String address, IComponentIdentifier target, ITransportHandler<IWebSocketConnection> handler)
	{
		this.address = address;
		this.target = target;
		this.handler = handler;
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
				try
				{
					List<byte[]> splitdata = SUtil.splitData(binary);
					if (splitdata.size() != 2)
						throw new IllegalArgumentException("Invalid data detected, closing connection...");
					
					handler.messageReceived(WebSocketConnectionClient.this, splitdata.get(0), splitdata.get(1));
				}
				catch (Exception e)
				{
					handler.connectionClosed(WebSocketConnectionClient.this, e);
					websocket.disconnect();
				}
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
	 *  @param message The message.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(byte[] message)
	{
//		System.out.println("SendClient: " + Arrays.hashCode(message) + " " + System.currentTimeMillis());
		Future<Void> ret = new Future<Void>();
		try
		{
			websocket.sendBinary(message);
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
