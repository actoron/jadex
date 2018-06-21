package jadex.android.bluetooth.exceptions;

import jadex.android.bluetooth.device.IBluetoothDevice;

public class AlreadyConnectedToDeviceException extends JadexBluetoothException {

	private static final long serialVersionUID = -1710258785613647151L;

	public AlreadyConnectedToDeviceException(IBluetoothDevice destinationDevice) {
		super("Device: " + destinationDevice.getAddress());
	}

}
