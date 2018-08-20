package jadex.platform.service.transport;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Internal transport representation for host:port-based transports.
 */
public interface ITransport<Con>
{
	/**
	 *  Initialize the transport.
	 *  To be called once, before any other method.
	 *  @param handler 	The transport handler with callback methods. 
	 */
	public void	init(ITransportHandler<Con> handler);
	
	/**
	 *  Shutdown and free all resources.
	 *  To be called once, as last method. 
	 */
	public void	shutdown();

	/**
	 *  Get the protocol name.
	 */
	public String	getProtocolName();
	
	/**
	 *  Open a port to accept connections.
	 *  @param port	The (positive) port or 0 for any port.
	 *  @return A future holding the actual port, once the server is running.
	 */
	public IFuture<Integer>	openPort(int port);

	/**
	 *  Create a connection to a given address.
	 *  @param address	The target platform's address.
	 *  @param target	The target identifier to maybe perform authentication of the connection.
	 *  @return A future containing the connection when succeeded.
	 */
	public IFuture<Con>	createConnection(String address, final IComponentIdentifier target);
	
	/**
	 *  Perform close operations on a connection.
	 *  Potentially cleans up key attachments as well.
	 */
	public void closeConnection(Con con);

	/**
	 *  Send bytes using the given connection.
	 *  @param sc	The connection.
	 *  @param header	The message header.
	 *  @param body	The message body.
	 *  @return	A future indicating success and the current transport priority.
	 */
	public IFuture<Integer> sendMessage(Con con, byte[] header, byte[] body);
}
