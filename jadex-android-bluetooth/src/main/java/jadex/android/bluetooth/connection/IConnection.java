package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.message.DataPacket;

import java.io.IOException;

public interface IConnection {

	public boolean isAlive();

	public void write(DataPacket msg) throws IOException;

	/* Call this from the main Activity to shutdown the connection */
	public void close();

	public void addConnectionListener(IConnectionListener l);
	
	public void removeConnectionListener(IConnectionListener l);
	
	public void connect();
	
	public IBluetoothDevice getRemoteDevice();
}