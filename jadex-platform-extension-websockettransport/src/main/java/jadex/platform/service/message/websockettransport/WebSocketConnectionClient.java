package jadex.platform.service.message.websockettransport;

import java.net.SocketException;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketOpcode;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.SConfigParser;
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
			websocket.setPingInterval(idletimeout >>> 1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
				ret.setResult(WebSocketConnectionClient.this);
			}
			
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
			{
				handler.connectionClosed(WebSocketConnectionClient.this, null);
			}
			
			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
			{
//				System.out.println("Binary msg size: " + (binary != null ? String.valueOf(binary.length) : "null"));
				handleFramePayload(binary);
			}
			
//			@Override
//			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
//			{
//				System.out.println("Cl frame op: " + frame.getOpcode());
//				super.onFrame(websocket, frame);
//			}
//			
//			public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
//			{
//				System.out.println("cl " + frame.getPayloadLength() + " " + WebSocketConnectionClient.this.hashCode() + " frame op: " + frame.getOpcode());
//				handleFramePayload(frame.getPayload());
//			}
			
//			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
//			{
////				System.out.println("RecClient: " + Arrays.hashCode(binary) + " " + System.currentTimeMillis());
//				handleFramePayload(binary);
//			}
			
			public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception
		    {
				exception.printStackTrace();
				ret.setException(exception);
		    }
			
//			public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
//			{
//				handler.connectionClosed(WebSocketConnectionClient.this, null);
//			}
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
			synchronized(this)
			{
//				System.out.println("Sending cl: " + address);
//				WebSocketFrame wsf = new WebSocketFrame();
//				wsf.setOpcode(WebSocketOpcode.BINARY);
//				wsf.setFin(true);
//				wsf.setPayload(header);
//				websocket.sendFrame(wsf);
//				wsf = new WebSocketFrame();
//				wsf.setOpcode(WebSocketOpcode.BINARY);
//				wsf.setFin(true);
//				wsf.setPayload(body);
//				websocket.sendFrame(wsf);
//				websocket.sendBinary(SUtil.mergeData(header, body));
				sendAsFrames(header);
				sendAsFrames(body);
				websocket.flush();
				ret.setResult(null);
			}
		}
		catch (Exception e)
		{
			ret.setException(e);
			e.printStackTrace();
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
			int count = data.length / maxpayload + 1;
			
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
				
				int psize = Math.min(maxpayload, data.length - offset);
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
