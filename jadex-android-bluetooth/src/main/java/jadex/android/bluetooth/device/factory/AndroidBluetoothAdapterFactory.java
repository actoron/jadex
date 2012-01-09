package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.AndroidBluetoothAdapterWrapper;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.exceptions.JadexBluetoothException;
import jadex.android.bluetooth.exceptions.JadexBluetoothRuntimeError;
import android.bluetooth.BluetoothAdapter;

/**
 * @author Julian Kalinowski
 */
public class AndroidBluetoothAdapterFactory implements IBluetoothAdapterFactory {

	private static AndroidBluetoothAdapterWrapper androidBluetoothAdapterWrapper;
	
	private static IBluetoothAdapterFactory instance;

	private AndroidBluetoothAdapterFactory() {
	}
	
	/**
	 * @return An instance of {@link IBluetoothAdapterFactory}
	 */
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
			BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
			if (defaultAdapter == null) {
				throw new JadexBluetoothRuntimeError("No default bluetooth Device found! (Running in an Emulator?)");
			}
			androidBluetoothAdapterWrapper = new AndroidBluetoothAdapterWrapper(
					defaultAdapter);
		}
		return androidBluetoothAdapterWrapper;
	}
}
