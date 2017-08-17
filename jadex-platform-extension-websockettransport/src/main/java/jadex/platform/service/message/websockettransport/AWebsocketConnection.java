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
	
	/** Flag if the header was saved for handling once body arrives. */
	protected boolean savedheader;
	
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
		if (savedheader)
		{
			handler.messageReceived(this, header, payload);
			savedheader = false;
			
		}
		else
		{
			header = payload;
			savedheader = true;
		}
	}
}
