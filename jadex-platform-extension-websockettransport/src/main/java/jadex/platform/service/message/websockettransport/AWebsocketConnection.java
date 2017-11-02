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
	
	/** Current maximum message size. */
	protected int maxmsgsize;
	
	/** The agent. */
	protected WebSocketTransportAgent pojoagent;
	
	/** Current bytes received. */
	protected int bytesreceived;
	
	/**
	 *  Creates the connection.
	 */
	public AWebsocketConnection(ITransportHandler<IWebSocketConnection> handler)
	{
		this.handler = handler;
		pojoagent = (WebSocketTransportAgent) handler.getAccess().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
		maxmsgsize = pojoagent.getMaximumMessageSize();
	}
	
	/**
	 *  Handles a websocket message payload.
	 *  
	 *  @param payload The payload.
	 */
	protected void handleMessagePayload(byte[] payload)
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
				maxmsgsize = pojoagent.getMaximumMessageSize();
			}
			else
			{
				header = payload;
				if (header != null)
					maxmsgsize -= header.length;
				hasheader = true;
			}
		}
	}
}
