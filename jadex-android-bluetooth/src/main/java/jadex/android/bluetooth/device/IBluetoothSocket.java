package jadex.android.bluetooth.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for Bluetooth Sockets
 * @author Julian Kalinowski
 */
public interface IBluetoothSocket extends java.io.Closeable {

	/**
	 * @return IBluetoothDevice this socket is connected to
	 */
	IBluetoothDevice getRemoteDevice();

	/**
	 * @return {@link InputStream} to send data to the remote device
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * @return {@link OutputStream} to receive data from remote device
	 * @throws IOException
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Connects to the remote Device
	 * @throws IOException
	 */
	void connect() throws IOException;
}
