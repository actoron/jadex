package jadex.android.bluetooth.service;

import jadex.android.bluetooth.domain.BluetoothMessage;

public interface MessageListener {

	void messageReceived(BluetoothMessage msg);
}
