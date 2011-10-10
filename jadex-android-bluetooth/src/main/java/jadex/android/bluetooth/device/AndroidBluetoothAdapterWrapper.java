package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;

public class AndroidBluetoothAdapterWrapper implements IBluetoothAdapter {

	private BluetoothAdapter mAdapter;

	public AndroidBluetoothAdapterWrapper(BluetoothAdapter adapter) {
		mAdapter = adapter;
	}
	
	@Override
	public String getAddress() {
		return mAdapter.getAddress();
	}

	@Override
	public Set<IBluetoothDevice> getBondedDevices() {
		Set<BluetoothDevice> bondedDevices = mAdapter.getBondedDevices();
		Set<IBluetoothDevice> result = new HashSet<IBluetoothDevice>();
		for (BluetoothDevice bluetoothDevice : bondedDevices) {
			result.add(new AndroidBluetoothDevice(bluetoothDevice));
		}
		return result;
	}

	@Override
	public boolean isEnabled() {
		return mAdapter.isEnabled();
	}

	@Override
	public void enable() {
		mAdapter.enable();
	}

	@Override
	public IBluetoothServerSocket listenUsingRfcommWithServiceRecord(
			String serviceName, UUID uuid) throws IOException {
		BluetoothServerSocket socket = mAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid);
		AndroidBluetoothServerSocketWrapper androidSocket = new AndroidBluetoothServerSocketWrapper(socket);
		return androidSocket;
	}

	@Override
	public boolean isDiscovering() {
		return mAdapter.isDiscovering();
	}

	@Override
	public void cancelDiscovery() {
		mAdapter.cancelDiscovery();
	}
	
	public BluetoothAdapter getBluetoothAdapter() {
		return mAdapter;
	}

	@Override
	public IBluetoothDevice getRemoteDevice(String address) {
		BluetoothDevice remoteDevice = mAdapter.getRemoteDevice(address);
		return new AndroidBluetoothDevice(remoteDevice);
	}

	@Override
	public void startDiscovery() {
		mAdapter.startDiscovery();
	}

}
