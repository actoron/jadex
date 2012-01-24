package jadex.base.service.message.transport.httprelaymtp.nio;

import java.nio.channels.SelectionKey;

/**
 *  Handler interface for managing NIO operations.
 */
public interface IHttpRequest
{
	/**
	 *  Get the host to connect to.
	 */
	public String	getHost();
	
	/**
	 *  Get the port to connect to.
	 */
	public int	getPort();
	
	/**
	 *  Handle connection success or error.
	 *  Has to change the interest to OP_WRITE, if connection was successful.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int handleConnect(SelectionKey key);
	
	/**
	 *  Write the HTTP request to the NIO connection.
	 *  May be called multiple times, if not all data can be send at once.
	 *  Has to change the interest to OP_READ, once all data is sent.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int handleWrite(SelectionKey key);
	
	/**
	 *  Receive the HTTP response from the NIO connection.
	 *  May be called multiple times, if not all data can be send at once.
	 *  Has to deregister interest in the connection, once required data is received.
	 *  May close the connection or leave it open for reuse if the server supports keep-alive.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int	handleRead(SelectionKey key);
}
