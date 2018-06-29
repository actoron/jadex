package jadex.android.bluetooth.device;

import java.io.IOException;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

/**
 * A Wrapper to abstract from the Android BluetoothServerSocket Implementation. 
 * @author Julian Kalinowski
 */
public class AndroidBluetoothServerSocketWrapper implements IBluetoothServerSocket{

	private BluetoothServerSocket mSocket;

	/**
	 * Constructor
	 * @param socket {@link BluetoothServerSocket} to be wrapped.
	 */
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
