package jadex.platform.service.message.websockettransport;

import jadex.commons.future.IFuture;

/**
 *  Interface representing a connection via web socket, either server- or client-side.
 *
 */
public interface IWebSocketConnection
{
	/**
	 *  Send bytes using the connection.
	 *  @param message The message.
	 *  @return	A future indicating success.
	 */
	public IFuture<Void> sendMessage(byte[] message);
	
	/**
	 *  Closes the connection (ignored if already closed).
	 */
	public void close();
}
