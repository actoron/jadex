package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.net.Socket;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoWSD.WebSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
import fi.iki.elonen.NanoWSD.WebSocketFrame.OpCode;
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
	
	/** The low-level socket. */
	protected Socket socket;
	
	/**
	 *  Creates the websocket.
	 */
	public WebSocketConnectionServer(IHTTPSession handshakerequest, ITransportHandler<IWebSocketConnection> handlr, Socket acceptsocket)
	{
		super(handlr);
		socket = acceptsocket;
		
		websocket = new WebSocket(handshakerequest)
		{
			/** Flag if connected. */
			protected boolean connected = false;
			
			/**
			 *  Intercept package to count total received message size.
			 */
			protected void debugFrameReceived(WebSocketFrame frame)
			{
				if (frame.getOpCode().equals(OpCode.Text))
				{
					try
					{
						this.close(CloseCode.UnsupportedData, "Text not supported.", false);
					}
					catch (IOException e)
					{
					}
				}
				else
				{
					if (frame.getOpCode().equals(OpCode.Binary) ||
						frame.getOpCode().equals(OpCode.Continuation))
					{
						bytesreceived += frame.getBinaryPayload() == null ? 0 : frame.getBinaryPayload().length;
						
						if (bytesreceived > maxmsgsize)
						{
							try
							{
								this.close(CloseCode.MessageTooBig, "Maxmimum message size exceeded.", false);
							}
							catch (IOException e)
							{
							}
						}
						else
						{
							if (frame.isFin())
								bytesreceived = 0;
						}
					}
				}
				super.debugFrameReceived(frame);
			}
			
			/**
			 *  Called on open.
			 */
			protected void onOpen()
			{
				synchronized (this)
				{
					connected = true;
				}
				handler.connectionEstablished(WebSocketConnectionServer.this);
//				System.out.println("srv conn open");
			}

			/**
			 *  Called on close.
			 */
			protected void onClose(CloseCode code, String reason, boolean initiatedByRemote)
			{
				boolean notify = false;
				synchronized (this)
				{
					if (connected)
					{
						connected = false;
						notify = true;
					}
				}
				if (notify)
					handler.connectionClosed(WebSocketConnectionServer.this, null);
//				System.out.println("srv conn closed: " + reason);
			}
			
			/**
			 *  Called on message.
			 */
			protected void onMessage(WebSocketFrame message)
			{
//				System.out.println("srv msg op: " + message.getOpCode() + " " + message.isFin());
				byte[] payload = null;
				if (message.getOpCode().equals(OpCode.Binary))
				{
					try
					{
						payload = message.getBinaryPayload();
					}
					catch(Exception e)
					{
					}
				}
				
				if (payload != null)
				{
//					System.out.println("RecServer: " + Arrays.hashCode(payload) + " " + System.currentTimeMillis());
					handleMessagePayload(payload);
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
				try
				{
					this.close(CloseCode.AbnormalClosure, exception.toString(), false);
				}
				catch (IOException e)
				{
				}
//				handler.connectionClosed(WebSocketConnectionServer.this, exception);
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
			synchronized(this)
			{
//				WebSocketFrame wsf = new WebSocketFrame(WebSocketFrame.OpCode.Binary, true, header);
//				websocket.sendFrame(wsf);
//				wsf = new WebSocketFrame(WebSocketFrame.OpCode.Binary, true, body);
//				websocket.sendFrame(wsf);
//				websocket.send(SUtil.mergeData(header, body));
				sendAsFrames(header);
				sendAsFrames(body);
				ret.setResult(null);
			}
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
			forceClose();
		}
		catch (IOException e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Forcibly closes the connection (ignored if already closed).
	 */
	public void forceClose()
	{
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
	
	/**
	 *  Fragment and send data as websocket frames.
	 *  
	 *  @param data The data.
	 *  @throws IOException On communication failure. 
	 */
	protected void sendAsFrames(byte[] data) throws IOException
	{
		if (data == null)
		{
			WebSocketFrame wsf = new WebSocketFrame(WebSocketFrame.OpCode.Binary, true, data);
			websocket.sendFrame(wsf);
		}
		else
		{
			int offset = 0;
			int count = data.length / pojoagent.getMaximumPayloadSize() + 1;
			
			for (int i = 0; i < count; ++i)
			{
				OpCode opcode = null;
				if (i == 0)
					opcode = WebSocketFrame.OpCode.Binary;
				else
					opcode = WebSocketFrame.OpCode.Continuation;
				
				boolean fin = false;
				if (i + 1 == count)
					fin = true;
				
				int psize = Math.min(pojoagent.getMaximumPayloadSize(), data.length - offset);
				byte[] payload = new byte[psize];
				System.arraycopy(data, offset, payload, 0, psize);
				offset += psize;
				
//				System.out.println("frame totalsize: " + data.length + " count " + count + " " + i + " " + opcode + " " + fin);
				WebSocketFrame wsf = new WebSocketFrame(opcode, fin, payload);
				websocket.sendFrame(wsf);
				socket.getOutputStream().flush();
			}
		}
	}
}
