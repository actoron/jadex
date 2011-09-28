package jadex.android.bluetooth.service;

// Declare the interface.
oneway interface IConnectionCallback {
//  void incomingConnection(String device);
//  void maxConnectionsReached();
//  void messageReceived(String device, in byte[] message);
//  void connectionLost(String device);
	void deviceListChanged();
}
