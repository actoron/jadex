package jadex.android.bluetooth.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IBluetoothSocket extends java.io.Closeable {

	IBluetoothDevice getRemoteDevice();

	InputStream getInputStream() throws IOException;

	OutputStream getOutputStream() throws IOException;

	void connect() throws IOException;
}
