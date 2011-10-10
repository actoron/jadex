package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public interface IBluetoothAdapter {

	String getAddress();

	Set<IBluetoothDevice> getBondedDevices();

	boolean isEnabled();

	void enable();

	IBluetoothServerSocket listenUsingRfcommWithServiceRecord(
			String serviceName, UUID uuid) throws IOException;

	boolean isDiscovering();

	void cancelDiscovery();
	
	IBluetoothDevice getRemoteDevice(String address);

	void startDiscovery();
}
