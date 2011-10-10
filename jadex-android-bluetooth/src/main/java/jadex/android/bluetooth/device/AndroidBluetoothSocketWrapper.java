package jadex.android.bluetooth.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class AndroidBluetoothSocketWrapper implements IBluetoothSocket {

	private BluetoothSocket mSocket;

	public AndroidBluetoothSocketWrapper(BluetoothSocket socket) {
		mSocket = socket;
	}
	
	@Override
	public IBluetoothDevice getRemoteDevice() {
		BluetoothDevice remoteDevice = mSocket.getRemoteDevice();
		return new AndroidBluetoothDevice(remoteDevice);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return mSocket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return mSocket.getOutputStream();
		}

	@Override
	public void close() throws IOException {
		mSocket.close();
	}

	@Override
	public void connect() throws IOException {
		mSocket.connect();
	}

}
