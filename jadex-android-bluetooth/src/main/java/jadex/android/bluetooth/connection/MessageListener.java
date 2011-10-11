package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.message.BluetoothMessage;

public interface MessageListener {

	void messageReceived(BluetoothMessage msg);
}
