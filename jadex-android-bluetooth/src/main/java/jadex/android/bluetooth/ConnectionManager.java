package jadex.android.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class ConnectionManager extends HashMap<String, IConnection> {

	public interface ConnectionsListener {
		void connectionRemoved(String address);

		void connectionAdded(String address, IConnection connection);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ConnectionsListener> listeners;

	public ConnectionManager() {
		super();
		listeners = new ArrayList<ConnectionsListener>();
	}

	@Override
	public IConnection put(String address, IConnection value) {
		IConnection put = super.put(address, value);
			notifyConnectionAdded(address, value);
		return put;
	}

	@Override
	public IConnection remove(Object key) {
		IConnection remove = super.remove(key);
		if (remove != null) {
			notifyConnectionRemoved(key.toString());
		}
		return remove;
	}
	
	public boolean containsKey(String deviceID) {
		return super.containsKey(deviceID);
	}
	
	public void addConnectionsListener(ConnectionsListener l) {
		listeners.add(l);
	}

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

	public void stopAll() {
		for (Entry<String, IConnection> entry : entrySet()) {
			IConnection value = entry.getValue();
			//String key = entry.getKey();
			value.close();
			// send message to inform others of shutdown
		}
		clear();
		for (ConnectionsListener l : listeners) {
			for (String address: this.keySet()) {
				l.connectionRemoved(address);
			}
		}
	}
}
