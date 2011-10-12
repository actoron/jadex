package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public interface IBluetoothAdapter {

	public enum BluetoothState {
		discovery_started, discovery_finished, on, off, switching_on, switching_off;
	}
	
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

	boolean isDeviceBonded(IBluetoothDevice device);
}
