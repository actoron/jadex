package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.AndroidBluetoothAdapterWrapper;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import android.bluetooth.BluetoothAdapter;

/**
 * @author  8kalinow
 */
public class AndroidBluetoothAdapterFactory implements IBluetoothAdapterFactory {

	private static AndroidBluetoothAdapterWrapper androidBluetoothAdapterWrapper;
	
	private static IBluetoothAdapterFactory instance;

	private AndroidBluetoothAdapterFactory() {
	}
	
	public static IBluetoothAdapterFactory getInstance() {
		if (instance == null) {
			instance = new AndroidBluetoothAdapterFactory();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see jadex.android.bluetooth.device.IBluetoothAdapterFactory#getDefaultBluetoothAdapter()
	 */
	@Override
	public IBluetoothAdapter getDefaultBluetoothAdapter() {
		if (androidBluetoothAdapterWrapper == null) {
			androidBluetoothAdapterWrapper = new AndroidBluetoothAdapterWrapper(
					BluetoothAdapter.getDefaultAdapter());
		}
		return androidBluetoothAdapterWrapper;
	}
}
