package jadex.platform.service.transport;

import jadex.bridge.IInternalAccess;

/**
 *  Access interface with callbacks to be used by concrete transport implementations.
 */
public interface ITransportHandler<Con>
{
	/**
	 *  Get the internal access.
	 */
	public IInternalAccess	getAccess();
	
	/**
	 *  Deliver a received message.
	 *  @param con	The connection.
	 *  @param header	The message header.
	 *  @param body	The message body.
	 */
	public void	messageReceived(Con con, byte[] header, byte[] body);
	
	/**
	 *  Called when a server connection is established.
	 *  @param con	The connection.
	 */
	public void	connectionEstablished(Con con);
	
	/**
	 *  Called when a connection is closed.
	 *  @param con	The connection.
	 *  @param e	The exception, if any.
	 */
	public void	connectionClosed(Con con, Exception e);
}
