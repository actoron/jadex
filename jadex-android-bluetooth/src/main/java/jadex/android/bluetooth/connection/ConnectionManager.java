package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.exceptions.MessageNotSendException;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.routing.IPacketSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The ConnectionManager manages In- and Outgoing connections. It is a HashMap
 * String->Connection and can inform Listeners of added and removed Connections.
 * 
 * @author Julian Kalinowski
 * 
 */
public class ConnectionManager extends HashMap<String, IConnection> implements
		IPacketSender {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = -2699630997693616455L;

	/**
	 * Listener Interface
	 * @author Julian Kalinowski
	 */
	public interface ConnectionsListener {
		/**
		 * Called when a Connection is removed.
		 * @param address of the remote device which has been disconnected
		 */
		void connectionRemoved(String address);

		/**
		 * Called when a Connection is added.
		 * @param address Address of the remote device
		 * @param connection the new Connection
		 */
		void connectionAdded(String address, IConnection connection);
	}

	private List<ConnectionsListener> listeners;

	/**
	 * Constructor
	 */
	public ConnectionManager() {
		super();
		listeners = new ArrayList<ConnectionsListener>();
	}

	@Override
	public IConnection put(String address, IConnection value) {
		synchronized (this) {
			IConnection put = super.put(address, value);
			notifyConnectionAdded(address, value);
			return put;
		}
	}

	@Override
	public IConnection remove(Object key) {
		synchronized (this) {
			IConnection remove = super.remove(key);
			if (remove != null) {
				notifyConnectionRemoved(key.toString());
			}
			return remove;
		}
	}

	/**
	 * Checks whether a Connection to the given devices address already exists.
	 * @param deviceAddress
	 * @return true, if a connection with the given address already exists.
	 */
	public boolean containsConnection(String deviceAddress) {
		return super.containsKey(deviceAddress);
	}

	/**
	 * Adds a Listener
	 * @param l
	 */
	public void addConnectionsListener(ConnectionsListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a Listener
	 * @param l
	 */
	public void removeConnectionsListener(ConnectionsListener l) {
		listeners.remove(l);
	}

	private void notifyConnectionAdded(String address, IConnection value) {
		for (ConnectionsListener l : listeners) {
			l.connectionAdded(address, value);
		}
	}

	private void notifyConnectionRemoved(String address) {
		for (ConnectionsListener l : listeners) {
			l.connectionRemoved(address);
		}
	}

	/**
	 * Terminates all Connections.
	 */
	public void stopAll() {
		for (Entry<String, IConnection> entry : entrySet()) {
			IConnection value = entry.getValue();
			// String key = entry.getKey();
			value.close();
			// send message to inform others of shutdown
		}
		clear();
		String[] keys = keySet().toArray(new String[keySet().size()]);
		for (String address : keys) {
			for (ConnectionsListener l : listeners) {
				l.connectionRemoved(address);
			}
		}
	}

	@Override
	public void sendMessageToConnectedDevice(DataPacket packet, String address)
			throws MessageNotSendException {
		IConnection con = get(address);
		if (con != null && con.isAlive()) {
			try {
				con.write(packet);
			} catch (IOException e) {
				throw new MessageNotSendException(e.getMessage());
			}
		} else {
			remove(con);
			throw new MessageNotSendException("connection was not alive");
		}
	}
}
