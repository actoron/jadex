package jadex.platform.service.message.websockettransport;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.ContinuousFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.TextFrame;
import org.java_websocket.handshake.ServerHandshake;

import com.neovisionaries.ws.client.WebSocketOpcode;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.platform.service.transport.ITransportHandler;

public class WebsocketConnectionClient2 extends AWebsocketConnection
{
	/** The connection address. */
	protected String address;
	
	/** The websocket client. */
	protected WebSocketClient wsclient;
	
//	protected
	
	/**
	 *  Creates the connection.
	 *  
	 *  @param address Target address.
	 *  @param target Target platform.
	 */
	public WebsocketConnectionClient2(String address, IComponentIdentifier target, ITransportHandler<IWebSocketConnection> handler)
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
			URI uri = new URI("ws://" + address);
			wsclient = new WebSocketClient(uri)
			{
				/** Flag for notifying about close. */
				protected boolean notifyclosed = true;
				
				public void onOpen(ServerHandshake handshakedata)
				{
					ret.setResult(WebsocketConnectionClient2.this);
				}
				
				public void onMessage(String message)
				{
					if (NULL_MSG_COMMAND.equals(message))
						handleMessagePayload(null);
				}
				
				public void onMessage(ByteBuffer bytes)
				{
					byte[] binary = null;
					if (bytes == null)
					{
						binary = new byte[0];
					}
					else
					{
						binary = new byte[bytes.remaining()];
						bytes.get(binary);
					}
					
//					System.out.println("Client Binary msg size: " + (binary != null ? String.valueOf(binary.length) : "null"));
					handleMessagePayload(binary);
				}
				
				public void onError(Exception ex)
				{
					if (!ret.isDone())
						ret.setException(ex);
					try
					{
						closeBlocking();
					}
					catch (Exception e)
					{
					}
					try
					{
						getSocket().close();
					}
					catch (Exception e)
					{
					}
				}
				
				public void onClose(int code, String reason, boolean remote)
				{
					synchronized(this)
					{
						if (notifyclosed)
						{
							notifyclosed = false;
							handler.connectionClosed(WebsocketConnectionClient2.this, null);
						}
					}
				}
			};
			
			wsclient.setTcpNoDelay(true);
			wsclient.connect();
			
//			if (wsclient.connectBlocking())
//			{
//				ret.setResult(WebsocketConnectionClient2.this);
//			}
//			else
//			{
//				ret.setException(new IllegalStateException("Connection failed: " + address));
//			}
		}
		catch (Exception e)
		{
			ret.setException(e);
		}
		ret.addResultListener(new IResultListener<IWebSocketConnection>()
		{
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			@Override
			public void resultAvailable(IWebSocketConnection result)
			{
				System.out.println("CONNECTION SUCCEEDED: " + address);
			}
		});
		return ret;
	}

	
	public IFuture<Void> sendMessage(byte[] header, byte[] body)
	{
		synchronized(this)
		{
			sendAsFrames(header);
			sendAsFrames(body);
		}
		return IFuture.DONE;
	}
	
	public void close()
	{
		wsclient.close();
	}

	public void forceClose()
	{
		SUtil.close(wsclient.getSocket());
	}
	
	/**
	 *  Fragment and send data as websocket frames.
	 *  
	 *  @param data The data.
	 *  @param fut The future.
	 */
	protected IFuture<Void> sendAsFrames(byte[] data)
	{
		Future<Void> ret = new Future<Void>();
		if (data == null)
		{
			TextFrame tf = new TextFrame();
			tf.setPayload(ByteBuffer.wrap(NULL_MSG_COMMAND.getBytes(SUtil.UTF8)));
			tf.setFin(true);
			wsclient.sendFrame(tf);
		}
		else
		{
			int offset = 0;
			int count = data.length / pojoagent.getMaximumPayloadSize() + 1;
			
			for (int i = 0; i < count; ++i)
			{
				DataFrame df = null;
				
				if (i == 0)
					df = new BinaryFrame();
				else
					df = new ContinuousFrame();
				
				df.setFin((i + 1) == count);
				
				int psize = Math.min(pojoagent.getMaximumPayloadSize(), data.length - offset);
				byte[] payload = new byte[psize];
				System.arraycopy(data, offset, payload, 0, psize);
				offset += psize;
				df.setPayload(ByteBuffer.wrap(payload));
				
				wsclient.sendFrame(df);
			}
		}
		
		return ret;
	}
}
