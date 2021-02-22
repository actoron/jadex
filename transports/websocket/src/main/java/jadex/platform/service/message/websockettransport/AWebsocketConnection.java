package jadex.platform.service.message.websockettransport;

import jadex.bridge.component.IPojoComponentFeature;
import jadex.platform.service.transport.ITransportHandler;

/**
 *  Abstract websocket connection implements shared functionality between client and server.
 *
 */
public abstract class AWebsocketConnection implements IWebSocketConnection
{
	/** Command to generate a null message (send as Text frame). */
	protected static final String NULL_MSG_COMMAND = "null";
	
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
		pojoagent = (WebSocketTransportAgent) handler.getAccess().getFeature(IPojoComponentFeature.class).getPojoAgent();
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
		byte[] dispatchheader = null;
		boolean dispatch = false;
		synchronized(this)
		{
			if (hasheader)
			{
				dispatch = true;
				dispatchheader = header;
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
		if (dispatch)
			handler.messageReceived(this, dispatchheader, payload);
	}
}
