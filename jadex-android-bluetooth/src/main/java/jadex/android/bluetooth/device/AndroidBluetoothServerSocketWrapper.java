package jadex.android.bluetooth.device;

import java.io.IOException;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class AndroidBluetoothServerSocketWrapper implements IBluetoothServerSocket{

	private BluetoothServerSocket mSocket;

	public AndroidBluetoothServerSocketWrapper(BluetoothServerSocket socket) {
		mSocket = socket;
	}

	@Override
	public IBluetoothSocket accept() throws IOException {
		BluetoothSocket accept = mSocket.accept();
		return new AndroidBluetoothSocketWrapper(accept);
	}

	@Override
	public void close() throws IOException {
		mSocket.close();
	}

}
