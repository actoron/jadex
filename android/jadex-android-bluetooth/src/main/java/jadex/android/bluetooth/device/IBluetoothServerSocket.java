package jadex.android.bluetooth.device;

import java.io.IOException;

/**
 * Interface for BluetoothServerSockets
 * @author Julian Kalinowski
 *
 */
public interface IBluetoothServerSocket extends java.io.Closeable {

	/**
	 * Accept an incoming Connection.
	 * This call should block until an incoming connection is made.
	 * @return {@link IBluetoothSocket}
	 * @throws IOException
	 */
	IBluetoothSocket accept() throws IOException;
}
