package jadex.platform.service.message.websockettransport;

import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent implementing the web socket transport.
 *
 */
public class WebSocketTransportAgent extends AbstractTransportAgent<IWebSocketConnection>
{
	 	/**
	 	 *  Get the transport implementation
	 	 */
	 	public ITransport<IWebSocketConnection> createTransportImpl()
	 	{
	 		return new WebSocketTransport();
	 	}
}
