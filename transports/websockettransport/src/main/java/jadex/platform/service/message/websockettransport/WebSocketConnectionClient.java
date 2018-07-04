package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
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
//		{
//			public void execute(final Runnable command)
//			{
//				super.execute(new Runnable()
//				{
//					public void run()
//					{
//						try
//						{
////							System.out.println("Started: " + command.getClass() + " " + Thread.currentThread().getId());
//							command.run();
////							System.out.println("Stopped: " + Thread.currentThread().getId());
//						}
//						catch (Throwable e)
//						{
//							System.err.println("Uncaught exception!!!");
//							e.printStackTrace();
//						}
//					}
//				});
//			}
//			
//			public void shutdown()
//			{
//			}
//			
//			public List<Runnable> shutdownNow()
//			{
//				return new ArrayList<Runnable>();
//			}
//		};
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
			websocket = pojoagent.getWebSocketFactory().createSocket("ws://" + address);
			websocket.setAutoFlush(true);
			websocket.setPingInterval(pojoagent.getIdleTimeout() >>> 1);
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			ret.setException(e);
			return ret;
		}
		
		websocket.addListener(new WebSocketListener()
		{
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
			{
//				try
//				{
//					websocket.getSocket().setTcpNoDelay(true);
//				}
//				catch (SocketException e)
//				{
//				}
//				ret.setResult(WebSocketConnectionClient.this);
			}
			
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
			{
//				System.out.println("Disconnect: " + WebSocketConnectionClient.this);
				try
				{
					websocket.getSocket().close();
				}
				catch (Exception e)
				{
				}
				handler.connectionClosed(WebSocketConnectionClient.this, null);
			}
			
			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
			{
				// API bug, zero-sized messages returned as null.
				if (binary == null)
					binary = new byte[0];
//				System.out.println("Client Binary msg size: " + (binary != null ? String.valueOf(binary.length) : "null"));
				handleMessagePayload(binary);
			}
			
			public void onTextMessage(WebSocket websocket, String text) throws Exception
			{
				if (NULL_MSG_COMMAND.equals(text))
					handleMessagePayload(null);
			}
			
			/** Message size check. */
			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
				if (frame.getOpcode() == WebSocketOpcode.TEXT && !frame.getFin())
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
				ret.setException(exception);
		    }
			
//			public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception
//			{
//				System.out.println("Threadexit: " + Thread.currentThread().getId());
//				// There seems to be a bug in the websocket API
//				// that terminates the threading without (properly)
//				// terminating frames, this fixes the issue.
//				cleanup(null);
//			}
			
			public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
//				System.out.println("Frame handled1: " + (frame == null) + " " + System.identityHashCode(frame));
			}
			
			public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
				cleanup(new RuntimeException("Connection terminated."));
			}
			
			public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception
			{
//				System.out.println("Frame handled2: "  + (frame == null) + " " + System.identityHashCode(frame));
				
//				Future<Void> fut = scheduledframes.remove(frame);
//				if (fut != null)
//					fut.setException(cause);
//				else
//					System.out.println("FUT NULL2");
				cleanup(cause);
			}
			
			public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception
			{
			}

			public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
			{
			}

			public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception
			{
			}

			public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception
			{
			}

			public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception
			{
			}

			public void onError(WebSocket websocket, WebSocketException cause) throws Exception
			{
				cleanup(cause);
			}

			public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception
			{
				cleanup(cause);
			}

			public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception
			{
				cleanup(cause);
			}

			public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception
			{
				cleanup(cause);
			}

			public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception
			{
				cleanup(cause);
			}

			public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception
			{
				cleanup(cause);
			}

			public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception
			{
				cleanup(new RuntimeException(cause));
			}

			public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception
			{
			}
			
			protected void cleanup(Exception e)
			{
				try
				{
					websocket.getSocket().close();
				}
				catch (Exception e1)
				{
				}
			}
		});
		
