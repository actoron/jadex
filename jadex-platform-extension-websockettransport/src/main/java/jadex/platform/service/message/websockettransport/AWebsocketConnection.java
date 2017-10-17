package jadex.platform.service.message.websockettransport;

import jadex.bridge.component.IPojoComponentFeature;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Abstract websocket connection implements shared functionality between client and server.
 *
 */
public abstract class AWebsocketConnection implements IWebSocketConnection
{
	/** The handler. */
	protected ITransportHandler<IWebSocketConnection> handler;
	
	/** Flag if the header was receiver: Needed because header can be null! */
	protected boolean hasheader = false;
	
	/** Header saved for handling once body arrives. */
	protected byte[] header;
	
	/** Maximum payload size. */
	protected int maxpayload;
	
	/** Idle connection timeout. */
	protected int idletimeout = 60000;
	
	/**
	 *  Creates the connection.
	 */
	public AWebsocketConnection(ITransportHandler<IWebSocketConnection> handler)
	{
		this.handler = handler;
		WebSocketTransportAgent pojo = (WebSocketTransportAgent) handler.getAccess().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
 		maxpayload = pojo.getMaximumPayloadSize();
 		idletimeout = pojo.getIdleTimeout();
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
		synchronized(this)
		{
			if (hasheader)
			{
				handler.messageReceived(this, header, payload);
				header = null;
				hasheader = false;
			}
			else
			{
				header = payload;
				hasheader = true;
			}
		}
	}
}
