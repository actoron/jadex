package jadex.android.bluetooth;

import jadex.android.bluetooth.device.IBluetoothDevice;

import java.io.IOException;



import android.bluetooth.BluetoothDevice;

public interface IConnection {

	public boolean isAlive();

	public void write(byte[] bytes) throws IOException;

	/* Call this from the main Activity to shutdown the connection */
	public void close();

	public void addConnectionListener(ConnectionListener l);
	
	public void removeConnectionListener(ConnectionListener l);
	
	public void connect();
	
	public IBluetoothDevice getRemoteDevice();
}