//		System.out.println("Maximum payload size: " + websocket.getMaxPayloadSize());
//		websocket.addListener(new WebSocketAdapter()
//		{
//			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
//			{
////				try
////				{
////					websocket.getSocket().setTcpNoDelay(true);
////				}
////				catch (SocketException e)
////				{
////				}
////				ret.setResult(WebSocketConnectionClient.this);
//			}
//			
//			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
//			{
////				System.out.println("Disconnect: " + WebSocketConnectionClient.this);
//				try
//				{
//					websocket.getSocket().close();
//				}
//				catch (Exception e)
//				{
//				}
//				handler.connectionClosed(WebSocketConnectionClient.this, null);
//			}
//			
//			public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
//			{
//				// API bug, zero-sized messages returned as null.
//				if (binary == null)
//					binary = new byte[0];
////				System.out.println("Client Binary msg size: " + (binary != null ? String.valueOf(binary.length) : "null"));
//				handleMessagePayload(binary);
//			}
//			
//			public void onTextMessage(WebSocket websocket, String text) throws Exception
//			{
//				if (NULL_MSG_COMMAND.equals(text))
//					handleMessagePayload(null);
//			}
//			
//			/** Message size check. */
//			public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception
//			{
//				if (frame.getOpcode() == WebSocketOpcode.TEXT && !frame.getFin())
//				{
//					websocket.disconnect(WebSocketCloseCode.UNACCEPTABLE);
//				}
//				else
//				{
//					if (frame.getOpcode() == WebSocketOpcode.BINARY ||
//						frame.getOpcode() == WebSocketOpcode.CONTINUATION)
//					{
//						bytesreceived += frame.getPayload() == null ? 0 : frame.getPayload().length;
//						
//						if (bytesreceived > maxmsgsize)
//						{
//							websocket.disconnect(WebSocketCloseCode.OVERSIZE);
//						}
//						else
//						{
//							if (frame.getFin())
//								bytesreceived = 0;
//						}
//					}
//				}
//			}
//			
//			public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception
//		    {
////				System.err.println("GOT CONNECT ERROR!");
//				ret.setException(exception);
//		    }
//			
////			public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception
////			{
////				System.out.println("Threadexit: " + Thread.currentThread().getId());
////				// There seems to be a bug in the websocket API
////				// that terminates the threading without (properly)
////				// terminating frames, this fixes the issue.
////				cleanup(null);
////			}
//			
//			public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception
//			{
////				System.out.println("Frame handled1: " + (frame == null) + " " + System.identityHashCode(frame));
//				Future<Void> fut = scheduledframes.remove(frame);
//				if (fut != null)
//					fut.setResult(null);
//			}
//			
//			public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception
//			{
//				Future<Void> fut = scheduledframes.remove(frame);
//				if (fut != null)
//					fut.setResult(null);
//				cleanup(new RuntimeException("Connection terminated."));
//			}
//			
//			public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception
//			{
////				System.out.println("Frame handled2: "  + (frame == null) + " " + System.identityHashCode(frame));
//				
////				Future<Void> fut = scheduledframes.remove(frame);
////				if (fut != null)
////					fut.setException(cause);
////				else
////					System.out.println("FUT NULL2");
//				cleanup(cause);
//			}
//			
//			protected void cleanup(Exception e)
//			{
//				try
//				{
//					websocket.getSocket().close();
//				}
//				catch (Exception e1)
//				{
//				}
//				synchronized (scheduledframes)
//				{
//					while (!scheduledframes.isEmpty())
//					{
//						Future<Void> fut = scheduledframes.remove(scheduledframes.keySet().iterator().next());
//						fut.setException(e != null? e : new RuntimeException("Connection terminated."));
//					}
//				}
//			}
//		});
		
		execservice.execute(new Runnable()
		{
			public void run()
			{
				try
				{
//					websocket.connect(execservice).get(pojoagent.getConnectTimeout(), TimeUnit.MILLISECONDS);
					try
					{
						websocket.getSocket().setTcpNoDelay(true);
						websocket.getSocket().setSoTimeout((int) pojoagent.getConnectTimeout());
					}
					catch (SocketException e)
					{
						e.printStackTrace();
					}
					websocket.connect(execservice).get();
					ret.setResult(WebSocketConnectionClient.this);
				}
				catch (Exception e)
				{
					ret.setException(e);
				}
			}
		});
		
//		websocket.connectAsynchronously();
		
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
		if (!WebSocketState.OPEN.equals(websocket.getState()) || websocket.getSocket().isClosed())
			return new Future<Void>(new IOException("Connection is not available."));
//		System.out.println("SendClient: " + hashCode() + " " + (header == null ? "null" : String.valueOf(header.length)) + " " + (body == null ? "null" : String.valueOf(body.length)));
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			synchronized(this)
			{
				sendAsFrames(header);
				sendAsFrames(body);
			}
			ret.setResult(null);
		}
		catch (Exception e)
		{
			forceClose();
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
			forceClose();
		}
	}
	
	/**
	 *  Forcibly closes the connection (ignored if already closed).
	 */
	public void forceClose()
	{
		websocket.disconnect();
		if (websocket != null && websocket.getSocket() != null)
		{
			try
			{
				websocket.getSocket().close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	/**
	 *  Fragment and send data as websocket frames.
	 *  
	 *  @param data The data.
	 *  @param fut The future.
	 */
	protected void sendAsFrames(byte[] data)
	{
		if (data == null)
		{
			WebSocketFrame wsf = new WebSocketFrame();
			wsf.setOpcode(WebSocketOpcode.TEXT);
			wsf.setFin(true);
			wsf.setPayload(NULL_MSG_COMMAND);
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
