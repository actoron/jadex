package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;

/**
 * A Wrapper to abstract from the Android BluetoothAdapter Implementation.
 * 
 * @author Julian Kalinowski
 */
public class AndroidBluetoothAdapterWrapper implements IBluetoothAdapter {

	private BluetoothAdapter mAdapter;

	/**
	 * Constructor
	 * 
	 * @param adapter
	 */
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
			result.add(new AndroidBluetoothDeviceWrapper(bluetoothDevice));
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
		BluetoothServerSocket socket = mAdapter
				.listenUsingRfcommWithServiceRecord(serviceName, uuid);
		AndroidBluetoothServerSocketWrapper androidSocket = new AndroidBluetoothServerSocketWrapper(
				socket);
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

	/**
	 * @return The wrapped BluetoothAdapter
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		return mAdapter;
	}

	@Override
	public IBluetoothDevice getRemoteDevice(String address) {
		BluetoothDevice remoteDevice = mAdapter.getRemoteDevice(address);
		return new AndroidBluetoothDeviceWrapper(remoteDevice);
	}

	@Override
	public void startDiscovery() {
		mAdapter.startDiscovery();
	}

	@Override
	public boolean isDeviceBonded(IBluetoothDevice device) {
		return (device instanceof AndroidBluetoothDeviceWrapper) ? ((AndroidBluetoothDeviceWrapper) device)
				.getDevice().getBondState() == BluetoothDevice.BOND_BONDED
				: mAdapter.getBondedDevices().contains(device);
	}

	/**
	 * Converts an Android BluetoothAdapterState to the States specified in the
	 * device-independent class {@link BluetoothState}.
	 * 
	 * @param androidAdapterState
	 * @return {@link BluetoothState}
	 */
	public static BluetoothState convertFromAndroidAdapterState(
			int androidAdapterState) {
		switch (androidAdapterState) {
		case BluetoothAdapter.STATE_ON:
			return BluetoothState.on;
		case BluetoothAdapter.STATE_TURNING_ON:
			return BluetoothState.switching_on;
		case BluetoothAdapter.STATE_TURNING_OFF:
			return BluetoothState.switching_off;
		case BluetoothAdapter.STATE_OFF:
		default:
			return BluetoothState.off;
		}

	}

}
