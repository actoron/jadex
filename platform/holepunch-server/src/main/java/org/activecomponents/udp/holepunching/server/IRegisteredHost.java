package org.activecomponents.udp.holepunching.server;

/**
 *  Listener for holepunching requests.
 *
 */
public interface IRegisteredHost
{
	/**
	 *  Writes a message to the connected host.
	 *  
	 *  @param msg The message.
	 */
	public void writeMsg(String msg);
}
