package jadex.android.bluetooth.service;

import jadex.android.bluetooth.message.BluetoothMessage;

public interface MessageListener {

	void messageReceived(BluetoothMessage msg);
}
