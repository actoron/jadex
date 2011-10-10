package jadex.android.bluetooth.device;

import android.bluetooth.BluetoothAdapter;

public class BluetoothAdapterFactory {

	private static AndroidBluetoothAdapterWrapper androidBluetoothAdapterWrapper;

	private BluetoothAdapterFactory() {
	}

	public static IBluetoothAdapter getBluetoothAdapter() {
		if (androidBluetoothAdapterWrapper == null) {
			androidBluetoothAdapterWrapper = new AndroidBluetoothAdapterWrapper(
					BluetoothAdapter.getDefaultAdapter());
		}
		return androidBluetoothAdapterWrapper;
	}
}
