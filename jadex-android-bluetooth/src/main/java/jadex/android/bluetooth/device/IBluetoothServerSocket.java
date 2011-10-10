package jadex.android.bluetooth.device;

import java.io.IOException;

public interface IBluetoothServerSocket extends java.io.Closeable {

	IBluetoothSocket accept() throws IOException;
}
