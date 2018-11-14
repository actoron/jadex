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
	 *  @param header The message header.
	 *  @param body The message body.
	 *  @return	A future indicating success.
	 */
	public IFuture<Integer> sendMessage(byte[] header, byte[] body);
	
	/**
	 *  Closes the connection (ignored if already closed).
	 */
	public void close();
	
	/**
	 *  Forcibly closes the connection (ignored if already closed).
	 */
	public void forceClose();
}
