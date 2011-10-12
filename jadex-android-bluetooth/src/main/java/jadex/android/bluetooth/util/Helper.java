package jadex.android.bluetooth.util;

import jadex.android.bluetooth.device.factory.AndroidBluetoothAdapterFactory;
import jadex.android.bluetooth.device.factory.AndroidBluetoothDeviceFactory;
import jadex.android.bluetooth.device.factory.IBluetoothAdapterFactory;
import jadex.android.bluetooth.device.factory.IBluetoothDeviceFactory;

public class Helper {
	public static final String LOG_TAG = "android-bluetooth";
	
	public static IBluetoothAdapterFactory getBluetoothAdapterFactory() {
		return AndroidBluetoothAdapterFactory.getInstance();
	}
	
	public static IBluetoothDeviceFactory getBluetoothDeviceFactory() {
		return AndroidBluetoothDeviceFactory.getInstance();
	}
}
