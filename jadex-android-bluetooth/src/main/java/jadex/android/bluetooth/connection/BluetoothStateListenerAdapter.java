package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter.BluetoothState;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice.BluetoothBondState;

/**
 * Adapter for {@link IBluetoothStateListener}.
 * @author Julian Kalinowski
 *
 */
public class BluetoothStateListenerAdapter implements IBluetoothStateListener{

	@Override
	public void bluetoothStateChanged(BluetoothState newState,
			BluetoothState oldState) {
	}

	@Override
	public void bluetoothDeviceFound(IBluetoothDevice device) {
	}

	@Override
	public void bluetoothDeviceBondStateChanged(IBluetoothDevice device,
			BluetoothBondState newState, BluetoothBondState oldState) {
	}

}
