package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.IBluetoothAdapter;

/**
 * @author Julian Kalinowski
 */
public interface IBluetoothAdapterFactory {
	/**
	 * @return Instance of {@link IBluetoothAdapter}
	 */
	IBluetoothAdapter getDefaultBluetoothAdapter();
}