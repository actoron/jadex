package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.AndroidBluetoothDeviceWrapper;
import jadex.android.bluetooth.device.IBluetoothDevice;

/**
 * @author Julian Kalinowski
 */
public class AndroidBluetoothDeviceFactory implements IBluetoothDeviceFactory {

	private static IBluetoothDeviceFactory instance;
	
	private AndroidBluetoothDeviceFactory() {
	}

	/**
	 * @return An instance of {@link IBluetoothDeviceFactory}
	 */
	public static IBluetoothDeviceFactory getInstance() {
		if (instance == null) {
			instance = new AndroidBluetoothDeviceFactory();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see jadex.android.bluetooth.device.IBluetoothDeviceFactory#createBluetoothDevice(java.lang.String)
	 */
	@Override
	public IBluetoothDevice createBluetoothDevice(String address) {
		return new AndroidBluetoothDeviceWrapper(AndroidBluetoothAdapterFactory.getInstance()
				.getDefaultBluetoothAdapter().getRemoteDevice(address));
	}
}
