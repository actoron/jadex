package jadex.android.bluetooth.connection;



public interface IBluetoothStateInformer {
	void addBluetoothStateListener(IBluetoothStateListener l);
	boolean removeBluetoothStateListener(IBluetoothStateListener l);
}
