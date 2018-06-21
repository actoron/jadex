package jadex.android.bluetooth.connection;

/**
 * This Interface is implemented by classes that know about the State of the
 * BluetoothAdapter and provide their knowledge to others.
 * 
 * @author Julian Kalinowski
 * 
 */
public interface IBluetoothStateInformer {
	/**
	 * Adds a State Listener
	 * 
	 * @param l
	 */
	void addBluetoothStateListener(IBluetoothStateListener l);

	/**
	 * Removes a state Listener
	 * 
	 * @param l
	 * @return true, if the listener existed and was removed successfully, else
	 *         false.
	 */
	boolean removeBluetoothStateListener(IBluetoothStateListener l);
}
