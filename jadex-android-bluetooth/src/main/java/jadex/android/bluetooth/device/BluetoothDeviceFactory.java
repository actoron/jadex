package jadex.android.bluetooth.device;

public class BluetoothDeviceFactory {

	private BluetoothDeviceFactory() {

	}

	public static IBluetoothDevice createBluetoothDevice(String address) {
		return new AndroidBluetoothDeviceWrapper(BluetoothAdapterFactory
				.getBluetoothAdapter().getRemoteDevice(address));
	}
}
