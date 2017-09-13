package jadex.platform.service.message.websockettransport;

import jadex.platform.service.transport.ITransportHandler;

/**
 *  Abstract websocket connection implements shared functionality between client and server.
 *
 */
public abstract class AWebsocketConnection implements IWebSocketConnection
{
	/** The handler. */
	protected ITransportHandler<IWebSocketConnection> handler;
	
	/** Header saved for handling once body arrives. */
	protected byte[] header;
	
	/**
	 *  Creates the connection.
	 */
	public AWebsocketConnection(ITransportHandler<IWebSocketConnection> handler)
	{
		this.handler = handler;
	}
	
	/**
	 *  Handles a websocket frame payload.
	 *  
	 *  @param payload The payload.
	 */
	protected void handleFramePayload(byte[] payload)
	{
//		List<byte[]> msg = SUtil.splitData(payload);
//		handler.messageReceived(this, msg.get(0), msg.get(1));
		if (header != null)
		{
			handler.messageReceived(this, header, payload);
			header = null;
			
		}
		else
		{
			header = payload;
		}
	}
}
