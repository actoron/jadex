package acconnector2009;

import eis.iilang.DataContainer;

/**
 * Handles incoming messages from the MASSim-Server.
 * 
 * @author tristanbehrens
 *
 */
public interface ConnectionListener {

	/**
	 * Handles a message.
	 * 
	 * @param connection
	 * @param container
	 */
	public void handleMessage(Connection connection, DataContainer container);
	
}
