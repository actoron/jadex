package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.IBluetoothAdapter;

/**
 * @author  8kalinow
 */
public interface IBluetoothAdapterFactory {
	IBluetoothAdapter getDefaultBluetoothAdapter();
}