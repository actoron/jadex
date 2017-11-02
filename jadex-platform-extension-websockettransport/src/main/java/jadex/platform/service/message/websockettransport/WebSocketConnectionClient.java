package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketOpcode;
import com.neovisionaries.ws.client.WebSocketState;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.threadpool.JadexExecutorServiceAdapter;
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
	
	/** Access to daemon thread pool for WS API */
	protected JadexExecutorServiceAdapter execservice;
	
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
		execservice = new JadexExecutorServiceAdapter(pojoagent.getThreadPoolService());
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
			websocket.setAutoFlush(true);
			websocket.setPingInterval(pojoagent.getIdleTimeout() >>> 1);
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			ret.setException(e);
			return ret;
		}
//		System.out.println("Maximum payload size: " + websocket.getMaxPayloadSize());
		websocket.addListener(new WebSocketAdapter()
		{
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
			{
				try
				{
					websocket.getSocket().setTcpNoDelay(true);
				}
				catch (SocketException e)
				{
				}
//				System.out.println("Connected: " + address);
				ret.setResult(WebSocketConnectionClient.this);
			}
			
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
			{
				handler.connectionClosed(WebSocketConnectionClient.this, null);
			}
			
			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
			{
//				System.out.println("Client Binary msg size: " + (binary != null ? String.valueOf(binary.length) : "null"));
				handleMessagePayload(binary);
			}
			
			/** Message size check. */
			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
				if (frame.getOpcode() == WebSocketOpcode.TEXT)
				{
					websocket.disconnect(WebSocketCloseCode.UNACCEPTABLE);
				}
				else
				{
					if (frame.getOpcode() == WebSocketOpcode.BINARY ||
						frame.getOpcode() == WebSocketOpcode.CONTINUATION)
					{
						bytesreceived += frame.getPayload() == null ? 0 : frame.getPayload().length;
						
						if (bytesreceived > maxmsgsize)
						{
							websocket.disconnect(WebSocketCloseCode.OVERSIZE);
						}
						else
						{
							if (frame.getFin())
								bytesreceived = 0;
						}
					}
				}
			}
			
			public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception
		    {
//				exception.printStackTrace();
				ret.setException(exception);
		    }
		});
		
//		websocket.connectAsynchronously();
		websocket.connect(execservice);
		
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
		if (!WebSocketState.OPEN.equals(websocket.getState()))
			return new Future<Void>(new IOException("Connection is not available."));
//		System.out.println("SendClient: " + Arrays.hashCode(body) + " " + System.currentTimeMillis());
		Future<Void> ret = new Future<Void>();
		try
		{
			synchronized(this)
			{
				sendAsFrames(header);
				sendAsFrames(body);
				websocket.flush();
				ret.setResult(null);
			}
		}
		catch (Exception e)
		{
			ret.setException(e);
//			e.printStackTrace();
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
			forceClose();
		}
	}
	
	/**
	 *  Forcibly closes the connection (ignored if already closed).
	 */
	public void forceClose()
	{
		try
		{
			websocket.getSocket().close();
		}
		catch (IOException e)
		{
		}
	}
	
	/**
	 *  Fragment and send data as websocket frames.
	 *  
	 *  @param data The data.
	 */
	protected void sendAsFrames(byte[] data)
	{
		if (data == null)
		{
			WebSocketFrame wsf = new WebSocketFrame();
			wsf.setOpcode(WebSocketOpcode.BINARY);
			wsf.setFin(true);
			wsf.setPayload(data);
			websocket.sendFrame(wsf);
		}
		else
		{
			int offset = 0;
			int count = data.length / pojoagent.getMaximumPayloadSize() + 1;
			
			for (int i = 0; i < count; ++i)
			{
				WebSocketFrame wsf = new WebSocketFrame();
				
				if (i == 0)
					wsf.setOpcode(WebSocketOpcode.BINARY);
				else
					wsf.setOpcode(WebSocketOpcode.CONTINUATION);
				
				if (i + 1 == count)
					wsf.setFin(true);
				else
					wsf.setFin(false);
				
				int psize = Math.min(pojoagent.getMaximumPayloadSize(), data.length - offset);
				byte[] payload = new byte[psize];
				System.arraycopy(data, offset, payload, 0, psize);
				offset += psize;
				wsf.setPayload(payload);
	//			System.out.println("Send Frame OP: " + wsf.getOpcode() + " " + wsf.getFin() + " " + offset + " " + payload.length);
				websocket.sendFrame(wsf);
			}
		}
	}
}
